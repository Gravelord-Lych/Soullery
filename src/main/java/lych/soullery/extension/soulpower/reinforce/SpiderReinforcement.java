package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SpiderReinforcement extends AggressiveReinforcement {
    private static final int BASE_SLOWDOWN_TICKS = 10;
    private static final int SLOWDOWN_AMPLIFIER = 4;

    public SpiderReinforcement() {
        super(EntityType.SPIDER);
    }

    @Override
    public boolean isItemPosSuitable(ItemStack stack) {
        return super.isItemPosSuitable(stack) || stack.getItem() instanceof ShootableItem;
    }

    @Override
    protected boolean allowsDamageSource(DamageSource source) {
        return super.allowsDamageSource(source) || source.isProjectile();
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, BASE_SLOWDOWN_TICKS * level, SLOWDOWN_AMPLIFIER));
    }
}
