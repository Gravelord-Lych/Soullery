package lych.soullery.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

public enum NoTargetFinder implements TargetFinder<MobEntity> {
    INSTANCE;

    @Nullable
    @Override
    public LivingEntity findTarget(MobEntity operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        return null;
    }
}
