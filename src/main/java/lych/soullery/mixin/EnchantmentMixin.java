package lych.soullery.mixin;

import lych.soullery.config.ConfigHelper;
import lych.soullery.util.RomanNumeralGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Redirect(method = "getFullname",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I")),
            at = @At(value = "NEW", target = "net/minecraft/util/text/TranslationTextComponent", remap = false),
            require = 0)
    private TranslationTextComponent redirect(String name, int level) {
        if (ConfigHelper.shouldUseRomanNumeralGenerator()) {
            return new TranslationTextComponent(RomanNumeralGenerator.getRomanNumeral(level));
        }
        return new TranslationTextComponent(name);
    }
}
