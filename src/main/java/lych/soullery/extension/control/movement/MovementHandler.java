package lych.soullery.extension.control.movement;

import lych.soullery.extension.control.IHandler;
import lych.soullery.network.MovementData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

public interface MovementHandler<T extends MobEntity> extends IHandler<T> {
    void handleMovement(T operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl, CompoundNBT data);
}
