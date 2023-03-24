package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SilverfishReinforcement extends DefensiveReinforcement {
    private static final int MAX_LEVEL = 6;
    private static final float ABSOLUTE_DEFENSE = 0.5f;
    private static final float MIN_DAMAGE = 0.2f;

    public SilverfishReinforcement() {
        super(EntityType.SILVERFISH);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event) {
        float newAmount = amount - ABSOLUTE_DEFENSE * Math.min(level, MAX_LEVEL);
        event.setAmount(Math.max(Math.min(amount, MIN_DAMAGE), newAmount));
    }

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }
}
