package lych.soullery.extension.control.movement;

import lych.soullery.network.MovementData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public enum FlyerMovementHandler implements MovementHandler<MobEntity> {
    NORMAL(false),
    SPEED_INDEPENDENT(true);

    private final boolean speedIndependent;

    FlyerMovementHandler(boolean speedIndependent) {
        this.speedIndependent = speedIndependent;
    }

    @Override
    public void handleMovement(MobEntity operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl) {
        operatingMob.setNoGravity(true);
        float speed = (float) operatingMob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        if (!operatingMob.isOnGround() && speedIndependent) {
            speed = (float) operatingMob.getAttributeValue(Attributes.FLYING_SPEED);
        }
        float forwardSpeed = speed * movement.forwardImpulse;
        float leftSpeed = (float) (operatingMob.getAttributeValue(Attributes.MOVEMENT_SPEED) * movement.leftImpulse);
        operatingMob.setSpeed(forwardSpeed);
        operatingMob.setXxa(leftSpeed);
        if (movement.jumping != movement.shiftKeyDown) {
            operatingMob.setYya(movement.jumping ? speed : -speed);
        } else {
            operatingMob.setYya(0);
        }
        operatingMob.setNoGravity(false);
    }
}
