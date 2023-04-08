package lych.soullery.item;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.extension.control.MindOperatorSynchronizer;
import lych.soullery.extension.control.SoulManager;
import lych.soullery.extension.control.dict.ControlDictionaries;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import java.util.Optional;

public class MindOperatorItem extends Item implements IUpgradeableItem, ISkillPerformable {
    private static final Int2IntMap TIME_MAP = EntityUtils.intChoiceBuilder().range(1).value(400).range(2).value(1200).range(3).value(2400).build();
    private static final Int2IntMap COOLDOWN_MAP = EntityUtils.intChoiceBuilder().range(1).value(200).range(2, 3).value(60).build();
    private final int tier;

    public MindOperatorItem(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (player.getCooldowns().isOnCooldown(this) || tier < 2 || MindOperatorSynchronizer.getOperatingMob(player) != null) {
            return super.use(world, player, hand);
        }
        EntityRayTraceResult ray = EntityUtils.getEntityRayTraceResult(player, 12 + (tier - 2) * 6);
        if (ray != null && ray.getEntity() instanceof MobEntity) {
            ActionResultType type = tryControl(player, ray.getEntity());
            if (type != null) {
                player.playSound(ModSoundEvents.MIND_OPERATE.get(), 1, 1);
                return new ActionResult<>(type, player.getItemInHand(hand));
            }
        }
        return super.use(world, player, hand);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return super.interactLivingEntity(stack, player, entity, hand);
        }
        ActionResultType type = tryControl(player, entity);
        if (type != null) {
            return type;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Nullable
    private ActionResultType tryControl(PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity) {
            if (player.level.isClientSide()) {
                return ActionResultType.SUCCESS;
            }
            if (SoulEnergies.cost(player, MindOperator.SE_COST)) {
                MindOperator<? super MobEntity> operator = MindOperator.cast(ControlDictionaries.MIND_OPERATOR.control((MobEntity) entity, player, getTimeForTier(tier)));
                if (operator == null) {
                    EntityUtils.addParticlesAroundSelfServerside(entity, (ServerWorld) player.level, ParticleTypes.LARGE_SMOKE, 12);
                    return null;
                } else {
                    operator.setTier(tier);
                }
                return ActionResultType.CONSUME;
            }
        }
        return null;
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return tier > 1;
    }

    public static int getTimeForTier(int tier) {
        return TIME_MAP.getOrDefault(tier, 400);
    }

    public static int getCooldownForTier(int tier) {
        return COOLDOWN_MAP.getOrDefault(tier, 200);
    }

    public static boolean hasExtendedControlRange(int tier) {
        return tier > 2;
    }

    @Override
    public boolean canUpgrade(ItemStack stack) {
        return getUpgraded().isPresent();
    }

    @Override
    public ItemStack upgraded(ItemStack old) {
        return new ItemStack(getUpgraded().orElseThrow(AssertionError::new));
    }

    @NotNull
    private Optional<MindOperatorItem> getUpgraded() {
        return MindOperator.MIND_OPERATORS.get().stream().filter(item -> item.tier == tier + 1).findFirst();
    }

    @Override
    public boolean perform(ItemStack stack, ServerPlayerEntity player) {
        if (!(stack.getItem() instanceof MindOperatorItem) || ((MindOperatorItem) stack.getItem()).tier < 3) {
            return false;
        }
        MobEntity operatingMob = MindOperatorSynchronizer.getOperatingMob(player);
        if (operatingMob == null) {
            return false;
        }
        SoulManager.get(player.getLevel()).remove(MindOperatorSynchronizer.getOperatingMob(player), MindOperator.class);
        Vector3d playerPos = player.position();
        player.teleportTo(operatingMob.getX(), operatingMob.getY(), operatingMob.getZ());
        operatingMob.teleportTo(playerPos.x, playerPos.y, playerPos.z);
        return true;
    }
}
