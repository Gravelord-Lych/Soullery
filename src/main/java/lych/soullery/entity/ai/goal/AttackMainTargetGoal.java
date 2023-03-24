package lych.soullery.entity.ai.goal;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.EntityPredicates;

import java.util.EnumSet;
import java.util.function.Supplier;

public class AttackMainTargetGoal extends TargetGoal {
    private final Supplier<? extends LivingEntity> targetGetter;
    private LivingEntity target;

    public AttackMainTargetGoal(MobEntity mob, boolean mustSee, Supplier<? extends LivingEntity> targetGetter) {
        this(mob, mustSee, false, targetGetter);
    }

    public AttackMainTargetGoal(MobEntity mob, boolean mustSee, boolean mustReach, Supplier<? extends LivingEntity> targetGetter) {
        super(mob, mustSee, mustReach);
        this.targetGetter = targetGetter;
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = targetGetter.get();
        if (!EntityUtils.isAlive(target) || !EntityPredicates.ATTACK_ALLOWED.test(target)) {
            return false;
        }
        if (mob.distanceToSqr(target) <= getFollowDistance() * getFollowDistance()) {
            this.target = target;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        mob.setTarget(target);
        super.start();
    }
}
