package lych.soullery.util.mixin;

import net.minecraft.entity.LivingEntity;

public interface IBrainMixin<E extends LivingEntity> {
    boolean isValidBrain();

    void setDisabled(boolean disabled);

    boolean canSwim();

    default void setDisabledIfValid(boolean disabled) {
        if (isValidBrain()) {
            setDisabled(disabled);
        }
    }
}
