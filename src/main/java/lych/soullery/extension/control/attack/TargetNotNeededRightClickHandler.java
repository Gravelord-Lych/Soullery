package lych.soullery.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface TargetNotNeededRightClickHandler<T extends MobEntity> extends RightClickHandler<T> {
    @Override
    default boolean needsExactTarget(T operatingMob) {
        return false;
    }

    @Override
    default void handleRightClick(T operatingMob, LivingEntity target, ServerPlayerEntity player, CompoundNBT data) {
        handleRightClick(operatingMob, player, data);
    }
}
