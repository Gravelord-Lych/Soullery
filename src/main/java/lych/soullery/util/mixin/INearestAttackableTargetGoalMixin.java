package lych.soullery.util.mixin;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

import java.util.function.Predicate;

public interface INearestAttackableTargetGoalMixin<T extends LivingEntity> {
    Class<T> getTargetType();

    <E extends LivingEntity> NearestAttackableTargetGoal<E> modifyType(Class<E> clazz, Predicate<? super LivingEntity> predicate);

    EntityPredicate getTargetConditions();

    void setTargetConditions(EntityPredicate conditions);
}
