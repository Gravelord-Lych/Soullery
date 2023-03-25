package lych.soullery.extension.control.attack;

import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public enum CreeperRightClickHandler implements TargetNotNeededRightClickHandler<CreeperEntity> {
    INSTANCE;

    @Override
    public void handleRightClick(CreeperEntity operatingCreeper, ServerPlayerEntity player, CompoundNBT data) {
        operatingCreeper.ignite();
    }
}
