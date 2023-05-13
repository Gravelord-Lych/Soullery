package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class WandererReinforcement extends AggressiveReinforcement {
    public static final double PROBABILITY_MULTIPLIER = 0.4;

    public WandererReinforcement() {
        super(ModEntities.WANDERER);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}

    @Override
    protected boolean hasEvents() {
        return false;
    }
}
