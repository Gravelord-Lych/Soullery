package lych.soullery.mixin;

import lych.soullery.util.mixin.INearestAttackableTargetGoalMixin;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal implements INearestAttackableTargetGoalMixin<T>, TargetGoalAccessor {
    @Shadow protected LivingEntity target;
    @Shadow @Final protected Class<T> targetType;
    @Shadow protected EntityPredicate targetConditions;
    @Shadow @Final protected int randomInterval;

    private NearestAttackableTargetGoalMixin(MobEntity mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Override
    public EntityPredicate getTargetConditions() {
        return targetConditions;
    }

    @Override
    public void setTargetConditions(EntityPredicate conditions) {
        this.targetConditions = conditions;
    }

    @Override
    public Class<T> getTargetType() {
        return targetType;
    }

    @Override
    public <E extends LivingEntity> NearestAttackableTargetGoal<E> modifyType(Class<E> clazz, Predicate<? super LivingEntity> predicate) {
        return new NearestAttackableTargetGoal<>(mob, clazz, randomInterval, mustSee, mustReach(), predicate::test);
    }
}
