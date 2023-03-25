package lych.soullery.extension.control;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IHandler<T extends MobEntity> {
    default void saveTo(CompoundNBT data) {}

    default void loadFrom(CompoundNBT data) {}

    default void tick(T operatingMob, ServerPlayerEntity player, CompoundNBT data) {}
}
