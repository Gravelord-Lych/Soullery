package lych.soullery.extension.control.attack;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public enum NoRightClickHandler implements TargetNotNeededRightClickHandler<MobEntity> {
    INSTANCE;

    @Override
    public void handleRightClick(MobEntity operatingMob, ServerPlayerEntity player, CompoundNBT data) {}
}
