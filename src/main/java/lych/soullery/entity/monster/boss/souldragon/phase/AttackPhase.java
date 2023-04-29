package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.Soullery;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.world.event.SoulDragonFight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nullable;

public abstract class AttackPhase extends AbstractPhase {
    private int maxAttackStep = -1;
    private int maxCharge = -1;
    private int charge;
    private Path currentPath;
    protected Vector3d targetLocation;
    protected LivingEntity attackTarget;
    private boolean clockwise;

    public AttackPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void doServerTick() {
        if (attackTarget == null) {
            Soullery.LOGGER.warn(SoulDragonFight.MARKER, "Skipping player strafe phase because no player was found");
            dragon.setPhase(PhaseType.DEFAULT);
        } else {
            if (currentPath != null && currentPath.isDone()) {
                double x = attackTarget.getX();
                double z = attackTarget.getZ();
                double tx = x - dragon.getX();
                double tz = z - dragon.getZ();
                double distance = MathHelper.sqrt(tx * tx + tz * tz);
                double yOffs = Math.min(0.4 + distance / 80 - 1, 10);
                targetLocation = new Vector3d(x, attackTarget.getY() + yOffs, z);
            }

            double distanceSqr = targetLocation == null ? 0 : targetLocation.distanceToSqr(dragon.getX(), dragon.getY(), dragon.getZ());
            if (distanceSqr < 10 * 10 || distanceSqr > 225 * 225) {
                findNewTarget();
            }

            double maxDistance = 64;
            if (attackTarget.distanceToSqr(dragon) < maxDistance * maxDistance) {
                if (dragon.canSee(attackTarget)) {
                    if (maxCharge < 0) {
                        maxCharge = Math.max(1, getMaxCharge(attackTarget));
                    }
                    charge++;
                    Vector3d vecToTarget = new Vector3d(attackTarget.getX() - dragon.getX(), 0, attackTarget.getZ() - this.dragon.getZ()).normalize();
                    Vector3d lookVec = new Vector3d(MathHelper.sin(dragon.yRot * ((float) Math.PI / 180)), 0, -MathHelper.cos(dragon.yRot * ((float) Math.PI / 180))).normalize();
                    float cosAngle = (float) lookVec.dot(vecToTarget);
                    float deg = (float) (Math.acos(cosAngle) * (double) (180F / (float)Math.PI));
                    deg = deg + 0.5f;
                    if (charge >= maxCharge && deg >= 0 && deg < 10) {
                        doAttack();

                        charge = 0;
                        maxCharge = Math.max(1, getMaxCharge(attackTarget));

                        if (currentPath != null) {
                            while (!currentPath.isDone()) {
                                currentPath.advance();
                            }
                        }

                        if (dragon.getAttackStep() >= maxAttackStep) {
                            dragon.setPhase(PhaseType.DEFAULT);
                        } else {
                            dragon.setAttackStep(dragon.getAttackStep() + 1);
                        }
                    }
                } else if (charge > 0) {
                    charge--;
                }
            } else if (charge > 0) {
                charge--;
            }
        }
    }

    protected abstract void doAttack();

    @Override
    public void begin() {
        charge = 0;
        targetLocation = null;
        currentPath = null;
        attackTarget = null;
    }

    private void findNewTarget() {
        if (currentPath == null || currentPath.isDone()) {
            int myNode = dragon.findClosestNode();
            int toNode = myNode;
            if (dragon.getRandom().nextInt(8) == 0) {
                clockwise = !clockwise;
                toNode = myNode + SoulDragonEntity.POINT_COUNT_OUTER / 2;
            }

            if (clockwise) {
                toNode++;
            } else {
                toNode--;
            }

            if (dragon.getFight() != null && dragon.getFight().getCrystalsAlive() > 0) {
                toNode = toNode % SoulDragonEntity.POINT_COUNT_OUTER;
                if (toNode < 0) {
                    toNode += SoulDragonEntity.POINT_COUNT_OUTER;
                }
            } else {
                toNode = toNode - SoulDragonEntity.POINT_COUNT_OUTER;
                toNode = toNode & 7;
                toNode = toNode + SoulDragonEntity.POINT_COUNT_OUTER;
            }

            currentPath = dragon.findPath(myNode, toNode, null);
            if (currentPath != null) {
                currentPath.advance();
            }
        }

        navigateToNextPathNode();
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

    public void setTarget(LivingEntity target) {
        attackTarget = target;
        int myNode = dragon.findClosestNode();
        int targetNode = dragon.findClosestNode(attackTarget.getX(), attackTarget.getY(), attackTarget.getZ());
        int x = MathHelper.floor(attackTarget.getX());
        int z = MathHelper.floor(attackTarget.getZ());
        double tx = x - dragon.getX();
        double tz = z - dragon.getZ();
        double horizontalDistance = MathHelper.sqrt(tx * tx + tz * tz);
        double yOffs = Math.min(0.4 + horizontalDistance / 80 - 1, 10);
        int y = MathHelper.floor(attackTarget.getY() + yOffs);
        PathPoint point = new PathPoint(x, y, z);
        currentPath = dragon.findPath(myNode, targetNode, point);
        if (currentPath != null) {
            currentPath.advance();
            navigateToNextPathNode();
        }
        maxAttackStep = getMaxAttackStep(target);
    }

    protected int getMaxCharge(LivingEntity target) {
        return 5;
    }

    protected int getMaxAttackStep(LivingEntity target) {
        return 1;
    }

    protected double calcHeadX(Vector3d viewVector) {
        return dragon.getHead().getX() - viewVector.x * 1;
    }

    protected double calcHeadY() {
        return dragon.getHead().getY(0.5) + 0.5;
    }

    protected double calcHeadZ(Vector3d viewVector) {
        return dragon.getHead().getZ() - viewVector.x * 1;
    }

    public double getWeightMultiplier(LivingEntity target) {
        return 1;
    }

    @Override
    public void end() {
        dragon.setAttackStep(0);
        maxAttackStep = -1;
        maxCharge = -1;
    }

    @Override
    @Nullable
    public Vector3d getFlyTargetLocation() {
        return this.targetLocation;
    }

    public final int getBaseWeight() {
        int weight = getPhase().getWeight();
        if (weight <= 0) {
            throw new IllegalStateException("Weight is not specified");
        }
        return weight;
    }
}
