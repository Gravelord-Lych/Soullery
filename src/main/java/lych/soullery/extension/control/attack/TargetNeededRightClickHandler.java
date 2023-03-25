package lych.soullery.extension.control.attack;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface TargetNeededRightClickHandler<T extends MobEntity> extends RightClickHandler<T> {
    @Override
    default boolean needsExactTarget(T operatingMob) {
        return true;
    }

    @Override
    default void handleRightClick(T operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        throw new UnsupportedOperationException();
    }
}
