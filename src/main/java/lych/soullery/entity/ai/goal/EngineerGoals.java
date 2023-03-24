package lych.soullery.entity.ai.goal;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.raider.AbstractRedstoneTurretEntity;
import lych.soullery.entity.monster.raider.EngineerEntity;
import lych.soullery.entity.monster.raider.RedstoneMortarEntity;
import lych.soullery.entity.monster.raider.RedstoneTurretEntity;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public final class EngineerGoals {
    private EngineerGoals() {
        throw new UnsupportedOperationException("Instantiate its static inner classes");
    }

    public static class BuildRedstoneTurretGoal extends EngineerBuildGoal {
        public BuildRedstoneTurretGoal(EngineerEntity engineer, double speedModifier) {
            super(engineer, speedModifier);
        }

        @Override
        protected EntityType<? extends AbstractRedstoneTurretEntity> getBuildType() {
            return ModEntities.REDSTONE_TURRET;
        }

        @Override
        protected Vector3d calculateBuildPos() {
            Vector3d toTarget = null;
            for (int i = 0; i < 5; i++) {
                toTarget = RandomPositionGenerator.getPosTowards(engineer, 12, 6, target.position());
                if (toTarget != null) {
                    break;
                }
            }
            if (toTarget == null) {
                toTarget = engineer.position().add(
                        (random.nextDouble() - random.nextDouble()) * 8,
                        (random.nextDouble() - random.nextDouble()) * 8,
                        (random.nextDouble() - random.nextDouble()) * 8);
            }
            double modifier = 2;
            if (toTarget.closerThan(target.position(), 5)) {
                modifier = 5;
            }
            toTarget.add(target.position().vectorTo(engineer.position()).normalize().scale(modifier));
            return toTarget;
        }

        @Override
        protected void doBuild() {
            RedstoneTurretEntity turret = ModEntities.REDSTONE_TURRET.create(level);
            if (turret != null) {
                turret.moveTo(buildPos);
                turret.setOwner(engineer);
                turret.finalizeSpawn(level, level.getCurrentDifficultyAt(turret.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
                level.addFreshEntity(turret);
                EntityUtils.spawnAnimServerside(turret, level);
            }
        }
    }

    public static class BuildRedstoneMortarGoal extends EngineerBuildGoal {
        private Raid raid;

        public BuildRedstoneMortarGoal(EngineerEntity engineer, double speedModifier) {
            super(engineer, speedModifier);
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (!engineer.hasActiveRaid()) {
                return StopReason.NO_TARGET;
            }
            StopReason skipReason = super.getSkipReason();
            if (skipReason == null) {
                Raid raid = engineer.getCurrentRaid();
                if (raid == null) {
                    return StopReason.NO_TARGET;
                }
                this.raid = raid;
                return null;
            }
            return skipReason;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (!engineer.hasActiveRaid()) {
                return StopReason.NEXT_PHASE;
            }
            return super.getStopReason();
        }

        @Override
        protected EntityType<? extends AbstractRedstoneTurretEntity> getBuildType() {
            return ModEntities.REDSTONE_MORTAR;
        }

        @Override
        protected Vector3d calculateBuildPos() {
            Vector3d center = Vector3d.atBottomCenterOf(raid.getCenter());
            Vector3d vectorToEngineer = center.vectorTo(engineer.position());
            double modifier = 1;
            Vector3d pos;

            do {
                double lengthSqr = vectorToEngineer.lengthSqr();
                if (lengthSqr < 5 * 5) {
                    vectorToEngineer = vectorToEngineer.normalize().scale(5);
                }
                if (lengthSqr < 30 * 30) {
                    modifier += 0.2;
                }
                pos = center.add(vectorToEngineer.scale(modifier));
            } while (pos.closerThan(center, 10));

            pos.add((random.nextDouble() - random.nextDouble()) * 8, (random.nextDouble() - random.nextDouble()) * 8, (random.nextDouble() - random.nextDouble()) * 8);
            return pos;
        }

        @Override
        protected void doBuild() {
            RedstoneMortarEntity mortar = ModEntities.REDSTONE_MORTAR.create(level);
            if (mortar != null) {
                mortar.moveTo(buildPos);
                mortar.setOwner(engineer);
                mortar.finalizeSpawn(level, level.getCurrentDifficultyAt(mortar.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
                level.addFreshEntity(mortar);
                EntityUtils.spawnAnimServerside(mortar, level);
            }
        }
    }

    public static class SendRedstoneBombGoal extends EngineerGoal {
        private final RangedAttackGoal goal;

        public SendRedstoneBombGoal(EngineerEntity engineer, RangedAttackGoal goal) {
            super(engineer);
            this.goal = goal;
            setFlags(goal.getFlags());
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (goal.canUse()) {
                return null;
            }
            return StopReason.NO_TARGET;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (engineer.isAttacked()) {
                return StopReason.NEXT_PHASE;
            }
            if (goal.canContinueToUse()) {
                return null;
            }
            return StopReason.NO_TARGET;
        }

        @Override
        public void start() {
            super.start();
            goal.start();
            engineer.setDelaying(true);
        }

        @Override
        public void tick() {
            super.tick();
            goal.tick();
        }

        @Override
        public void stop() {
            super.stop();
            goal.stop();
            engineer.setAttacked(false);
        }

        @Override
        public boolean isInterruptable() {
            return goal.isInterruptable();
        }
    }

    public static abstract class EngineerBuildGoal extends EngineerGoal {
        protected final ServerWorld level;
        protected final Random random;
        protected LivingEntity target;
        protected Vector3d buildPos;
        private final double speedModifier;
        private boolean built;

        protected EngineerBuildGoal(EngineerEntity engineer, double speedModifier) {
            super(engineer);
            this.speedModifier = speedModifier;
            EntityUtils.checkGoalInstantiationServerside(engineer);
            level = (ServerWorld) engineer.level;
            random = engineer.getRandom();
        }

        @Override
        public void start() {
            super.start();
            buildPos = calculateBuildPos();
        }

        @Override
        public void tick() {
            super.tick();
            if (engineer.position().distanceToSqr(buildPos) < 3 * 3) {
                build();
            } else {
                engineer.getNavigation().moveTo(buildPos.x, buildPos.y, buildPos.z, speedModifier);
            }
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (engineer.getTurretMap().get(getBuildType()) <= 0) {
                return StopReason.NEXT_PHASE;
            }
            LivingEntity target = engineer.getTarget();
            if (target != null) {
                this.target = target;
                return null;
            }
            return StopReason.NO_TARGET;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            StopReason skipReason = getSkipReason();
            if (skipReason != null) {
                return skipReason;
            }
            return built ? StopReason.NEXT_PHASE : null;
        }

        @Override
        public void stop() {
            super.stop();
            built = false;
        }

        protected abstract EntityType<? extends AbstractRedstoneTurretEntity> getBuildType();

        protected abstract Vector3d calculateBuildPos();

        protected abstract void doBuild();

        protected final void build() {
            doBuild();
            built = true;
        }
    }

    public static abstract class EngineerGoal extends Goal implements IPhaseableGoal {
        protected final EngineerEntity engineer;

        protected EngineerGoal(EngineerEntity engineer) {
            this.engineer = engineer;
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
}
