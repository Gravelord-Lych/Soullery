package lych.soullery.entity.ai.goal.boss;

import lych.soullery.api.shield.ISharedShield;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.IPhaseableGoal;
import lych.soullery.entity.ai.goal.LaserAttackGoal;
import lych.soullery.entity.functional.SoulBoltEntity;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.monster.RobotEntity;
import lych.soullery.entity.monster.boss.Meta08Entity;
import lych.soullery.extension.shield.SharedShield;
import lych.soullery.util.*;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static lych.soullery.util.EntityUtils.checkGoalInstantiationServerside;

public final class Meta08Goals {
    private Meta08Goals() {
        throw new UnsupportedOperationException("Instantiate its static inner classes");
    }

    public static class Meta08LaserAttackGoal extends LaserAttackGoal<Meta08Entity> implements IPhaseableGoal {
        private final int minAttackTime;
        private final int maxAttackTime;
        private final DoubleSupplier skipProbability;
        protected int attackTimeLimit;
        protected int attackTime;

        public Meta08LaserAttackGoal(Meta08Entity meta8, double speedModifier, IntSupplier attackInterval, int minAttackTime, int maxAttackTime, float attackRadius) {
            this(meta8, speedModifier, attackInterval, minAttackTime, maxAttackTime, attackRadius, () -> 0);
        }

        public Meta08LaserAttackGoal(Meta08Entity meta8, double speedModifier, IntSupplier attackInterval, int minAttackTime, int maxAttackTime, float attackRadius, DoubleSupplier skipProbability) {
            super(meta8, speedModifier, attackInterval, attackRadius);
            this.minAttackTime = minAttackTime;
            this.maxAttackTime = maxAttackTime;
            this.skipProbability = skipProbability;
        }

        public Meta08LaserAttackGoal(Meta08Entity meta8, double speedModifier, IntSupplier attackIntervalMin, IntSupplier attackIntervalMax, int minAttackTime, int maxAttackTime, float attackRadius, DoubleSupplier skipProbability) {
            super(meta8, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
            this.maxAttackTime = maxAttackTime;
            this.minAttackTime = minAttackTime;
            this.skipProbability = skipProbability;
        }

        public Meta08LaserAttackGoal(Meta08Entity meta8, double speedModifier, IntSupplier attackIntervalMin, IntSupplier attackIntervalMax, int attackThreshold, int minAttackTime, int maxAttackTime, float attackRadius, DoubleSupplier skipProbability) {
            super(meta8, speedModifier, attackIntervalMin, attackIntervalMax, attackThreshold, attackRadius);
            this.maxAttackTime = maxAttackTime;
            this.minAttackTime = minAttackTime;
            this.skipProbability = skipProbability;
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (mob.getRandom().nextDouble() < skipProbability.getAsDouble()) {
                return StopReason.NEXT_PHASE;
            }
            LivingEntity target = mob.getTarget();
            if (target != null && target.isAlive()) {
                this.target = target;
                return null;
            }
            return StopReason.NO_TARGET;
        }

        @Override
        public void start() {
            super.start();
            attackTimeLimit = minAttackTime + mob.getRandom().nextInt(maxAttackTime - minAttackTime + 1);
        }

        @Override
        protected void mobAttack(Vector3d trueTargetPosition, float power) {
            super.mobAttack(trueTargetPosition, power);
            attackTime++;
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        @Override
        public void stop() {
            super.stop();
            attackTime = 0;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (target == null || !target.isAlive() || !mob.canAttack(target)) {
                return StopReason.NO_TARGET;
            }
            if (attackTime >= attackTimeLimit) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }
    }

    public static class SummonRobotWhenLowHealthGoal extends Goal implements IPhaseableGoal {
        private final Meta08Entity meta8;
        private final ServerWorld level;
        private final List<Vector3d> summonPositions = new ArrayList<>();
        private final double speedModifier;
        private final double attackRadiusSqr;
        private LivingEntity target;
        private int attackWarmupTime = 60;
        private boolean canContinueToUse = true;

        public SummonRobotWhenLowHealthGoal(Meta08Entity meta8, double speedModifier, double attackRadius) {
            checkGoalInstantiationServerside(meta8);
            this.meta8 = meta8;
            this.level = (ServerWorld) meta8.level;
            this.speedModifier = speedModifier;
            this.attackRadiusSqr = attackRadius * attackRadius;
            setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            LivingEntity target = meta8.getTarget();
            if (EntityUtils.isAlive(target)) {
                this.target = target;
//              Don't spawn too many robots.
                return level.getEntitiesOfClass(RobotEntity.class, meta8.getBoundingBox().inflate(Meta08Entity.SHIELD_RANGE)).size() <= 10 ? null : StopReason.NEXT_PHASE;
            }
            return StopReason.NO_TARGET;
        }

        @Override
        public void start() {
            super.start();
            attackWarmupTime = 60;
            canContinueToUse = true;
            fillSummonPositions();
        }

        @Override
        public void tick() {
            super.tick();
            if (meta8.distanceToSqr(target) <= attackRadiusSqr) {
                meta8.getNavigation().stop();
            } else {
                meta8.getNavigation().moveTo(target, speedModifier);
            }
            meta8.getLookControl().setLookAt(target, 30, 30);
            attackWarmupTime--;
            if (attackWarmupTime == 10) {
                meta8.prepareAttack();
                meta8.setAttacking(true);
            } else if (attackWarmupTime == 0) {
                for (Vector3d pos : summonPositions) {
                    RobotEntity robot = ModEntities.ROBOT.create(level);
                    if (robot != null) {
                        robot.setOwner(meta8);
                        robot.moveTo(pos.add(0, 0.5, 0));
                        robot.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(pos)), SpawnReason.MOB_SUMMONED, null, null);
                        level.addFreshEntityWithPassengers(robot);
                        EntityUtils.spawnAnimServerside(robot, level);
                        EntityUtils.addParticlesAroundSelf(meta8, Meta08Entity.SUMMON, 5, 50);
                    }
                }
                canContinueToUse = false;
            }
        }

