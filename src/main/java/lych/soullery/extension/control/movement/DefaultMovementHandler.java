package lych.soullery.extension.control.movement;

import lych.soullery.network.MovementData;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

public enum DefaultMovementHandler implements MovementHandler<MobEntity> {
    NORMAL(Float.MAX_VALUE),
    SPEED_LIMITED(0.28f),
    WATER(0.8f);

    private final float limitedSpeed;

    DefaultMovementHandler(float limitedSpeed) {
        this.limitedSpeed = limitedSpeed;
    }

    @Override
    public void handleMovement(MobEntity operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl, CompoundNBT data) {
        handleMove(operatingMob, movement);
        if (jumpControl != null) {
            handleJump(operatingMob, movement, jumpControl);
        }
    }

    private void handleMove(MobEntity operatingMob, MovementData movement) {
        float forwardSpeed = (float) (operatingMob.getAttributeValue(Attributes.MOVEMENT_SPEED) * movement.forwardImpulse);
        float leftSpeed = (float) (operatingMob.getAttributeValue(Attributes.MOVEMENT_SPEED) * movement.leftImpulse);
        if (movement.shiftKeyDown) {
            forwardSpeed = leftSpeed = 0;
        }
        forwardSpeed = MathHelper.clamp(forwardSpeed, -limitedSpeed, limitedSpeed);
        leftSpeed = MathHelper.clamp(leftSpeed, -limitedSpeed, limitedSpeed);
        operatingMob.setSpeed(Math.max(Math.abs(forwardSpeed), Math.abs(leftSpeed)));
        operatingMob.setZza(forwardSpeed);
        operatingMob.setXxa(leftSpeed);
    }

    private static void handleJump(MobEntity operatingMob, MovementData movement, JumpController jumpControl) {
        boolean inLiquid = operatingMob.isInWater() && operatingMob.getFluidHeight(FluidTags.WATER) > operatingMob.getFluidJumpThreshold() || operatingMob.isInLava();
        if (!inLiquid && !operatingMob.isOnGround()) {
            return;
        }
        if (inLiquid) {
            if (EntityUtils.canSwim(operatingMob) || EntityUtils.isWaterMob(operatingMob)) {
                double yMul = EntityUtils.isWaterMob(operatingMob) ? 0.4 : 1;
                if (movement.jumping) {
                    operatingMob.setDeltaMovement(operatingMob.getDeltaMovement().add(0, 0.04 * yMul * operatingMob.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0));
                } else if (movement.shiftKeyDown) {
                    operatingMob.setDeltaMovement(operatingMob.getDeltaMovement().add(0, -0.04 * yMul * operatingMob.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0));
                }
            }
        } else if (!movement.shiftKeyDown && movement.jumping) {
            jumpControl.jump();
        }
    }
}
