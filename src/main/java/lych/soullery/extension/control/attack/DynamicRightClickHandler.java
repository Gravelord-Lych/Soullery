package lych.soullery.extension.control.attack;

import lych.soullery.Soullery;
import lych.soullery.entity.iface.ILaserAttacker;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.mixin.RangedAttackGoalAccessor;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;

public class DynamicRightClickHandler implements TargetNotNeededRightClickHandler<MobEntity> {
    private int laserCooldown = -1;
    private int attackCooldown = 10;
    private boolean crossbowCharging;
    private LivingEntity cachedLaserTarget;

    @Override
    public void handleRightClick(MobEntity operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        NoRightClickHandler.INSTANCE.handleRightClick(operatingMob, player, data);
    }

    @Override
    public void handleRightClick(MobEntity operatingMob, LivingEntity target, ServerPlayerEntity player, CompoundNBT data) {
        if (operatingMob.getMainHandItem().getItem() instanceof CrossbowItem && operatingMob instanceof ICrossbowUser) {
            if (!crossbowCharging) {
                operatingMob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(operatingMob, item -> item instanceof CrossbowItem));
                ((ICrossbowUser) operatingMob).setChargingCrossbow(true);
                crossbowCharging = true;
            } else {
                int ticksUsingItem = operatingMob.getTicksUsingItem();
                ItemStack useItem = operatingMob.getUseItem();
                if (ticksUsingItem >= CrossbowItem.getChargeDuration(useItem)) {
                    operatingMob.releaseUsingItem();
                    ((ICrossbowUser) operatingMob).setChargingCrossbow(false);

//                  Prevent NPE
                    EntityUtils.setTarget(operatingMob, target);
                    ((ICrossbowUser) operatingMob).performRangedAttack(target, 1);
                    EntityUtils.setTarget(operatingMob, null);

                    ItemStack itemInHand = operatingMob.getItemInHand(ProjectileHelper.getWeaponHoldingHand(operatingMob, item -> item instanceof CrossbowItem));
                    CrossbowItem.setCharged(itemInHand, false);
                    crossbowCharging = false;
                }
            }
        } else if (operatingMob instanceof ILaserAttacker) {
            if (attackCooldown > 0) {
                return;
            }
            laserCooldown = 10;
            cachedLaserTarget = target;
            ((ILaserAttacker) operatingMob).setAttacking(true);
        } else if (operatingMob instanceof IRangedAttackMob) {
            if (operatingMob.getMainHandItem().getItem() instanceof BowItem) {
                if (attackCooldown > 0) {
                    return;
                }
                if (operatingMob.isUsingItem()) {
                    int ticksUsingItem = operatingMob.getTicksUsingItem();
                    if (ticksUsingItem >= 20) {
                        operatingMob.stopUsingItem();
                        ((IRangedAttackMob) operatingMob).performRangedAttack(target, BowItem.getPowerForTime(ticksUsingItem));
                        attackCooldown = 20;
                        operatingMob.setAggressive(false);
                    }
                } else {
                    operatingMob.setAggressive(true);
                    operatingMob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(operatingMob, item -> item instanceof BowItem));
                }
            } else {
                Optional<RangedAttackGoal> goal = EntityUtils.findRangedAttackGoal(operatingMob);
                Optional<RangedAttackGoalAccessor> accessor = goal.map(goalIn -> ((RangedAttackGoalAccessor) goalIn));
                operatingMob.setTarget(target);
                if (goal.filter(Goal::canUse).isPresent()) {
                    if (attackCooldown > 0) {
                        return;
                    }
                    int min = accessor.map(RangedAttackGoalAccessor::getAttackIntervalMin).orElse(10);
                    int max = accessor.map(RangedAttackGoalAccessor::getAttackIntervalMax).orElse(30);
                    ((IRangedAttackMob) operatingMob).performRangedAttack(target, 1);
                    operatingMob.setTarget(null);
                    attackCooldown = (min + max) / 2;
                } else {
                    TargetNotNeededRightClickHandler.super.handleRightClick(operatingMob, target, player, data);
                }
            }
        } else {
            TargetNotNeededRightClickHandler.super.handleRightClick(operatingMob, target, player, data);
        }
    }

    @Override
    public void tick(MobEntity operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (laserCooldown > 0) {
            laserCooldown--;
        } else if (laserCooldown == 0 && operatingMob instanceof ILaserAttacker) {
            if (cachedLaserTarget != null) {
                ILaserAttacker laserAttacker = (ILaserAttacker) operatingMob;
                laserAttacker.performLaserAttack(cachedLaserTarget, laserAttacker.getTargetPosition(cachedLaserTarget), 1);
                laserAttacker.setAttacking(false);
                cachedLaserTarget = null;
                attackCooldown = 20;
            } else {
                Soullery.LOGGER.warn(MindOperator.MARKER, "Unable to find cachedTarget for {}", operatingMob.getType().getRegistryName());
            }
            laserCooldown = -1;
        }
    }

    @Override
    public void saveTo(CompoundNBT data) {
        data.putInt("AttackCooldown", attackCooldown);
    }

    @Override
    public void loadFrom(CompoundNBT data) {
        attackCooldown = data.getInt("AttackCooldown");
    }
}
