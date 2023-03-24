package lych.soullery.entity.ai.goal;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BrainAdaptedNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public BrainAdaptedNearestAttackableTargetGoal(MobEntity mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, mustSee);
    }

    public BrainAdaptedNearestAttackableTargetGoal(MobEntity mob, Class<T> targetType, boolean mustSee, boolean mustReach) {
        super(mob, targetType, mustSee, mustReach);
    }

    public BrainAdaptedNearestAttackableTargetGoal(MobEntity mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<? super LivingEntity> predicate) {
        super(mob, targetType, randomInterval, mustSee, mustReach, predicate == null ? null : predicate::test);
    }

    @Override
    public void start() {
        EntityUtils.setTarget(mob, target);
        super.start();
    }

    @Override
    public void stop() {
        EntityUtils.setTarget(mob, null);
        super.stop();
    }
}
