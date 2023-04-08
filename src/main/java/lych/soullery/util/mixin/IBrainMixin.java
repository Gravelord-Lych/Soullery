package lych.soullery.util.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.SwimTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

import java.util.Set;
import java.util.function.Predicate;

public interface IBrainMixin<E extends LivingEntity> {
    boolean isValidBrain();

    void setDisabled(boolean disabled);

    void disableTargetTasks(ServerWorld level, E entity, long gameTime);

    @SuppressWarnings("unchecked")
    default void disableTargetTasksRaw(ServerWorld level, LivingEntity entity, long gameTime) {
        disableTargetTasks(level, (E) entity, gameTime);
    }

    void restartTargetTasks();

    Set<Task<? super E>> getTasks(Predicate<? super Task<? super E>> predicate);

    Set<Task<? super E>> getTasks(Predicate<? super Task<? super E>> predicate, Activity activity);

    default boolean canSwim() {
        return anyMatch(SwimTask.class);
    }

    default void setDisabledIfValid(boolean disabled) {
        if (isValidBrain()) {
            setDisabled(disabled);
        }
    }

    boolean anyMatch(Class<?> type);
}
