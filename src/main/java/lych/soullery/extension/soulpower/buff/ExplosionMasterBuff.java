package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum ExplosionMasterBuff implements DefenseBuff {
    INSTANCE;

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {
        if (event.getSource().isExplosion()) {
            event.setAmount((float) Math.log(event.getAmount() + 1));
        }
    }

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
