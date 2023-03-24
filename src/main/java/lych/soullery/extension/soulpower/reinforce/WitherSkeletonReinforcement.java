package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.api.event.PostLivingHurtEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class WitherSkeletonReinforcement extends AggressiveReinforcement {
    private static final double HEAL_PROBABILITY = 0.35;
    private static final float HEAL_AMOUNT_MULTIPLIER = 0.05f;

    public WitherSkeletonReinforcement() {
        super(EntityType.WITHER_SKELETON);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}

    @Override
    protected void postHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, PostLivingHurtEvent event) {
        super.postHurt(stack, attacker, target, level, event);
        if (event.isSuccessfullyHurt() && target.getRandom().nextDouble() < HEAL_PROBABILITY) {
            attacker.heal(event.getAmount() * HEAL_AMOUNT_MULTIPLIER * level);
        }
    }
}
