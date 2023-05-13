package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.IHasOwner;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class FollowOwnerGoal<T extends MobEntity & IHasOwner<?>> extends Goal {
    private final T mob;
    private LivingEntity owner;
    private final World level;
    private final double speedModifier;
    private final PathNavigator navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private final double teleportDistance;
    private float oldWaterCost;
    private final boolean followOwnerIfHasTarget;
    private final boolean canFly;

    public FollowOwnerGoal(T mob, double speedModifier, float startDistance, float stopDistance, boolean canFly) {
        this(mob, speedModifier, startDistance, stopDistance, -1, canFly);
    }

    public FollowOwnerGoal(T mob, double speedModifier, float startDistance, float stopDistance, double teleportDistance, boolean canFly) {
        this(mob, speedModifier, startDistance, stopDistance, teleportDistance, false, canFly);
    }

    public FollowOwnerGoal(T mob, double speedModifier, float startDistance, float stopDistance, double teleportDistance, boolean followOwnerIfHasTarget, boolean canFly) {
        this.mob = mob;
        this.level = mob.getAsEntity().level;
        this.speedModifier = speedModifier;
        this.navigation = mob.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.teleportDistance = teleportDistance;
        this.followOwnerIfHasTarget = followOwnerIfHasTarget;
        this.canFly = canFly;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigator) && !(mob.getNavigation() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = mob.getOwner();
        if (owner == null) {
            return false;
        }
        if (!mob.isOwnerInTheSameWorld()) {
            return false;
        }
        if (owner.isSpectator()) {
            return false;
        }
        if (mob.distanceToSqr(owner) < (double) (startDistance * startDistance)) {
            return false;
        }
        if (!followOwnerIfHasTarget && mob.getTarget() != null) {
            return false;
        }
        this.owner = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (navigation.isDone()) {
            return false;
        }
        if (!followOwnerIfHasTarget && mob.getTarget() != null) {
            return false;
        }
        return mob.distanceToSqr(owner) > (double) (stopDistance * stopDistance);
    }

    @Override
    public void start() {
        timeToRecalcPath = 0;
        oldWaterCost = mob.getPathfindingMalus(PathNodeType.WATER);
        mob.setPathfindingMalus(PathNodeType.WATER, 0);
    }

    @Override
    public void stop() {
        owner = null;
        navigation.stop();
        mob.setPathfindingMalus(PathNodeType.WATER, oldWaterCost);
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(owner, 10, (float) mob.getMaxHeadXRot());
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10;
            if (!mob.isLeashed() && !mob.isPassenger()) {
                if (teleportDistance > 0 && mob.distanceToSqr(owner) >= teleportDistance * teleportDistance) {
                    teleportToOwner();
                } else {
                    navigation.moveTo(owner, speedModifier);
                }
            }
        }
    }

    private void teleportToOwner() {
        BlockPos ownerPos = owner.blockPosition();

        for (int i = 0; i < 10; ++i) {
            int x = randomIntInclusive(-3, 3);
            int y = randomIntInclusive(-1, 1);
            int z = randomIntInclusive(-3, 3);
            boolean teleported = maybeTeleportTo(ownerPos.getX() + x, ownerPos.getY() + y, ownerPos.getZ() + z);
            if (teleported) {
                return;
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (Math.abs(x - owner.getX()) < 2 && Math.abs(z - owner.getZ()) < 2) {
            return false;
        }
        if (!canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        }
        mob.moveTo((double) x + 0.D, y, (double) z + 0.5, mob.yRot, mob.xRot);
        navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType type = WalkNodeProcessor.getBlockPathTypeStatic(level, pos.mutable());
        if (type != PathNodeType.WALKABLE) {
            return false;
        }
        BlockState state = level.getBlockState(pos.below());
        if (!canFly && state.getBlock() instanceof LeavesBlock) {
            return false;
        } else {
            BlockPos vectorToPos = pos.subtract(mob.blockPosition());
            return level.noCollision(mob, mob.getBoundingBox().move(vectorToPos));
        }
    }

    private int randomIntInclusive(int min, int max) {
        return mob.getRandom().nextInt(max - min + 1) + min;
    }
}
