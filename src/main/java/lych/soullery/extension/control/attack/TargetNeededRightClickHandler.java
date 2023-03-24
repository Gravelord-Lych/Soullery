package lych.soullery.extension.control.attack;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface TargetNeededRightClickHandler<T extends MobEntity> extends RightClickHandler<T> {
    @Override
    default boolean needsExactTarget() {
        return true;
    }

    @Override
    default void handleRightClick(T operatingMob, ServerPlayerEntity player) {
        throw new UnsupportedOperationException();
    }
}
