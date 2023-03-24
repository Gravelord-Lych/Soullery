package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum FireResistanceBuff implements DefenseBuff {
    INSTANCE;

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {
        if (event.getSource().isFire()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {
        player.clearFire();
    }

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        DefenseBuff.super.serverTick(player, world);
        if (player.isOnFire()) {
            player.clearFire();
        }
    }
}
