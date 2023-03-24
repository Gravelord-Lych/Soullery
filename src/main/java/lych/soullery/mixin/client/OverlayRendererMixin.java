package lych.soullery.mixin.client;

import lych.soullery.extension.fire.Fire;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OverlayRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OverlayRenderer.class)
public abstract class OverlayRendererMixin {
    @Redirect(method = "renderFire", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/ModelBakery;FIRE_1:Lnet/minecraft/client/renderer/model/RenderMaterial;"))
    private static RenderMaterial findFireOverlay(Minecraft minecraft) {
        if (minecraft.player != null) {
            return ((IEntityMixin) minecraft.player).getFireOnSelf().getFireOverlays().getSecond();
        }
        return Fire.DEFAULT_FIRE_OVERLAYS.getSecond();
    }
}
