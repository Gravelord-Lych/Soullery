package lych.soullery.mixin.client;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {
    @ModifyArg(method = "handleUpdateMobEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/EffectInstance;<init>(Lnet/minecraft/potion/Effect;IIZZZ)V"), index = 2, require = 0)
    private int fixNegativeAmplifierEffect(int amplifier) {
        return amplifier < 0 ? amplifier + 256 : amplifier;
    }
}
