package lych.soullery.extension.control.rotation;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface RotationHandler<T extends MobEntity> {
    void handleRotation(T operatingMob, ServerPlayerEntity player, float rotationDelta);

    float handleRotationOffset(T operatingMob, ServerPlayerEntity player, double scroll);

    default void tick(T operatingMob, ServerPlayerEntity player) {}
}
