package lych.soullery.extension.control.movement;

import lych.soullery.extension.control.IHandler;
import lych.soullery.network.MovementData;
import lych.soullery.util.DefaultValues;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface MovementHandler<T extends MobEntity> extends IHandler<T> {
    void handleMovement(T operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl, CompoundNBT data);

    static boolean updateAutoJump(MobEntity mob, double forwardX, double forwardZ, double autoJumpModifier, Vector2f moveVector) {
        forwardX *= autoJumpModifier;
        forwardZ *= autoJumpModifier;
        Vector3d pos = mob.position();
        Vector3d forwardPos = pos.add(forwardX, 0, forwardZ);
        Vector3d forwardVector = new Vector3d(forwardX, 0, forwardZ);
        float speed = mob.getSpeed();
        float lengthSqr = (float) forwardVector.lengthSqr();
        if (lengthSqr <= 0.001F) {
            float forwardSpeed = speed * moveVector.y;
            float leftSpeed = speed * moveVector.x;
            float sinRot = MathHelper.sin(mob.yRot * ((float)Math.PI / 180F));
            float cosRot = MathHelper.cos(mob.yRot * ((float)Math.PI / 180F));
            forwardVector = new Vector3d(forwardSpeed * cosRot - leftSpeed * sinRot, forwardVector.y, leftSpeed * cosRot + forwardSpeed * sinRot);
            lengthSqr = (float) forwardVector.lengthSqr();
            if (lengthSqr <= 0.001F) {
                return false;
            }
        }

        float lengthInv = MathHelper.fastInvSqrt(lengthSqr);
        Vector3d forwardInv = forwardVector.scale(lengthInv);
        Vector3d mobForward = mob.getForward();
        float hDot = (float) (mobForward.x * forwardInv.x + mobForward.z * forwardInv.z);

        if (!(hDot < -0.15F)) {
            ISelectionContext context = ISelectionContext.of(mob);
            BlockPos maxBlockPos = new BlockPos(mob.getX(), mob.getBoundingBox().maxY, mob.getZ());
            BlockState maxState = mob.level.getBlockState(maxBlockPos);
            if (maxState.getCollisionShape(mob.level, maxBlockPos, context).isEmpty()) {
                maxBlockPos = maxBlockPos.above();
                BlockState maxState2 = mob.level.getBlockState(maxBlockPos);
                if (maxState2.getCollisionShape(mob.level, maxBlockPos, context).isEmpty()) {
                    float jumpHeight = 1.2F;
                    if (mob.hasEffect(Effects.JUMP)) {
                        jumpHeight += (mob.getEffect(Effects.JUMP).getAmplifier() + 1) * 0.75F;
                    }
                    float moveLen = Math.max(speed * 7, 1 / lengthInv);
                    Vector3d to = forwardPos.add(forwardInv.scale(moveLen));
                    float bbWidth = mob.getBbWidth();
                    float bbHeight = mob.getBbHeight();
                    AxisAlignedBB bb = (new AxisAlignedBB(pos, to.add(0, bbHeight, 0))).inflate(bbWidth, 0, bbWidth);
                    Vector3d startTestPos = pos.add(0, 0.51, 0);
                    to = to.add(0, 0.51, 0);
                    Vector3d right = forwardInv.cross(new Vector3d(0, 1, 0)).scale(bbWidth * 0.5f);
                    Vector3d startLeft = startTestPos.subtract(right);
                    Vector3d toLeft = to.subtract(right);
                    Vector3d startRight = startTestPos.add(right);
                    Vector3d toRight = to.add(right);
                    Iterator<AxisAlignedBB> itr = mob.level.getCollisions(mob, bb, DefaultValues.alwaysTrue()).flatMap(shape -> shape.toAabbs().stream()).iterator();

                    float maxY = Float.MIN_VALUE;
                    while (itr.hasNext()) {
                        AxisAlignedBB cbb = itr.next();
                        if (cbb.intersects(startLeft, toLeft) || cbb.intersects(startRight, toRight)) {
                            maxY = (float) cbb.maxY;
                            Vector3d center = cbb.getCenter();
                            BlockPos centerPos = new BlockPos(center);

                            for (int i = 1; (float) i < jumpHeight; ++i) {
                                BlockPos above = centerPos.above(i);
                                BlockState aboveState = mob.level.getBlockState(above);
                                VoxelShape shape;
                                if (!(shape = aboveState.getCollisionShape(mob.level, above, context)).isEmpty()) {
                                    maxY = (float) shape.max(Direction.Axis.Y) + (float) above.getY();
                                    if (maxY - mob.getY() > jumpHeight) {
                                        return false;
                                    }
                                }

                                if (i > 1) {
                                    maxBlockPos = maxBlockPos.above();
                                    BlockState aboveAboveState = mob.level.getBlockState(maxBlockPos);
                                    if (!aboveAboveState.getCollisionShape(mob.level, maxBlockPos, context).isEmpty()) {
                                        return false;
                                    }
                                }
                            }
                            break;
                        }
                    }

                    if (maxY != Float.MIN_VALUE) {
                        float yDelta = (float) (maxY - mob.getY());
                        return !(yDelta <= 0.5f) && !(yDelta > jumpHeight);
                    }
                }
            }
        }
        return false;
    }
}
