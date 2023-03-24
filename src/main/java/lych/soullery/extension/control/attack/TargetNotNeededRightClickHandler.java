package lych.soullery.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface TargetNotNeededRightClickHandler<T extends MobEntity> extends RightClickHandler<T> {
    @Override
    default boolean needsExactTarget() {
        return false;
    }

    @Override
    default void handleRightClick(T operatingMob, LivingEntity target, ServerPlayerEntity player) {
        handleRightClick(operatingMob, player);
    }
}
