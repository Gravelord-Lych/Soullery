package lych.soullery.extension.control.movement;

import lych.soullery.network.MovementData;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public enum SquidMovementHandler implements MovementHandler<SquidEntity> {
    INSTANCE;

    private static final float SPEED = 0.2f;
    private static final float SPEED_Y = 0.016f;

    @Override
    public void handleMovement(SquidEntity operatingSquid, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl, CompoundNBT data) {
        float forwardSpeed = movement.forwardImpulse;
        float leftSpeed = -movement.leftImpulse;
        if (movement.shiftKeyDown) {
            forwardSpeed = leftSpeed = 0;
        }
        float x, y = 0, z;
        float angle = (float) MathHelper.atan2(leftSpeed, forwardSpeed);

        x = MathHelper.cos(angle) * SPEED;
        z = MathHelper.sin(angle) * SPEED;

        if (movement.jumping) {
            y = SPEED_Y;
        } else if (movement.shiftKeyDown) {
            y = -SPEED_Y;
        }

        operatingSquid.setMovementVector(x, y, z);
    }
}
