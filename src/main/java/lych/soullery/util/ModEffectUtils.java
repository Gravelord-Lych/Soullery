package lych.soullery.util;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

public final class ModEffectUtils {
    private ModEffectUtils() {}

    public static boolean isBeneficial(EffectInstance effect) {
        return effect.getEffect().isBeneficial();
    }

    public static boolean isNeutral(EffectInstance effect) {
        return isNeutral(effect.getEffect());
    }

    public static boolean isHarmful(EffectInstance effect) {
        return isHarmful(effect.getEffect());
    }

    public static boolean isNeutral(Effect effect) {
        return effect.getCategory() == EffectType.NEUTRAL;
    }

    public static boolean isHarmful(Effect effect) {
        return effect.getCategory() == EffectType.HARMFUL;
    }

    public static EffectInstance copyAttributes(Effect effectType, EffectInstance effect) {
        int duration = effect.getDuration();
        int amplifier = effect.getAmplifier();
        boolean ambient = effect.isAmbient();
        boolean showIcon = effect.showIcon();
        boolean visible = effect.isVisible();
        return new EffectInstance(effectType, duration, amplifier, ambient, showIcon, visible);
    }

    public static EffectInstance chooseStronger(EffectInstance ea, EffectInstance eb) {
        int a = calculateEffectStrength(ea);
        int b = calculateEffectStrength(eb);
        return a > b ? ea : eb;
    }

    public static int calculateEffectStrength(EffectInstance effect) {
        return effect.getDuration() * (effect.getAmplifier() + 1);
    }
}
