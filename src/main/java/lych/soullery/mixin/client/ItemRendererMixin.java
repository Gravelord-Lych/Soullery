package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import lych.soullery.client.SoulRenderers;
import lych.soullery.util.mixin.IItemStackMixin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow public abstract void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderModelLists(Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/item/ItemStack;IILcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true, require = 0)
    private void renderSoulFoil(ItemStack stack, ItemCameraTransforms.TransformType type, boolean leftHandHackery, MatrixStack mStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, IBakedModel model, CallbackInfo ci, boolean flag, boolean flag1, RenderType rendertype) {
        if (((IItemStackMixin) (Object) stack).hasSoulFoil()) {
            IVertexBuilder newBuilder;
            if (stack.getItem() == Items.COMPASS) {
                mStack.pushPose();
                MatrixStack.Entry entry = mStack.last();

                if (type == ItemCameraTransforms.TransformType.GUI) {
                    entry.pose().multiply(0.5F);
                } else if (type.firstPerson()) {
                    entry.pose().multiply(0.75F);
                }

                if (flag1) {
                    newBuilder = SoulRenderers.getCompassSoulFoilBufferDirect(buffer, rendertype, entry);
                } else {
                    newBuilder = SoulRenderers.getCompassSoulFoilBuffer(buffer, rendertype, entry);
                }

                mStack.popPose();
            } else if (flag1) {
                newBuilder = SoulRenderers.getSoulFoilBufferDirect(buffer, rendertype, true);
            } else {
                newBuilder = SoulRenderers.getSoulFoilBuffer(buffer, rendertype, true);
            }
            renderModelLists(model, stack, combinedLight, combinedOverlay, mStack, newBuilder);
            mStack.popPose();
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;drawItemLayered(Lnet/minecraft/client/renderer/ItemRenderer;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/item/ItemStack;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IIZ)V", remap = false), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0, cancellable = true)
    private void renderSoulFoilForLayeredItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHandHackery, MatrixStack mStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, IBakedModel model, CallbackInfo ci, boolean flag, boolean fabulous) {
        if (((IItemStackMixin) (Object) stack).hasSoulFoil()) {
            for (Pair<IBakedModel, RenderType> layerModel : model.getLayerModels(stack, fabulous)) {
                IBakedModel layer = layerModel.getFirst();
                RenderType type = layerModel.getSecond();
                ForgeHooksClient.setRenderLayer(type);
                IVertexBuilder builder;
                if (fabulous) {
                    builder = SoulRenderers.getSoulFoilBufferDirect(buffer, type, true);
                } else {
                    builder = SoulRenderers.getSoulFoilBuffer(buffer, type, true);
                }
                renderModelLists(layer, stack, combinedLight, combinedOverlay, mStack, builder);
            }
            ForgeHooksClient.setRenderLayer(null);
            mStack.popPose();
            ci.cancel();
        }
    }
}
