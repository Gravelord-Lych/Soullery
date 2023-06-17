package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public enum FireResistanceBuff implements DefenseBuff {
    INSTANCE;

    private static final UUID SPEED_NERF_UUID = UUID.fromString("2757AC30-C340-3CA2-62A7-D52833E0281D");
    private static final AttributeModifier SPEED_NERF = new AttributeModifier(SPEED_NERF_UUID, "Fire Resistance speed nerf", ExtraAbilityConstants.FIRE_RESISTANCE_SPEED_NERF_AMOUNT, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {
        if (event.getSource().isFire()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {
        if (!event.getSource().isFire()) {
            event.setAmount(event.getAmount() * ExtraAbilityConstants.FIRE_RESISTANCE_DAMAGE_MULTIPLIER);
        }
    }

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {
        player.clearFire();
        player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(SPEED_NERF);
    }

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_NERF_UUID);
    }

    @Override
    public void reload(@Nullable PlayerEntity oldPlayer, PlayerEntity newPlayer) {
        if (oldPlayer != null) {
            newPlayer.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(SPEED_NERF);
        }
    }

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        DefenseBuff.super.serverTick(player, world);
        if (player.isOnFire()) {
            player.clearFire();
        }
    }
}
