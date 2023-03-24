package lych.soullery.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ControlledEnemyFindNewTargetTask<E extends MobEntity> extends FindNewAttackTargetTask<E> {
    public ControlledEnemyFindNewTargetTask(E mob) {
        this(entity -> !isTargetValid(mob, entity));
    }

    public ControlledEnemyFindNewTargetTask(Predicate<LivingEntity> stopAttackingWhen) {
        super(stopAttackingWhen);
    }

    public static boolean isTargetValid(MobEntity mob, LivingEntity target) {
        return isTargetValid(ControlledEnemyFindTargetTask::findTarget, mob, target);
    }

    public static boolean isTargetValid(Function<? super MobEntity, Optional<? extends LivingEntity>> targetFinderFunction, MobEntity mob, LivingEntity target) {
        return targetFinderFunction.apply(mob).filter(entity -> entity == target).isPresent();
    }
}
