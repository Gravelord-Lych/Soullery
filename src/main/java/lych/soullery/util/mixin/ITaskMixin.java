package lych.soullery.util.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public interface ITaskMixin<E extends LivingEntity> {
    boolean isDisabled();

    void disable(ServerWorld level, E entity, long gameTime);

    default void disablePartially(ServerWorld level, E entity, long gameTime) {
        disable(level, entity, gameTime);
    }

    void restart();

    default void onRestart() {}

    @SuppressWarnings("unchecked")
    static <E extends LivingEntity> ITaskMixin<E> cast(Task<E> task) {
        return (ITaskMixin<E>) task;
    }
}
