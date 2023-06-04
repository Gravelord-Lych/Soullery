package lych.soullery.mixin;

import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "hasSoulSpeed", at = @At("HEAD"), cancellable = true)
    private static void handleSoulSpeed(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Reinforcements.SOUL_RABBIT.getTotalLevel(entity.getArmorSlots()) > 0) {
            cir.setReturnValue(true);
        }
    }
}
