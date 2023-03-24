package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ZombifiedPiglinReinforcement extends AggressiveReinforcement {
    private static final float MAX_DAMAGE_COEFFICIENT = 1.8f;

    public ZombifiedPiglinReinforcement() {
        super(EntityType.ZOMBIFIED_PIGLIN);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {
        float temperature = attacker.level.getBiome(attacker.blockPosition()).getTemperature(attacker.blockPosition());
        float damageCoefficient = 1 + MathHelper.clamp(temperature / 3 * level, 0, MAX_DAMAGE_COEFFICIENT - 1);
        event.setAmount(event.getAmount() * damageCoefficient);
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}
}
