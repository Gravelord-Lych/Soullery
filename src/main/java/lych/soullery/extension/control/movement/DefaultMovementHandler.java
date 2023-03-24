package lych.soullery.extension.control.movement;

import lych.soullery.network.MovementData;
import lych.soullery.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

public enum DefaultMovementHandler implements MovementHandler<MobEntity> {
    NORMAL(Float.MAX_VALUE),
    SPEED_LIMITED(0.28f);

    private final float limitedSpeed;

    DefaultMovementHandler(float limitedSpeed) {
        this.limitedSpeed = limitedSpeed;
    }

    @Override
    public void handleMovement(MobEntity operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl) {
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
        forwardSpeed = Math.min(forwardSpeed, limitedSpeed);
        leftSpeed = Math.min(leftSpeed, limitedSpeed);
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
            if (EntityUtils.canSwim(operatingMob)) {
                if (movement.jumping) {
                    operatingMob.setDeltaMovement(operatingMob.getDeltaMovement().add(0, 0.04 * operatingMob.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0));
                } else if (movement.shiftKeyDown) {
                    operatingMob.setDeltaMovement(operatingMob.getDeltaMovement().add(0, -0.04 * operatingMob.getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0));
                }
            }
        } else if (!movement.shiftKeyDown && movement.jumping) {
            jumpControl.jump();
        }
        if (movement.jumping || !operatingMob.isOnGround()) {
            return;
        }
        handleAutoJump(operatingMob, movement);
    }

    private static void handleAutoJump(MobEntity operatingMob, MovementData movement) {
        double wantedX = operatingMob.getLookAngle().x;
        double wantedY = operatingMob.getLookAngle().y;
        double wantedZ = operatingMob.getLookAngle().z;

        BlockPos pos = operatingMob.blockPosition();
        BlockState state = operatingMob.level.getBlockState(pos);
        Block block = state.getBlock();
        VoxelShape shape = state.getCollisionShape(operatingMob.level, pos);

        if (shouldJump(operatingMob, wantedX, wantedY, wantedZ, pos, block, shape)) {
            operatingMob.setJumping(true);
        }
    }

    private static boolean shouldJump(MobEntity operatingMob, double wantedX, double wantedY, double wantedZ, BlockPos pos, Block block, VoxelShape shape) {
        if (wantedY > operatingMob.maxUpStep) {
            if (wantedX * wantedX + wantedZ * wantedZ < Math.max(1, operatingMob.getBbWidth())) {
                return true;
            }
        }
        if (!shape.isEmpty() && operatingMob.getY() < shape.max(Direction.Axis.Y) + pos.getY()) {
            return !block.is(BlockTags.DOORS) && !block.is(BlockTags.FENCES);
        }
        return false;
    }
}
