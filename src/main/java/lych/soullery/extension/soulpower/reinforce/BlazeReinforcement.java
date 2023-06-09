package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class BlazeReinforcement extends DefensiveReinforcement {
    private static final int MAX_LEVEL = 6;

    public BlazeReinforcement() {
        super(EntityType.BLAZE);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {
        level = Math.min(level, MAX_LEVEL);
        if (source.isFire()) {
            event.setAmount(event.getAmount() * (float) (MAX_LEVEL + 1 - level) / (MAX_LEVEL + 1));
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
        return 2;
    }
}
