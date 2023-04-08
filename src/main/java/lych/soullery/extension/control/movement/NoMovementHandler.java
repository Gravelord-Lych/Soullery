package lych.soullery.extension.control.movement;

import lych.soullery.network.MovementData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

public enum NoMovementHandler implements MovementHandler<MobEntity> {
    INSTANCE;

    @Override
    public void handleMovement(MobEntity operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl, CompoundNBT data) {}
}
