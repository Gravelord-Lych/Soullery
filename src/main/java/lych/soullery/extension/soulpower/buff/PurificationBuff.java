package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.util.ModEffectUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public enum PurificationBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {
        List<EffectInstance> harmfulEffects = new ArrayList<>();
        for (EffectInstance effect : player.getActiveEffects()) {
            if (ModEffectUtils.isHarmful(effect)) {
                harmfulEffects.add(effect);
            }
        }
        harmfulEffects.stream().map(EffectInstance::getEffect).forEach(player::removeEffect);
    }

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {}
}
