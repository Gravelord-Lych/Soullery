package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum FrostResistanceBuff implements DefenseBuff {
    INSTANCE;

    private static float calculateFrostResistanceDamageMultiplier(float temperature, boolean onFire) {
        float damageMultiplier = MathHelper.clamp(0.5f + (temperature + 1) / 6, 0.5f, 1.1f);
        return onFire ? (damageMultiplier + 1) / 2 : damageMultiplier;
    }

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {
        float temperature = player.level.getBiome(player.blockPosition()).getTemperature(player.blockPosition());
        float damageMultiplier = calculateFrostResistanceDamageMultiplier(temperature, player.isOnFire());
        event.setAmount(event.getAmount() * damageMultiplier);
    }

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
