package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.functional.SoulCrystalEntity;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap;

import javax.annotation.Nullable;

public class DefaultPhase extends AbstractPhase {
    private static final EntityPredicate NEW_TARGET_TARGETING = (new EntityPredicate()).range(64.0D);
    private Path currentPath;
    private Vector3d targetLocation;
    private boolean clockwise;

    public DefaultPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void doServerTick() {
        double distanceSqr = targetLocation == null ? 0 : targetLocation.distanceToSqr(dragon.getX(), dragon.getY(), dragon.getZ());
        if (distanceSqr < 10 * 10 || distanceSqr > 150 * 150 || dragon.horizontalCollision || dragon.verticalCollision) {
            findNewTarget();
        }
    }

    @Override
    public void begin() {
        currentPath = null;
        targetLocation = null;
    }

    private void findNewTarget() {
        if (currentPath != null && currentPath.isDone()) {
            BlockPos pos = level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, dragon.getFightCenter());
            int crystalsAlive = dragon.getFight() == null ? 0 : dragon.getFight().getCrystalsAlive();
            if (dragon.getHealthStatus() == SoulDragonEntity.HealthStatus.HIGH && dragon.getRandom().nextInt(crystalsAlive + 3) == 0) {
                dragon.setPhase(PhaseType.LANDING_APPROACH);
                return;
            }

            double distanceSqr = 64 * 64;
            PlayerEntity player = dragon.level.getNearestPlayer(NEW_TARGET_TARGETING, pos.getX(), pos.getY(), pos.getZ());
            if (player != null) {
                distanceSqr = pos.distSqr(player.position(), true) / 512;
            }

            if (player != null && !player.abilities.invulnerable && (dragon.getRandom().nextInt(Math.abs((int) distanceSqr) + 2) == 0 || dragon.getRandom().nextInt(crystalsAlive + 2) == 0)) {
                strafePlayer(player);
                return;
            }
        }

        if (currentPath == null || currentPath.isDone()) {
            int closestNode = dragon.findClosestNode();
            int nextNode = closestNode;
            if (dragon.getRandom().nextInt(8) == 0) {
                clockwise = !clockwise;
                nextNode = closestNode + 6;
            }

            if (clockwise) {
                ++nextNode;
            } else {
                --nextNode;
            }

            if (dragon.getFight() != null && dragon.getFight().getCrystalsAlive() >= 0) {
                nextNode = nextNode % SoulDragonEntity.POINT_COUNT_OUTER;
                if (nextNode < 0) {
                    nextNode += SoulDragonEntity.POINT_COUNT_OUTER;
                }
            } else {
                nextNode = nextNode - SoulDragonEntity.POINT_COUNT_OUTER;
                nextNode = nextNode & 7;
                nextNode = nextNode + SoulDragonEntity.POINT_COUNT_OUTER;
            }

            currentPath = dragon.findPath(closestNode, nextNode, null);
            if (currentPath != null) {
                currentPath.advance();
            }
        }

        navigateToNextPathNode();
    }

    private void strafePlayer(LivingEntity entity) {
        dragon.setRandomAttackPhase(entity);
    }

    private void navigateToNextPathNode() {
        if (currentPath != null && !currentPath.isDone()) {
            Vector3i nextNodePos = currentPath.getNextNodePos();
            currentPath.advance();
            double x = nextNodePos.getX();
            double z = nextNodePos.getZ();
            double y;

            do {
                y = nextNodePos.getY() + dragon.getRandom().nextFloat() * 20;
            } while (y < nextNodePos.getY());

            targetLocation = new Vector3d(x, y, z);
        }
    }

    @Override
    public void onCrystalDestroyed(SoulCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player) {
        if (player != null && !player.abilities.invulnerable) {
            strafePlayer(player);
        }
    }

    @Override
    @Nullable
    public Vector3d getFlyTargetLocation() {
        return targetLocation;
    }

    @Override
    public PhaseType<?> getPhase() {
        return PhaseType.DEFAULT;
    }
}
