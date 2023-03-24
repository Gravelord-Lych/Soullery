package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class EndermanReinforcement extends DefensiveReinforcement {
    private static final double BASE_DODGE_PROBABILITY = 0.05;

    public EndermanReinforcement() {
        super(EntityType.ENDERMAN);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {
        if (entity.getRandom().nextDouble() < BASE_DODGE_PROBABILITY * level) {
            event.setCanceled(true);
        }
    }

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event) {}

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }
}
