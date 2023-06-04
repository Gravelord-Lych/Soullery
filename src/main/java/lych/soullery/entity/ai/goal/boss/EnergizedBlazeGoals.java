package lych.soullery.entity.ai.goal.boss;

import lych.soullery.entity.ai.goal.IPhaseableGoal;
import lych.soullery.entity.monster.boss.EnergizedBlazeEntity;
import lych.soullery.util.ArrayUtils;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;

public final class EnergizedBlazeGoals {
    private EnergizedBlazeGoals() {}

    public static class FireballAttackGoal extends Goal {
        private final EnergizedBlazeEntity blaze;
        private final boolean small;
        private final int maxAttackStep;
        private final int chargeTime;
        private final int attackInterval;
        private final int timeBetweenAttacks;
        private final float deviationMultiplier;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public FireballAttackGoal(EnergizedBlazeEntity blaze, boolean small, int attackCount, int chargeTime, int attackInterval, int timeBetweenAttacks, float deviationMultiplier) {
            this.blaze = blaze;
            this.small = small;
            this.maxAttackStep = attackCount + 1;
            this.chargeTime = chargeTime;
            this.attackInterval = attackInterval;
            this.timeBetweenAttacks = timeBetweenAttacks;
            this.deviationMultiplier = deviationMultiplier;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return blaze.isConstantlyDoingFireballAttack() && blaze.canAttackTarget();
        }

        @Override
        public void start() {
            attackStep = 0;
        }

        @Override
        public void tick() {
            tickWithResult();
        }

        public boolean tickWithResult() {
            attackTime--;
            LivingEntity target = blaze.getTarget();
            if (target != null) {
                boolean canSeeTarget = blaze.getSensing().canSee(target);
                if (canSeeTarget) {
                    lastSeen = 0;
                } else {
                    lastSeen++;
                }

                double distSqr = blaze.distanceToSqr(target);
                if (distSqr < 4) {
                    if (!canSeeTarget) {
                        return false;
                    }
                    if (attackTime <= 0) {
                        attackTime = 20;
                        blaze.doHurtTarget(target);
                    }
                    blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1);
                } else if (distSqr < getFollowDistance() * getFollowDistance() && canSeeTarget) {
                    double tx = target.getX() - blaze.getX();
                    double ty = target.getY(0.5) - blaze.getY(0.5);
                    double tz = target.getZ() - blaze.getZ();
                    if (attackTime <= 0) {
                        attackStep++;
                        if (attackStep == 1) {
                            attackTime = chargeTime;
                            blaze.setCharged(true);
                        } else if (attackStep <= maxAttackStep) {
                            attackTime = attackInterval;
                        } else {
                            attackTime = timeBetweenAttacks;
                            attackStep = 0;
                            blaze.setCharged(false);
                            return false;
                        }

                        if (attackStep > 1) {
                            float deviation = MathHelper.sqrt(MathHelper.sqrt(distSqr)) * 0.5f * deviationMultiplier;
                            if (!blaze.isSilent()) {
                                blaze.playSound(ModSoundEvents.ENERGIZED_BLAZE_SHOOT.get(), (small ? 1 : 1.5f) * blaze.getSoundVolume(), 1);
                            }
                            AbstractFireballEntity fireball;
                            if (small) {
                                fireball = new SmallFireballEntity(blaze.level, blaze, tx + blaze.getRandom().nextGaussian() * deviation, ty, tz + blaze.getRandom().nextGaussian() * deviation);
                            } else {
                                fireball = new FireballEntity(blaze.level, blaze, tx + blaze.getRandom().nextGaussian() * deviation, ty, tz + blaze.getRandom().nextGaussian() * deviation);
                            }
                            fireball.setPos(fireball.getX(), blaze.getY(0.5) + 0.5, fireball.getZ());
                            blaze.level.addFreshEntity(fireball);
                        }
                    }

                    blaze.getLookControl().setLookAt(target, 10, 10);
                } else if (lastSeen < 5) {
                    blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1);
                }

                super.tick();
                return true;
            }
            return false;
        }

        private double getFollowDistance() {
            return blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }

        @Override
        public void stop() {
            blaze.setCharged(false);
            lastSeen = 0;
        }
    }

    public static class PhasedFireballAttackGoal extends EnergizedBlazePhasedGoal {
        private final FireballAttackGoal goal;
        private final int maxDelayTicks;
        private int delayTicks = -1;

        public PhasedFireballAttackGoal(EnergizedBlazeEntity blaze, boolean small, int attackCount, int chargeTime, int attackInterval, int maxDelayTicks, float deviationMultiplier) {
            super(blaze);
            goal = new FireballAttackGoal(blaze, small, attackCount, chargeTime, attackInterval, maxDelayTicks + 10, deviationMultiplier);
            this.maxDelayTicks = maxDelayTicks;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            return blaze.canAttackTarget() ? null : StopReason.NO_TARGET;
        }

        @Override
        public boolean isInterruptable() {
            return goal.isInterruptable();
        }

        @Override
        public void start() {
            super.start();
            goal.start();
        }

        @Override
        public void tick() {
            super.tick();
            if (delayTicks < 0 && !goal.tickWithResult()) {
                delayTicks = maxDelayTicks;
            }
            if (delayTicks > 0) {
                delayTicks--;
            }
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (delayTicks == 0) {
                return StopReason.NEXT_PHASE;
            }
            return blaze.canAttackTarget() ? null : StopReason.NO_TARGET;
        }

        @Override
        public void stop() {
            super.stop();
            goal.stop();
            delayTicks = -1;
        }

        @Override
        public EnumSet<Flag> getFlags() {
            return goal.getFlags();
        }
    }

    public static abstract class EnergizedBlazePhasedGoal extends Goal implements IPhaseableGoal {
        protected final EnergizedBlazeEntity blaze;

        public EnergizedBlazePhasedGoal(EnergizedBlazeEntity blaze, Flag... flags) {
            this.blaze = blaze;
            if (!ArrayUtils.isNullOrEmpty(flags)) {
                if (flags.length == 1) {
                    setFlags(EnumSet.of(flags[0]));
                } else {
                    setFlags(EnumSet.of(flags[0], Arrays.copyOfRange(flags, 1, flags.length)));
                }
            }
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (blaze. isConstantlyDoingFireballAttack()) {
                return StopReason.NO_TARGET;
            }
            return null;
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }
    }

    public static class FirestormGoal extends EnergizedBlazePhasedGoal {
        private final int maxDelayTicks;

        private int delayTicks;

        public FirestormGoal(EnergizedBlazeEntity blaze, int maxDelayTicks) {
            super(blaze, Flag.MOVE, Flag.LOOK);
            this.maxDelayTicks = maxDelayTicks;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (blaze.canAttackTarget()) {
                if (blaze.distanceToSqr(blaze.getTarget()) > EnergizedBlazeEntity.FIRESTORM_RANGE * EnergizedBlazeEntity.FIRESTORM_RANGE) {
                    return StopReason.NEXT_PHASE;
                }
                return null;
            }
            return StopReason.NO_TARGET;
        }

        @Override
        public void start() {
            super.start();
            blaze.firestorm();
            delayTicks = maxDelayTicks;
        }

        @Override
        public void tick() {
            super.tick();
            if (delayTicks > 0) {
                delayTicks--;
            }
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (blaze.canAttackTarget()) {
                return delayTicks > 0 ? null : StopReason.NEXT_PHASE;
            }
            return StopReason.NO_TARGET;
        }
    }
}
