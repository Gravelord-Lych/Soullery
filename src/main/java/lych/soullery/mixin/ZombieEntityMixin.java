package lych.soullery.mixin;

import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin {
    @Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setSecondsOnFire(I)V", shift = At.Shift.AFTER))
    private void makeTargetOnSoulFire(Entity target, CallbackInfoReturnable<Boolean> cir) {
        ((IEntityMixin) target).setFireOnSelf(((IEntityMixin) this).getFireOnSelf());
    }
}
