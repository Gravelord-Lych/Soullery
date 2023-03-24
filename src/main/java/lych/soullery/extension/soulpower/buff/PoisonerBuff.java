package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum PoisonerBuff implements DamageBuff {
    INSTANCE;

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {
        int caveSpiderLevel = ReinforcementHelper.getReinforcementLevel(player.getMainHandItem(), Reinforcements.CAVE_SPIDER);
        int beeLevel = ReinforcementHelper.getReinforcementLevel(player.getMainHandItem(), Reinforcements.BEE);
        event.getEntityLiving().addEffect(new EffectInstance(Effects.POISON, ExtraAbilityConstants.POISONER_POISON_EFFECT_DURATION + ExtraAbilityConstants.POISONER_ADDITIONAL_POISON_EFFECT_DURATION * (caveSpiderLevel + beeLevel)));
    }

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
