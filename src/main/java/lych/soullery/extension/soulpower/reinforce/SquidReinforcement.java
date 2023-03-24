package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SquidReinforcement extends AggressiveReinforcement {
    private static final int BASE_EFFECT_TICKS = 10;
    private static final int EFFECT_TICKS_STEP = 10;

    public SquidReinforcement() {
        super(EntityType.SQUID);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        if (target instanceof PlayerEntity) {
            target.addEffect(new EffectInstance(Effects.BLINDNESS, BASE_EFFECT_TICKS + EFFECT_TICKS_STEP * level));
        } else {
            target.addEffect(new EffectInstance(Effects.WEAKNESS, BASE_EFFECT_TICKS + EFFECT_TICKS_STEP * level, 0));
            target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, BASE_EFFECT_TICKS + EFFECT_TICKS_STEP * level, 1));
        }
    }
}
