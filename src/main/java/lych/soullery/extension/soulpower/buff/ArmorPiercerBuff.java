package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum ArmorPiercerBuff implements DamageBuff {
    INSTANCE;

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {
        if (EntityUtils.isMelee(event.getSource())) {
            event.getSource().bypassArmor();
        }
    }

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
