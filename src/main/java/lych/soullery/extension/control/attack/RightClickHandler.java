package lych.soullery.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface RightClickHandler<T extends MobEntity> {
    boolean needsExactTarget();

    void handleRightClick(T operatingMob, ServerPlayerEntity player);

    void handleRightClick(T operatingMob, LivingEntity target, ServerPlayerEntity player);

    default void tick(T operatingMob, ServerPlayerEntity player) {}
}
