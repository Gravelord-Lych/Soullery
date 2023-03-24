package lych.soullery.entity.ai.goal.boss;

import lych.soullery.entity.ai.goal.IPhaseableGoal;
import lych.soullery.entity.monster.boss.GiantXEntity;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;

import static lych.soullery.util.EntityUtils.checkGoalInstantiationServerside;

public final class GiantXGoals {
    private GiantXGoals() {
        throw new UnsupportedOperationException("Instantiate its static inner classes");
    }

    public static class RushAttackGoal extends Goal implements IPhaseableGoal {
        private final GiantXEntity giant;
        private final ServerWorld level;
        private final DoubleSupplier speedModifierSupplier;
        private LivingEntity target;
        @Nullable
        private Vector3d attackPos;

        public RushAttackGoal(GiantXEntity giant, DoubleSupplier speedModifierSupplier) {
            checkGoalInstantiationServerside(giant);
            this.giant = giant;
            this.level = (ServerWorld) giant.level;
            this.speedModifierSupplier = speedModifierSupplier;
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            return hasTarget() ? null : StopReason.NO_TARGET;
        }

        private boolean hasTarget() {
            LivingEntity target = giant.getTarget();
            if (target != null && target.isAlive()) {
                this.target = target;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            for (int i = 0; i < 20; i++) {
                attackPos = generateAttackPos();
                BlockRayTraceResult result = level.clip(new RayTraceContext(
                        giant.getEyePosition(1),
                        new Vector3d(attackPos.x, giant.getEyeY(), attackPos.z),
                        RayTraceContext.BlockMode.COLLIDER,
                        RayTraceContext.FluidMode.NONE,
                        giant));
                if (result.getBlockPos().distSqr(new BlockPos(giant.getEyePosition(1))) >= new Vector3d(attackPos.x, giant.getEyeY(), attackPos.z).distanceToSqr(giant.getEyePosition(1)) || result.getType() == RayTraceResult.Type.MISS) {
                    break;
                }
                attackPos = null;
            }
            if (attackPos != null) {
                giant.setRushing(true);
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (attackPos != null) {
                giant.getNavigation().moveTo(attackPos.x, attackPos.y, attackPos.z, speedModifierSupplier.getAsDouble());
                if (giant.getTarget() != null) {
                    giant.getLookControl().setLookAt(giant.getTarget(), 30, 30);
                }
                giant.getLookControl().lock();
            }
        }

        private Vector3d generateAttackPos() {
            Vector3d pos = giant.position();
            Vector3d targetPos = target.position();
            Vector3d vectorToTarget = pos.vectorTo(targetPos);
            return pos.add(vectorToTarget.normalize().scale(getAttackDistance()));
        }

        private double getAttackDistance() {
            return 16 + giant.getRandom().nextDouble() * 20;
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        @Override
        public void stop() {
            super.stop();
            giant.getLookControl().unlock();
            giant.setRushing(false);
            giant.setJumped(false);
            attackPos = null;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (giant.getTarget() == null || !giant.getTarget().isAlive()) {
                return StopReason.NO_TARGET;
            }
            if (attackPos == null || giant.getNavigation().isDone()) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }
    }

    public static class JumpAttackGoal extends Goal implements IPhaseableGoal {
        public static final double JUMP_ATTACK_MAX_DISTANCE = 25;
        public static final double JUMP_TO_TARGET_DISTANCE = 15;

        private final GiantXEntity giant;
        private int jumpCooldown;

        public JumpAttackGoal(GiantXEntity giant) {
            checkGoalInstantiationServerside(giant);
            this.giant = giant;
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (!giant.isOnGround() || giant.isJumped()) {
                return StopReason.NEXT_PHASE;
            }
            LivingEntity target = giant.getTarget();
            if (!EntityUtils.isAlive(target)) {
                return StopReason.NO_TARGET;
            }
            return giant.distanceToSqr(target) <= JUMP_ATTACK_MAX_DISTANCE * JUMP_ATTACK_MAX_DISTANCE ? null : StopReason.NEXT_PHASE;

        }

        @Override
        public void start() {
            super.start();
            giant.setFalling(true);
            giant.getJumpControl().jump();
            if (giant.getTarget() != null && giant.distanceToSqr(giant.getTarget()) >= JUMP_TO_TARGET_DISTANCE * JUMP_TO_TARGET_DISTANCE) {
                float rot = (float) (giant.yRot * Math.PI / 180F);
                double power = giant.distanceTo(giant.getTarget()) / JUMP_TO_TARGET_DISTANCE * 0.2;
                giant.setDeltaMovement(giant.getDeltaMovement().add(-MathHelper.sin(rot) * power, 0, MathHelper.cos(rot) * power));
            }
            EntityUtils.addParticlesAroundSelfServerside(giant, (ServerWorld) giant.level, ParticleTypes.LARGE_SMOKE, 40);
            jumpCooldown = 55 + giant.getRandom().nextInt(10);
        }

        @Override
        public void tick() {
            super.tick();
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (giant.getTarget() == null || !giant.getTarget().isAlive()) {
                return StopReason.NO_TARGET;
            }
            if (jumpCooldown <= 0) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }

        @Override
        public void stop() {
            super.stop();
            giant.setJumped(true);
        }
    }
}