        private void fillSummonPositions() {
            summonPositions.clear();
            BlockPos pos = meta8.blockPosition();
            int distance = (int) Math.rint(meta8.distanceTo(target));
            Vector3d srcPos = Vector3d.atBottomCenterOf(pos);
            Vector3d targetPos = Vector3d.atBottomCenterOf(pos).add(0, 0, MathHelper.clamp(distance * 0.5, 5, 10));
//          Summon 8 robots instead of 6 if creative.
            for (int degree = 0; degree < 360; degree += meta8.isCreative() ? 45 : 60) {
                Vector3d rotated = Vectors.rotateTo(targetPos, srcPos, Math.toRadians(degree), true);
                summonPositions.add(new Vector3d(rotated.x, PositionCalculators.smart(new BlockPos(rotated), level, pos), rotated.z));
            }
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            LivingEntity target = meta8.getTarget();
            if (target == null || !target.isAlive()) {
                meta8.setAttacking(false);
                return StopReason.NO_TARGET;
            } else if (attackWarmupTime < 0 || !canContinueToUse) {
                meta8.setAttacking(false);
                return StopReason.NEXT_PHASE;
            }
            return null;
        }

        @Override
        public void stop() {
            super.stop();
            attackWarmupTime = 60;
        }
    }

    public static class ShieldRobotsGoal extends Goal implements IPhaseableGoal {
        private static final Supplier<ISharedShield> DEFENSIVE_SHARED_SHIELD_SUPPLIER = () -> new SharedShield(3, 75, 40, 1);
        private static final Supplier<ISharedShield> SHARED_SHIELD_SUPPLIER = () -> new SharedShield(2, 50);

        private final Meta08Entity meta8;
        private final Supplier<ISharedShield> shieldSupplier;

        public ShieldRobotsGoal(Meta08Entity meta8) {
            this(meta8, () -> meta8.getTrait() != null && meta8.getTrait().enhancesShield() ? DEFENSIVE_SHARED_SHIELD_SUPPLIER.get() : SHARED_SHIELD_SUPPLIER.get());
        }

        public ShieldRobotsGoal(Meta08Entity meta8, Supplier<ISharedShield> shieldSupplier) {
            checkGoalInstantiationServerside(meta8);
            this.meta8 = meta8;
            this.shieldSupplier = shieldSupplier;
            setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (meta8.getSharedShield() != null) {
                return StopReason.NEXT_PHASE;
            }
            LivingEntity target = meta8.getTarget();
            return EntityUtils.isAlive(target) ? null : StopReason.NO_TARGET;
        }

        @Override
        public void start() {
            super.start();
            ISharedShield shield = shieldSupplier.get();
            meta8.setSharedShield(shield);
            if (meta8.getTrait() != null && meta8.getTrait().enhancesShield()) {
                meta8.playSound(ModSoundEvents.DEFENSIVE_META8_SHARE_SHIELD.get(), 10, 1);
            } else {
                meta8.playSound(ModSoundEvents.META8_SHARE_SHIELD.get(), 10, 1);
            }
            meta8.prepareAttack();
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            return meta8.getTarget() == null && meta8.getSharedShield() == null ? StopReason.NO_TARGET : StopReason.NEXT_PHASE;
        }
    }

    public static class SpawnLightningGoal extends Goal implements IPhaseableGoal {
        private static final double CREATIVE_KNOCKBACK_STRENGTH = 0.4;
        private static final float DAMAGE = 10;
        private final Meta08Entity meta8;
        private final ServerWorld level;
        private final double speedModifier;
        private final double attackRadiusSqr;
        private final IntSupplier maxInterval;
        private final int lightningSpacing;
        private final int lightningCount;
        private final IntSupplier maxAttackTimeSup;
        private final Queue<Vector3d> lightningPositions = new ArrayDeque<>();
        private int lightningWarmupTime;
        private int lightningInterval;
        private int attackTime;
        private LivingEntity target;

