package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap.Type;

import javax.annotation.Nullable;

public class TakeoffPhase extends AbstractPhase {
    private boolean firstTick;
    private Path currentPath;
    private Vector3d targetLocation;

    public TakeoffPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void doServerTick() {
        if (firstTick || currentPath == null) {
            firstTick = false;
            findNewTarget();
        } else {
            BlockPos pos = level.getHeightmapPos(Type.MOTION_BLOCKING_NO_LEAVES, dragon.getFightCenter());
            if (!pos.closerThan(dragon.position(), 10)) {
                dragon.setPhase(PhaseType.DEFAULT);
            }
        }
    }

    @Override
    public void begin() {
        firstTick = true;
        currentPath = null;
        targetLocation = null;
    }

    private void findNewTarget() {
        int closestNodeId = dragon.findClosestNode();
        Vector3d lookVec = dragon.getHeadLookVector(1);
        int lookingClosestNodeId = dragon.findClosestNode(-lookVec.x * 40, 105, -lookVec.z * 40);
        if (dragon.getFight() != null && dragon.getFight().getCrystalsAlive() > 0) {
//          Finding outer nodes
            lookingClosestNodeId = lookingClosestNodeId % SoulDragonEntity.POINT_COUNT_OUTER;
            if (lookingClosestNodeId < 0) {
                lookingClosestNodeId += SoulDragonEntity.POINT_COUNT_OUTER;
            }
        } else {
            lookingClosestNodeId = lookingClosestNodeId - SoulDragonEntity.POINT_COUNT_OUTER;
            lookingClosestNodeId = lookingClosestNodeId & 7;
            lookingClosestNodeId = lookingClosestNodeId + SoulDragonEntity.POINT_COUNT_OUTER;
        }

        currentPath = dragon.findPath(closestNodeId, lookingClosestNodeId, null);
        navigateToNextPathNode();
    }

    private void navigateToNextPathNode() {
        if (currentPath != null) {
            currentPath.advance();
            if (!currentPath.isDone()) {
                Vector3i nextNodePos = currentPath.getNextNodePos();
                currentPath.advance();

                double y;
                do {
                    y = (float) nextNodePos.getY() + dragon.getRandom().nextFloat() * 20;
                } while (y < (double) nextNodePos.getY());

                targetLocation = new Vector3d(nextNodePos.getX(), y, nextNodePos.getZ());
            }
        }
    }

    @Override
    @Nullable
    public Vector3d getFlyTargetLocation() {
        return targetLocation;
    }

    @Override
    public PhaseType<TakeoffPhase> getPhase() {
        return PhaseType.TAKEOFF;
    }
}
