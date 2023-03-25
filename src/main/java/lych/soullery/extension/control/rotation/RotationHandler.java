package lych.soullery.extension.control.rotation;

import lych.soullery.extension.control.IHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface RotationHandler<T extends MobEntity> extends IHandler<T> {
    void handleRotation(T operatingMob, ServerPlayerEntity player, float rotationDelta, CompoundNBT data);
}
