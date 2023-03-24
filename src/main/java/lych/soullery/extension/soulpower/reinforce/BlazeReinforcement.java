package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class BlazeReinforcement extends DefensiveReinforcement {
    private static final int MAX_TOTAL_LEVEL = 2;

    public BlazeReinforcement() {
        super(EntityType.BLAZE);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {
        if (source.isFire() && level >= MAX_TOTAL_LEVEL) {
            event.setCanceled(true);
            entity.clearFire();
        }
    }

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {
        if (source.isFire()) {
            event.setAmount(event.getAmount() * (float) (MAX_TOTAL_LEVEL - level) / MAX_TOTAL_LEVEL);
        }
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event) {}

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
