package lych.soullery.mixin.client;

import lych.soullery.config.ConfigHelper;
import lych.soullery.util.ArrayUtils;
import lych.soullery.util.ModConstants;
import lych.soullery.util.RomanNumeralGenerator;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DisplayEffectsScreen.class)
public abstract class DisplayEffectsScreenMixin {
    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 9), require = 0)
    private int modifyMaxAmplifier(int maxAmplifier) {
        return ModConstants.MAX_SHOWABLE_EFFECT_AMPLIFIER;
    }

    @Redirect(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;get(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 1), require = 0)
    private String redirect(String key, Object[] args) {
        if (ConfigHelper.shouldUseRomanNumeralGenerator()) {
            int number = Integer.parseInt(ArrayUtils.last(key.split("\\.")));
            return RomanNumeralGenerator.getRomanNumeral(number);
        }
        return I18n.get(key, args);
    }
}
