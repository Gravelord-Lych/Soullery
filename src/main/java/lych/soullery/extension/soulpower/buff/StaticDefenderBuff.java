package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.ExtraAbilityConstants;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum StaticDefenderBuff implements DefenseBuff {
    INSTANCE;

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {
        if (isPlayerStatic(player)) {
            event.setAmount(event.getAmount() * ExtraAbilityConstants.STATIC_DEFENDER_DAMAGE_MULTIPLIER);
        }
    }

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}

    private static boolean isPlayerStatic(PlayerEntity player) {
        return ((IPlayerEntityMixin) player).isStatic();
    }
}
