package lych.soullery.mixin;

import lych.soullery.entity.functional.HorcruxEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZoglinEntity.class)
public class ZoglinEntityMixin {
    @Inject(method = "isTargetable", at = @At("HEAD"), cancellable = true)
    private static void doNotAttackHorcrux(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof HorcruxEntity) {
            cir.setReturnValue(false);
        }
    }
}
