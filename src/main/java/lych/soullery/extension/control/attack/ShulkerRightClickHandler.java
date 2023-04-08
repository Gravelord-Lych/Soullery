package lych.soullery.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;

public class ShulkerRightClickHandler implements TargetNeededRightClickHandler<ShulkerEntity> {
    private int attackTime = 20;
    private int attackCooldown = 10;
    private LivingEntity cachedTarget;

    @Override
    public void handleRightClick(ShulkerEntity operatingShulker, LivingEntity target, ServerPlayerEntity player, CompoundNBT data) {
        if (attackCooldown <= 0) {
            operatingShulker.setRawPeekAmount(100);
            attackTime = 20;
            cachedTarget = target;
        }
    }

    @Override
    public void tick(ShulkerEntity operatingShulker, ServerPlayerEntity player, CompoundNBT data) {
        if (attackTime > 0) {
            attackTime--;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        LivingEntity target = cachedTarget;
        if (target == null) {
            return;
        }
        double distSqr = operatingShulker.distanceToSqr(target);
        if (distSqr < 20 * 20) {
            if (attackTime == 0) {
                operatingShulker.level.addFreshEntity(new ShulkerBulletEntity(operatingShulker.level, operatingShulker, target, operatingShulker.getAttachFace().getAxis()));
                operatingShulker.playSound(SoundEvents.SHULKER_SHOOT, 2, (operatingShulker.getRandom().nextFloat() - operatingShulker.getRandom().nextFloat()) * 0.2f + 1);
                attackCooldown = 50;
                attackTime = -1;
                operatingShulker.setRawPeekAmount(0);
            }
        } else {
            cachedTarget = null;
        }
    }
}
