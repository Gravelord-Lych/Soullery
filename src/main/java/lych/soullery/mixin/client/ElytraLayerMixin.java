package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.client.SoulRenderers;
import lych.soullery.util.mixin.IItemStackMixin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.ElytraModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ElytraLayer.class)
public abstract class ElytraLayerMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow @Final private ElytraModel<T> elytraModel;

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/ElytraModel;renderToBuffer(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void renderSoulFoil(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialRenderTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci, ItemStack itemstack, ResourceLocation resourcelocation, IVertexBuilder ivertexbuilder) {
        if (((IItemStackMixin) (Object) itemstack).hasSoulFoil()) {
            elytraModel.renderToBuffer(stack, SoulRenderers.getArmorSoulFoilBuffer(buffer, RenderType.armorCutoutNoCull(resourcelocation), false), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            stack.popPose();
            ci.cancel();
        }
    }
}
