package lych.soullery.extension.control.movement;

import lych.soullery.network.MovementData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface MovementHandler<T extends MobEntity> {
    void handleMovement(T operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl);

    default void tick(T operatingMob, ServerPlayerEntity player) {}
}
