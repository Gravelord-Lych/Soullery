package lych.soullery.item;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.extension.control.MindOperatorSynchronizer;
import lych.soullery.extension.control.dict.ControlDictionaries;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import static lych.soullery.item.ModItems.next;

public class MindOperatorItem extends Item {
    private static final Int2IntMap TIME_MAP = EntityUtils.intChoiceBuilder().range(1).value(400).range(2).value(1200).build();
    private static final Int2IntMap COOLDOWN_MAP = EntityUtils.intChoiceBuilder().range(1).value(200).range(2).value(60).build();
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

    @Override
    public Rarity getRarity(ItemStack stack) {
        Rarity rarity = super.getRarity(stack);
        if (tier > 1) {
            rarity = next(next(rarity));
        }
        return rarity;

    }

    public static int getTimeForTier(int tier) {
        return TIME_MAP.getOrDefault(tier, 400);
    }

    public static int getCooldownForTier(int tier) {
        return COOLDOWN_MAP.getOrDefault(tier, 200);
    }
}
