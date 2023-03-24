package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class EnderDragonReinforcement extends AggressiveReinforcement {
    private static final int BASE_DURATION = 10;
    private static final int DURATION_STEP = 20;

    public EnderDragonReinforcement() {
        super(EntityType.ENDER_DRAGON);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {
        double reachDistance = Math.min(attacker.getAttributeValue(ForgeMod.REACH_DISTANCE.get()), attacker.distanceTo(target) + 1);
        List<LivingEntity> entities = attacker.level.getEntitiesOfClass(LivingEntity.class, attacker.getBoundingBox().inflate(reachDistance, reachDistance, reachDistance));
        entities.removeIf(e -> e.distanceToSqr(attacker) > reachDistance * reachDistance);
        if (target instanceof IMob) {
            entities.removeIf(e -> !(e instanceof IMob));
        }
        entities.remove(attacker);
        entities.forEach(e -> e.addEffect(new EffectInstance(e.isInvertedHealAndHarm() ? Effects.HEAL : Effects.HARM, BASE_DURATION + level * DURATION_STEP, 0)));
        entities.remove(target);
        entities.forEach(e -> e.hurt(EntityUtils.livingAttack(attacker).setMagic(), event.getAmount()));
    }

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}

    @Override
    protected boolean allowsDamageSource(DamageSource source) {
        return super.allowsDamageSource(source) && !source.isMagic();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
