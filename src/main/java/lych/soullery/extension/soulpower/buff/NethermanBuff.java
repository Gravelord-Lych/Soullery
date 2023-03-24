package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum NethermanBuff implements DamageBuff {
    INSTANCE;

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {
        if (event.isSuccessfullyHurt()) {
            int fireAspect = EnchantmentHelper.getFireAspect(player);
            event.getEntity().setSecondsOnFire(ExtraAbilityConstants.NETHERMAN_SET_ON_FIRE_SECONDS + fireAspect * 4);
        }
    }
}
