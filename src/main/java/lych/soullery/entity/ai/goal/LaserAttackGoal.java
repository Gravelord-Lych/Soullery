package lych.soullery.entity.ai.goal;

import com.google.common.base.Preconditions;
import lych.soullery.api.IRangedAttackGoal;
import lych.soullery.entity.iface.ILaserAttacker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.IntSupplier;

public class LaserAttackGoal<T extends MobEntity & ILaserAttacker> extends Goal implements IRangedAttackGoal {
    protected final T mob;
    protected LivingEntity target;
    protected int attackTime = -1;
    protected final double speedModifier;
    protected int seeTime;
    protected final IntSupplier attackIntervalMin;
    protected final IntSupplier attackIntervalMax;
    protected final float attackRadius;
    protected final float attackRadiusSqr;
    protected final int attackThreshold;
    protected final IntSupplier lockTargetThreshold;
    @Nullable
    protected Vector3d trueTargetPosition;

    public LaserAttackGoal(T mob, double speedModifier, int attackInterval, float attackRadius) {
        this(mob, speedModifier, () -> attackInterval, attackRadius);
    }

    public LaserAttackGoal(T mob, double speedModifier, IntSupplier attackInterval, float attackRadius) {
        this(mob, speedModifier, attackInterval, attackInterval, attackRadius);
    }

    public LaserAttackGoal(T mob, double speedModifier, IntSupplier attackIntervalMin, IntSupplier attackIntervalMax, float attackRadius) {
        this(mob, speedModifier, attackIntervalMin, attackIntervalMax, Math.min(attackIntervalMin.getAsInt(), 10), attackRadius);
    }

    public LaserAttackGoal(T mob, double speedModifier, IntSupplier attackIntervalMin, IntSupplier attackIntervalMax, int attackThreshold, float attackRadius) {
        this(mob, speedModifier, attackIntervalMin, attackIntervalMax, attackThreshold, attackRadius, () -> 6 - mob.level.getDifficulty().getId());
    }

    public LaserAttackGoal(T mob, double speedModifier, IntSupplier attackIntervalMin, IntSupplier attackIntervalMax, int attackThreshold, float attackRadius, IntSupplier lockTargetThreshold) {
        Preconditions.checkArgument(attackThreshold > 0, "AttackThreshold should be positive");
        Preconditions.checkArgument(Objects.requireNonNull(lockTargetThreshold, "LockTargetThreshold should be non-null").getAsInt() >= 0, "LockTargetThreshold should not be negative");
        Preconditions.checkArgument(attackThreshold > lockTargetThreshold.getAsInt(), "AttackThreshold should be larger than lockTargetThreshold");
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackIntervalMax = attackIntervalMax;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.attackThreshold = attackThreshold;
        this.lockTargetThreshold = lockTargetThreshold;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target != null && target.isAlive()) {
            this.target = target;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive() || !mob.canAttack(target)) {
            return false;
        }
        return canUse() || mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        target = null;
        seeTime = 0;
        attackTime = -1;
    }

    @Override
    public void tick() {
        double distanceSqrToTarget = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSeeTarget = mob.getSensing().canSee(target);
        if (canSeeTarget) {
            ++seeTime;
        } else {
            seeTime = 0;
        }

        if (distanceSqrToTarget <= attackRadiusSqr && seeTime >= 5) {
            mob.getNavigation().stop();
        } else {
            mob.getNavigation().moveTo(target, speedModifier);
        }

        mob.getLookControl().setLookAt(target, 30, 30);
        --attackTime;
        if (attackTime <= lockTargetThreshold.getAsInt() && trueTargetPosition == null) {
            trueTargetPosition = mob.getTargetPosition(target);
            Objects.requireNonNull(trueTargetPosition);
            mob.getLookControl().setLookAt(trueTargetPosition.x, trueTargetPosition.y, trueTargetPosition.z, 30, 30);
        }
        if (attackTime <= attackThreshold) {
            if (attackTime > 0) {
                if (!mob.isAttacking()) {
                    mob.onLaserAttack(target, attackTime);
                    mob.setAttacking(true);
                }
            } else if (attackTime == 0) {
                if (!canSeeTarget) {
                    return;
                }
                float distanceProportion = MathHelper.sqrt(distanceSqrToTarget) / attackRadius;
                float power = MathHelper.clamp(distanceProportion, 0.1f, 1);
                mobAttack(Objects.requireNonNull(trueTargetPosition, "TrueTargetPosition is null"), power);
                attackTime = MathHelper.floor(distanceProportion * (float) (attackIntervalMax.getAsInt() - attackIntervalMin.getAsInt()) + (float) attackIntervalMin.getAsInt());
                mob.setAttacking(false);
                trueTargetPosition = null;
            } else {
                float distanceProportion = MathHelper.sqrt(distanceSqrToTarget) / attackRadius;
                attackTime = MathHelper.floor(distanceProportion * (float) (attackIntervalMax.getAsInt() - attackIntervalMin.getAsInt()) + (float) attackIntervalMin.getAsInt());
            }
        }
    }

    protected void mobAttack(Vector3d trueTargetPosition, float power) {
        mob.performLaserAttack(target, trueTargetPosition, power);
    }
}