        public SpawnLightningGoal(Meta08Entity meta8, double speedModifier, double attackRadius, IntSupplier maxInterval, int lightningSpacing, int lightningCount, IntSupplier maxAttackTimeSup) {
            checkGoalInstantiationServerside(meta8);
            this.meta8 = meta8;
            this.level = (ServerWorld) meta8.level;
            this.speedModifier = speedModifier;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.maxInterval = maxInterval;
            this.lightningSpacing = lightningSpacing;
            this.lightningCount = lightningCount;
            this.maxAttackTimeSup = maxAttackTimeSup;

            lightningWarmupTime = maxInterval.getAsInt() * 8;
            lightningInterval = maxInterval.getAsInt();
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            LivingEntity target = meta8.getTarget();
            if (EntityUtils.isAlive(target)) {
                this.target = target;
                return null;
            }
            return StopReason.NO_TARGET;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void tick() {
            super.tick();
            if (meta8.distanceToSqr(target) <= attackRadiusSqr) {
                meta8.getNavigation().stop();
            } else {
                meta8.getNavigation().moveTo(target, speedModifier);
            }
            meta8.getLookControl().setLookAt(target, 60, 60);
            if (lightningWarmupTime > 0){
                lightningWarmupTime--;
            } else if (lightningWarmupTime == 0) {
                meta8.prepareAttack();
                meta8.setAttacking(true);
                updateLightningPositions();
                lightningWarmupTime = -1;
            } else {
                if (lightningPositions.isEmpty()) {
                    attackTime++;
                    lightningWarmupTime = maxInterval.getAsInt() * 4;
                    return;
                }
                if (lightningInterval > 0) {
                    lightningInterval--;
                } else {
                    Vector3d pos = lightningPositions.remove();
                    if (meta8.isCreative()) {
                        SoulBoltEntity bolt = ModEntities.SOUL_BOLT.create(level);
                        if (bolt != null) {
                            bolt.setOwner(meta8);
                            bolt.setKnockbackStrength(CREATIVE_KNOCKBACK_STRENGTH);
                            bolt.setKnockbackModifier(0.1);
                            bolt.moveTo(new Vector3d(pos.x, PositionCalculators.smart(new BlockPos(pos), level, meta8.blockPosition()), pos.z));
                            level.addFreshEntity(bolt);
                        }
                    } else {
                        LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(level);
                        if (bolt != null) {
                            ((IHasOwner<LivingEntity>) bolt).setOwner(meta8);
                            bolt.moveTo(new Vector3d(pos.x, PositionCalculators.smart(new BlockPos(pos), level, meta8.blockPosition()), pos.z));
                            bolt.setDamage(DAMAGE);
                            level.addFreshEntity(bolt);
                        }
                    }
                    lightningInterval = maxInterval.getAsInt();
                }
            }
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        private void updateLightningPositions() {
            lightningPositions.clear();
            Vector3d pos = meta8.position();
            Vector3d vectorToTarget = pos.vectorTo(target.position());
            for (int i = 1; i <= lightningCount; i++) {
                lightningPositions.add(pos.add(vectorToTarget.normalize().scale(lightningSpacing * i)));
            }
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (meta8.getTarget() == null || !meta8.getTarget().isAlive()) {
                meta8.setAttacking(false);
                return StopReason.NO_TARGET;
            } else if (attackTime >= maxAttackTimeSup.getAsInt()) {
                meta8.setAttacking(false);
                return StopReason.NEXT_PHASE;
            }
            return null;
        }

        @Override
        public void stop() {
            super.stop();
            attackTime = 0;
            lightningWarmupTime = maxInterval.getAsInt() * 8;
            lightningInterval = maxInterval.getAsInt();
            lightningPositions.clear();
        }
    }

    public static class HealRobotGoal extends Meta08LaserAttackGoal implements IPhaseableGoal {
        private static final double RANGE = 22;
        private final Meta08Entity meta8;
        private final ServerWorld level;

        public HealRobotGoal(Meta08Entity meta8, int minAttackTime, int maxAttackTime) {
            super(meta8, 1, () -> 20, () -> 20, 10, minAttackTime, maxAttackTime, (int) (RANGE - 6), () -> 0);
            checkGoalInstantiationServerside(meta8);
            this.meta8 = meta8;
            this.level = (ServerWorld) meta8.level;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (!meta8.isCreative()) {
                return StopReason.NEXT_PHASE;
            }
            List<RobotEntity> robots = level.getNearbyEntities(RobotEntity.class, EntityPredicate.DEFAULT.allowSameTeam().allowInvulnerable().allowNonAttackable().range(RANGE), meta8, meta8.getBoundingBox().inflate(RANGE));
            if (robots.isEmpty()) {
                return StopReason.NEXT_PHASE;
            }
            RobotEntity robot = CollectionUtils.getRandom(robots, meta8.getRandom());
            if (EntityUtils.isAlive(robot)) {
                target = robot;
                return null;
            }
            return StopReason.NEXT_PHASE;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (!EntityUtils.isAlive(target)) {
                return StopReason.NO_TARGET;
            }
            if (attackTime >= attackTimeLimit) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }
    }
}
