package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.Supplier;

public abstract class DragonReinforcement extends AggressiveReinforcement {
    private static final int BASE_DURATION = 5;
    private static final int DURATION_STEP = 15;

    public DragonReinforcement(EntityType<?> type) {
        super(type);
    }

    public DragonReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public DragonReinforcement(Supplier<EntityType<?>> type) {
        super(type);
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
        entities.forEach(e -> {
            Effect effect = getEffect(e, level);
            int duration = getDuration(level);
            if (duration == 1 && effect.isInstantenous()) {
                effect.applyInstantenousEffect(attacker, attacker, e, getAmplifier(level), 1);
            } else {
                e.addEffect(new EffectInstance(effect, duration, getAmplifier(level)));
            }
        });
        entities.remove(target);
        entities.forEach(e -> e.hurt(EntityUtils.livingAttack(attacker).setMagic(), event.getAmount()));
    }

    protected abstract Effect getEffect(LivingEntity entity, int level);

    protected int getDuration(int level) {
        return BASE_DURATION + level * DURATION_STEP;
    }

    protected int getAmplifier(int level) {
        return 0;
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
