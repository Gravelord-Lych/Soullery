package lych.soullery.extension.control.attack;

import lych.soullery.extension.control.IHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface RightClickHandler<T extends MobEntity> extends IHandler<T> {
    boolean needsExactTarget(T operatingMob);

    void handleRightClick(T operatingMob, ServerPlayerEntity player, CompoundNBT data);

    void handleRightClick(T operatingMob, LivingEntity target, ServerPlayerEntity player, CompoundNBT data);
}
