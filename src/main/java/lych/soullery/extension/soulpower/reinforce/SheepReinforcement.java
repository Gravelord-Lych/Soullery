package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.mixin.ILivingEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SheepReinforcement extends DefensiveReinforcement {
    private static final long MAX_APPLICABLE_TIME = 600;
    private static final int APPLICABLE_TIME_STEP = 30;
    private static final float DAMAGE_REDUCTION_AMOUNT = 0.8f;

    public SheepReinforcement() {
        super(EntityType.SHEEP);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {
        long timestamp = ((ILivingEntityMixin) entity).getSheepReinforcementLastHurtByTimestamp();
        long tickCount = ((ILivingEntityMixin) entity).getSheepReinforcementTickCount();
        if (tickCount - timestamp > MAX_APPLICABLE_TIME - (long) level * APPLICABLE_TIME_STEP) {
            event.setAmount(amount * (1 - DAMAGE_REDUCTION_AMOUNT));
        }
        ((ILivingEntityMixin) entity).setSheepReinforcementLastHurtByTimestamp(tickCount);
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event) {}

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }
}
