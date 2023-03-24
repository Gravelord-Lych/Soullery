package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum RestorationBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        if (player.tickCount % ExtraAbilityConstants.RESTORATION_INTERVAL_TICKS == 0) {
            player.heal(1);
        }
    }
}
