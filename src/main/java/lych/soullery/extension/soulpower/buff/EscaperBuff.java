package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum EscaperBuff implements DefenseBuff {
    INSTANCE;

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {
        if (event.isSuccessfullyHurt() && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            ExtraAbilityConstants.ESCAPER_EFFECTS.stream().map(EffectInstance::new).forEach(attacker::addEffect);
        }
    }
}
