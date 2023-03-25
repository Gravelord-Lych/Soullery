package lych.soullery.extension.control.attack;

import lych.soullery.extension.control.IHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface MeleeHandler<T extends MobEntity> extends IHandler<T> {
    void handleMeleeAttack(T operatingMob, LivingEntity target, ServerPlayerEntity player, CompoundNBT data);
}
