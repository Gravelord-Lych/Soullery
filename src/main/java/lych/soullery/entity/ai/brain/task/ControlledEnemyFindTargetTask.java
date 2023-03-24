package lych.soullery.entity.ai.brain.task;

import lych.soullery.entity.ai.brain.memory.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ControlledEnemyFindTargetTask<E extends MobEntity> extends ForgetAttackTargetTask<E> {
    public ControlledEnemyFindTargetTask() {
        super(ControlledEnemyFindTargetTask::findTarget);
    }

    public ControlledEnemyFindTargetTask(Predicate<E> canAttackPredicate, Function<E, Optional<? extends LivingEntity>> targetFinderFunction) {
        super(canAttackPredicate, targetFinderFunction);
    }

    @SuppressWarnings("unchecked")
    public static Optional<? extends LivingEntity> findTarget(MobEntity mob) {
        Brain<? extends MobEntity> brain = (Brain<? extends MobEntity>) mob.getBrain();
        Optional<List<MobEntity>> visibleMonsters = brain.getMemory(ModMemories.VISIBLE_MONSTERS);
        if (visibleMonsters.isPresent()) {
            return visibleMonsters.get().stream().filter(mob::canAttack).findFirst();
        }
        return Optional.empty();
    }
}