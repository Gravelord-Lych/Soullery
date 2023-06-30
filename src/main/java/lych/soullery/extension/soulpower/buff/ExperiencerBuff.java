package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum ExperiencerBuff implements PlayerBuff {
    INSTANCE;

    private static final int XP_INTERVAL = 60;
    private static final int SE_INTERVAL = 20;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        if (player.tickCount % XP_INTERVAL == 0) {
            player.giveExperiencePoints(1);
        }
        if (player.tickCount % SE_INTERVAL == 0) {
            SoulEnergies.getSEContainers(player).stream().map(SoulEnergies::of).findFirst().ifPresent(optional -> optional.ifPresent(ses -> ses.receiveSoulEnergy(1)));
        }
    }
}
