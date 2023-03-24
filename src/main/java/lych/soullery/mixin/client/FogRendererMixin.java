package lych.soullery.mixin.client;

import lych.soullery.tag.ModFluidTags;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    @Inject(method = "setupColor",
            at = @At(
                    value = "FIELD",
                    opcode = 179, // PUTSTATIC
                    target = "Lnet/minecraft/client/renderer/FogRenderer;biomeChangedTime:J",
                    ordinal = 2,
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void setupSoulLavaColor(ActiveRenderInfo info, float partialTicks, ClientWorld world, int renderDistance, float darkenWorldAmount, CallbackInfo ci, FluidState fluidstate) {
        if (fluidstate.is(ModFluidTags.SOUL_LAVA)) {
            fogRed = 0.0F;
            fogGreen = 0.48F;
            fogBlue = 0.55F;
        }
    }
}
