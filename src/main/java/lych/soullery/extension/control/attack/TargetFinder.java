package lych.soullery.extension.control.attack;

import lych.soullery.extension.control.IHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

public interface TargetFinder<T extends MobEntity> extends IHandler<T> {
    @Nullable
    LivingEntity findTarget(T operatingMob, ServerPlayerEntity player, CompoundNBT data);

    @Nullable
    default TargetFinder<? super T> getPrimary() {
        return this;
    }
}
