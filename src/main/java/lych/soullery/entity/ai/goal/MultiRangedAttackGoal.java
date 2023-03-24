package lych.soullery.entity.ai.goal;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class MultiRangedAttackGoal<T extends MobEntity & IRangedAttackMob> extends Goal {
    protected final T mob;
    protected LivingEntity target;
    protected int attackTime = -1;
    protected final double speedModifier;
    protected int seeTime;
    protected final IntSupplier attackIntervalMin;
    protected final IntSupplier attackIntervalMax;
    protected final Supplier<Float> attackRadius;
    protected final Supplier<Float> attackRadiusSqr;
    protected final IntSupplier attackIntervalOfPerAttack;
    protected final IntSupplier attackTimes;
    protected int maxAttackTimes;
    protected int currentAttackTimes;

    public MultiRangedAttackGoal(T mob, double speedModifier, IntSupplier attackInterval, Supplier<Float> attackRadius, IntSupplier attackTimes, IntSupplier attackIntervalOfPerAttack) {
        this(mob, speedModifier, attackInterval, attackInterval, attackRadius, attackIntervalOfPerAttack, attackTimes);
    }

    public MultiRangedAttackGoal(T mob, double speedModifier, IntSupplier attackIntervalMin, IntSupplier attackIntervalMax, Supplier<Float> attackRadius, IntSupplier attackIntervalOfPerAttack, IntSupplier attackTimes) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackIntervalMax = attackIntervalMax;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = () -> attackRadius.get() * attackRadius.get();
        this.attackIntervalOfPerAttack = attackIntervalOfPerAttack;
        this.attackTimes = attackTimes;
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
        maxAttackTimes = attackTimes.getAsInt();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() || !mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        target = null;
        seeTime = 0;
        attackTime = -1;
        currentAttackTimes = 0;
    }

    @Override
    public void tick() {
        double distanceSqrToTarget = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSeeTarget = mob.getSensing().canSee(target);
        if (canSeeTarget) {
            seeTime++;
        } else {
            seeTime = 0;
        }

        if (distanceSqrToTarget > (double) attackRadiusSqr.get() || seeTime < 5) {
            mob.getNavigation().moveTo(target, speedModifier);
        } else {
            mob.getNavigation().stop();
        }

        mob.getLookControl().setLookAt(target, 30, 30);
        if (--attackTime == 0) {
            if (canSeeTarget) {
                float distanceProportion = MathHelper.sqrt(distanceSqrToTarget) / attackRadius.get();
                float power = MathHelper.clamp(distanceProportion, 0.1f, 1);
                if (finishedAttack()) {
                    attackTime = MathHelper.floor(MathHelper.lerp(distanceProportion, attackIntervalMin.getAsInt(), attackIntervalMax.getAsInt()));
                    currentAttackTimes = 0;
                } else {
                    mob.performRangedAttack(target, power);
                    attackTime = attackIntervalOfPerAttack.getAsInt();
                    currentAttackTimes++;
                }
            }
        } else if (attackTime < 0) {
            float distanceProportion = MathHelper.sqrt(distanceSqrToTarget) / attackRadius.get();
            attackTime = MathHelper.floor(MathHelper.lerp(distanceProportion, attackIntervalMin.getAsInt(), attackIntervalMax.getAsInt()));
        }
    }

    protected boolean finishedAttack() {
        return currentAttackTimes >= maxAttackTimes;
    }
}
