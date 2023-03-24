package lych.soullery.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface TargetFinder<T extends MobEntity> {
    @Nullable
    LivingEntity findTarget(T operatingMob, ServerPlayerEntity player);

    default void tick(T operatingMob, ServerPlayerEntity player) {}
}
