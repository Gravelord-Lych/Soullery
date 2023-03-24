package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.client.SoulRenderers;
import lych.soullery.util.mixin.ITridentEntityMixin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TridentRenderer;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentRenderer.class)
public abstract class TridentRendererMixin extends EntityRenderer<TridentEntity> {
    @Shadow @Final private TridentModel model;

    private TridentRendererMixin(EntityRendererManager manager) {
        super(manager);
    }

    @Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/TridentModel;renderToBuffer(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"), cancellable = true)
    private void renderSoulFoil(TridentEntity trident, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo ci) {
        if (((ITridentEntityMixin) trident).isSoulFoil()) {
            model.renderToBuffer(stack, SoulRenderers.getSoulFoilBufferDirect(buffer, model.renderType(getTextureLocation(trident)), false), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            stack.popPose();
            super.render(trident, entityYaw, partialTicks, stack, buffer, packedLight);
            ci.cancel();
        }
    }
}
