package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lych.soullery.util.CollectionUtils;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.RangedInteger;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Map;
import java.util.Random;

public class WitchReinforcement extends AggressiveReinforcement {
    private static final Int2DoubleMap EFFECT_PROBABILITY_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(0.15).range(2).value(0.225).range(3).value(0.3).build();
    private static final Int2ObjectMap<RangedInteger> EFFECT_DURATION_MAP = EntityUtils.<RangedInteger>choiceBuilder()
            .range(1).value(RangedInteger.of(40, 80))
            .range(2).value(RangedInteger.of(60, 100))
            .range(3).value(RangedInteger.of(80, 120))
            .build();

    public WitchReinforcement() {
        super(EntityType.WITCH);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        if (attacker.getRandom().nextDouble() < EFFECT_PROBABILITY_MAP.get(level)) {
            if (attacker.getRandom().nextBoolean()) {
                attacker.addEffect(createRandomEffect(getBeneficialEffects(level), attacker.getRandom(), level));
            } else {
                target.addEffect(createRandomEffect(getHarmfulEffects(level), attacker.getRandom(), level));
            }
        }
    }

    private static EffectInstance createRandomEffect(ImmutableMap<Effect, Integer> effects, Random random, int level) {
        Map.Entry<Effect, Integer> entry = CollectionUtils.getNonnullRandom(effects.entrySet(), random);
        int duration = EFFECT_DURATION_MAP.get(level).randomValue(random);
        return new EffectInstance(entry.getKey(), duration, entry.getValue());
    }

    private static ImmutableMap<Effect, Integer> getBeneficialEffects(int level) {
        switch (level) {
            case 1:
                return ImmutableMap.of(Effects.DAMAGE_RESISTANCE, 0, Effects.MOVEMENT_SPEED, 0, Effects.REGENERATION, 0);
            case 2:
                return ImmutableMap.of(Effects.DAMAGE_RESISTANCE, 1, Effects.FIRE_RESISTANCE, 0, Effects.MOVEMENT_SPEED, 1, Effects.REGENERATION, 0);
            case 3:
                return ImmutableMap.of(Effects.DAMAGE_BOOST, 0, Effects.DAMAGE_RESISTANCE, 1, Effects.FIRE_RESISTANCE, 0, Effects.MOVEMENT_SPEED, 1, Effects.REGENERATION, 1);
            default:
                throw new AssertionError();
        }
    }

    private static ImmutableMap<Effect, Integer> getHarmfulEffects(int level) {
        switch (level) {
            case 1:
                return ImmutableMap.of(Effects.MOVEMENT_SLOWDOWN, 0, Effects.WITHER, 0);
            case 2:
                return ImmutableMap.of(Effects.MOVEMENT_SLOWDOWN, 1, Effects.WEAKNESS, 0, Effects.WITHER, 0);
            case 3:
                return ImmutableMap.of(Effects.MOVEMENT_SLOWDOWN, 2, Effects.WEAKNESS, 0, Effects.WITHER, 1);
            default:
                throw new AssertionError();
        }
    }
}
