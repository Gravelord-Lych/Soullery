package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.api.exa.PlayerBuff;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public interface DefenseBuff extends PlayerBuff {
    void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event);

    void onPlayerHurt(PlayerEntity player, LivingHurtEvent event);

    void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event);

    void onPostHurt(PlayerEntity player, PostLivingHurtEvent event);

    @Override
    default void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    default void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    default void serverTick(ServerPlayerEntity player, ServerWorld world) {}
}
