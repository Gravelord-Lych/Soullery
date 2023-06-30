package lych.soullery.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public final class ExtraAbilityConstants {
    public static final float ENHANCED_AUTO_JUMP_MAX_JUMP_HEIGHT_MULTIPLIER = 2;
    public static final float ENHANCED_AUTO_JUMP_COEFFICIENT = 0.19f;
    public static final float FALL_BUFFER_AMOUNT = 5;
    public static final double MONSTER_VIEW_RANGE = 16;
    public static final double BASE_TELEPORTATION_RADIUS = 19;
    public static final double BASE_WITHER_REACH_DISTANCE = 19;
    public static final float WITHER_REACH_DAMAGE = 1.5f;
    public static final int WITHER_REACH_DAMAGE_INTERVAL = 20;
    public static final int TELEPORTATION_COOLDOWN = 300;
    public static final float FANGS_DAMAGE = 4;
    public static final double FANGS_SPACING = 1.25;
    public static final double FANGS_MAX_Y_OFFSET = 5;
    public static final int FANGS_SUMMONER_COUNT = 8;
    public static final double ULTRAREACH_HORIZONTAL_BONUS = 1.125;
    public static final double ULTRAREACH_VERTICAL_BONUS = 0.25;
    public static final int DEFAULT_ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT = 20;
    public static final int WATER_BREATHING_TICKS = 300;
    public static final int WATER_BREATHING_TICKS_WITH_TURTLE_HELMET = 500;
    public static final float THORNS_MASTER_DAMAGE = 3;
    public static final double SPEEDUP_AMOUNT = 0.15;
    public static final double ULTRAREACH_AMOUNT = 1;
    public static final int RESTORATION_INTERVAL_TICKS = 200;
    public static final int POISONER_POISON_EFFECT_DURATION = 80;
    public static final int POISONER_ADDITIONAL_POISON_EFFECT_DURATION = 40;
    public static final double INITIAL_ARMOR_AMOUNT = 4;
    public static final double INITIAL_ARMOR_TOUGHNESS_AMOUNT = 4;
    public static final int NETHERMAN_SET_ON_FIRE_SECONDS =  4;
    public static final int OVERDRIVE_FOOD_LEVEL_REQUIREMENT = 14;
    public static final int OVERDRIVE_REGEN_INTERVAL = 60;
    public static final double IMITATOR_VISIBILITY_MODIFIER = 0.6;
    public static final float NUTRITIONIST_NUTRITION_AND_SATURATION_MODIFIER = 1.25f;
    public static final float FIRE_RESISTANCE_DAMAGE_MULTIPLIER = 1.1f;
    public static final double FIRE_RESISTANCE_SPEED_NERF_AMOUNT = -0.12;
    public static final ImmutableList<ImmutableEffectInstance> GOLD_PREFERENCE_EFFECTS = ImmutableList.of(
            new ImmutableEffectInstance(Effects.DIG_SPEED, 10, 1, false, false, true),
            new ImmutableEffectInstance(Effects.MOVEMENT_SPEED, 10, 1, false, false, true));
    public static final int FROST_RESISTANCE_MONSTER_EFFECT_DURATION = 5;
    public static final int FROST_RESISTANCE_MONSTER_EFFECT_AMPLIFIER = 0;
    public static final double FROST_RESISTANCE_SLOWDOWN_RADIUS = 4;
    public static final int PILLAGER_LOOTING_LEVEL_BONUS = 1;
    public static final float STATIC_DEFENDER_DAMAGE_MULTIPLIER = 0.6f;
    public static final double SLIME_POWER_STICKY_EFFECT_ADDITIONAL_KNOCKBACK_STRENGTH = 0.5;
    public static final double SLIME_POWER_STICKY_EFFECT_SPEED_NERF = -0.07;
    public static final double MONSTER_SABOTAGE_AMOUNT = -0.1;
    public static final double PERMANENT_SLOWDOWN_AMOUNT = -0.1;
    public static final double FAVORED_TRADER_DISCOUNT = 0.1;
    public static final ImmutableList<EffectInstance> ESCAPER_EFFECTS = ImmutableList.of(
            new ImmutableEffectInstance(Effects.WEAKNESS, 30, 1, false, false, true),
            new ImmutableEffectInstance(Effects.MOVEMENT_SLOWDOWN, 30, 1));
    public static final double PURIFICATION_PROBABILITY = 0.5;
    public static final float DESTROYER_SPEED_MULTIPLIER = 1.5f;

    private ExtraAbilityConstants() {}
}
