package lych.soullery.util.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.SwimTask;

public interface IBrainMixin<E extends LivingEntity> {
    boolean isValidBrain();

    void setDisabled(boolean disabled);

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
