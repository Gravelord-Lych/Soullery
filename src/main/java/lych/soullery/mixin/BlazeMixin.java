package lych.soullery.mixin;

import lych.soullery.entity.monster.boss.EnergizedBlazeEntity;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlazeEntity.class)
public class BlazeMixin {
    @SuppressWarnings("ConstantValue")
    @ModifyArg(method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playLocalSound(DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V"),
            require = 0)
    private SoundEvent modifySound(SoundEvent event) {
        return (Object) this instanceof EnergizedBlazeEntity ? ModSoundEvents.ENERGIZED_BLAZE_BURN.get() : event;
    }
}
