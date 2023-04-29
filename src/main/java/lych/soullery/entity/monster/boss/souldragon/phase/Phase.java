package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.functional.SoulCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public interface Phase {
    float getTurnSpeed();

    PhaseType<?> getPhase();

    @Nullable
    default Vector3d getFlyTargetLocation() {
        return null;
    }

    default float onHurt(DamageSource source, float amount) {
        return amount;
    }

    default void doClientTick() {}

    default void doServerTick() {}

    default void onCrystalDestroyed(SoulCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player) {}

    default void begin() {}

    default void end() {}

    default float getFlySpeed() {
        return 0.6f;
    }
}
