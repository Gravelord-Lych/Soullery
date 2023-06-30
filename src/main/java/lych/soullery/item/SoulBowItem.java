package lych.soullery.item;

import com.google.common.base.Preconditions;
import lych.soullery.Soullery;
import lych.soullery.api.ItemSEContainer;
import lych.soullery.api.capability.ISoulEnergyStorage;
import lych.soullery.api.event.ArrowSpawnEvent;
import lych.soullery.entity.projectile.SoulArrowEntity;
import lych.soullery.util.InventoryUtils;
import lych.soullery.util.SoulEnergies;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.Predicate;

public class SoulBowItem extends BowItem {
    public static final ResourceLocation PULLING = Soullery.prefix("soul_bow_pulling");
    public static final ResourceLocation PULL = Soullery.prefix("soul_bow_pull");

    private static final int SE_COST_PER_SHOOT = 100;

    public SoulBowItem(Properties properties) {
        super(properties.fireResistant().durability(384));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void releaseUsing(ItemStack bow, World world, LivingEntity entity, int useItemRemainingTicks) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            NonNullList<ItemStack> arrows = InventoryUtils.get(player.inventory, ModItems.SOUL_ARROW);
            ItemStack arrowOrEmpty = arrows.isEmpty() ? ItemStack.EMPTY : arrows.get(0);

            boolean infinity = player.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0;
            int energy = SoulEnergies.getSEContainers(player).stream().mapToInt(stack -> SoulEnergies.of(stack).map(ISoulEnergyStorage::getSoulEnergyStored).orElse(0)).sum();

            int charge = getUseDuration(bow) - useItemRemainingTicks;
            charge = ForgeEventFactory.onArrowLoose(bow, world, player, charge, energy >= SE_COST_PER_SHOOT || infinity);

            if (charge < 0) {
                return;
            }
            if (arrowOrEmpty.isEmpty() && energy < SE_COST_PER_SHOOT) {
                return;
            }

            float power = getPowerForTime(charge);

            if (power >= 0.1) {
                boolean noArrowCost = player.abilities.instabuild || arrowOrEmpty.isEmpty();
                if (!world.isClientSide) {
                    SoulArrowEntity arrow = new SoulArrowEntity(world, player);
                    arrow.shootFromRotation(player, player.xRot, player.yRot, 0, power * 3, 1);

                    if (power == 1) {
                        arrow.setCritArrow(true);
                    }

                    int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow);
                    if (powerLevel > 0) {
                        arrow.setBaseDamage(arrow.getBaseDamage() + powerLevel * 0.5 + 0.5);
                    }

                    int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bow);
                    if (punchLevel > 0) {
                        arrow.setKnockback(punchLevel);
                    }

                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bow) > 0) {
                        arrow.setSecondsOnFire(100);
                        ((IEntityMixin) arrow).setOnSoulFire(true);
                    }

                    bow.hurtAndBreak(1, player, playerIn -> playerIn.broadcastBreakEvent(player.getUsedItemHand()));

                    if (noArrowCost) {
                        arrow.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    }
                    MinecraftForge.EVENT_BUS.post(new ArrowSpawnEvent(bow, player, arrow));
                    world.addFreshEntity(arrow);
                }

                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

                if (!noArrowCost && !player.abilities.instabuild) {
                    arrowOrEmpty.shrink(1);
                    if (arrowOrEmpty.isEmpty()) {
                        player.inventory.removeItem(arrowOrEmpty);
                    }
                } else if (!infinity) {
                    costSE(SoulEnergies.getSEContainers(player));
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() == ModItems.SOUL_ARROW;
    }

    private static void costSE(NonNullList<ItemStack> seContainers) {
        int seToCost = SE_COST_PER_SHOOT;
        for (ItemStack stack : seContainers) {
            if (stack.getItem() instanceof ItemSEContainer && ((ItemSEContainer) stack.getItem()).isTransferable(stack)) {
                final int nextCost = seToCost;
                seToCost -= SoulEnergies.of(stack).map(ses -> ses.extractSoulEnergy(nextCost)).orElseThrow(() -> new IllegalStateException("Invalid ItemStack"));
            }
            if (seToCost <= 0) {
                break;
            }
        }
        Preconditions.checkState(seToCost == 0, String.format("SE cost was %d. Expected: %d", 100 - seToCost, 100));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasProjectile = !player.getProjectile(stack).isEmpty();
        ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(stack, world, player, hand, hasProjectile);
        if (ret != null) {
            return ret;
        }
        if (!player.abilities.instabuild && !hasProjectile) {
            int energy = SoulEnergies.getSEContainers(player).stream().mapToInt(stackIn -> SoulEnergies.of(stackIn).map(ISoulEnergyStorage::getSoulEnergyStored).orElse(0)).sum();
            if (energy >= SE_COST_PER_SHOOT) {
                player.startUsingItem(hand);
                return ActionResult.success(stack);
            }
            return ActionResult.fail(stack);
        } else {
            player.startUsingItem(hand);
            return ActionResult.consume(stack);
        }
    }
}
