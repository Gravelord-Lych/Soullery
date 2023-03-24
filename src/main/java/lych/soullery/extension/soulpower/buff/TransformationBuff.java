package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.util.ModEffectUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum TransformationBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {
        EffectInstance witherEffect = player.getEffect(Effects.WITHER);
        if (witherEffect != null) {
            player.removeEffect(Effects.WITHER);
            player.addEffect(ModEffectUtils.copyAttributes(Effects.REGENERATION, witherEffect));
        }
    }

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {}
}
