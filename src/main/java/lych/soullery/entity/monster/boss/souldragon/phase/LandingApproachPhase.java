package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap.Type;

import javax.annotation.Nullable;

public class LandingApproachPhase extends AbstractPhase {
    private static final EntityPredicate PREDICATE = new EntityPredicate().range(128);
    private Path currentPath;
    private Vector3d targetLocation;

    public LandingApproachPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void begin() {
        currentPath = null;
        targetLocation = null;
    }

    @Override
    public void doServerTick() {
        double distanceSqr = targetLocation == null ? 0 : targetLocation.distanceToSqr(dragon.getX(), dragon.getY(), dragon.getZ());
        if (distanceSqr < 10 * 10 || distanceSqr > 150 * 150 || dragon.horizontalCollision || dragon.verticalCollision) {
            findNewTarget();
        }
    }

    @Override
    @Nullable
    public Vector3d getFlyTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        if (currentPath == null || currentPath.isDone()) {
            int myNode = dragon.findClosestNode();
            BlockPos pos = dragon.level.getHeightmapPos(Type.MOTION_BLOCKING_NO_LEAVES, dragon.getFightCenter());
            PlayerEntity player = dragon.level.getNearestPlayer(PREDICATE, pos.getX(), pos.getY(), pos.getZ());
            int toNode;
            if (player != null) {
                Vector3d playerXZ = new Vector3d(player.getX(), 0, player.getZ()).subtract(Vector3d.atCenterOf(dragon.getFightCenter())).normalize();
                toNode = dragon.findClosestNode(-playerXZ.x * 40, 105, -playerXZ.z * 40);
            } else {
                toNode = dragon.findClosestNode(40, pos.getY(), 0);
            }

            PathPoint pathpoint = new PathPoint(pos.getX(), pos.getY(), pos.getZ());
            currentPath = dragon.findPath(myNode, toNode, pathpoint);
            if (currentPath != null) {
                currentPath.advance();
            }
        }

        navigateToNextPathNode();
        if (currentPath != null && currentPath.isDone()) {
            dragon.setPhase(PhaseType.LANDING);
        }
    }

    private void navigateToNextPathNode() {
        if (currentPath != null && !currentPath.isDone()) {
            Vector3i nextNodePos = currentPath.getNextNodePos();
            currentPath.advance();
            double x = nextNodePos.getX();
            double z = nextNodePos.getZ();

            double y;
            do {
                y = nextNodePos.getY() + this.dragon.getRandom().nextFloat() * 20;
            } while (y < nextNodePos.getY());

            targetLocation = new Vector3d(x, y, z);
        }
    }

    @Override
    public PhaseType<?> getPhase() {
        return PhaseType.LANDING_APPROACH;
    }
}
