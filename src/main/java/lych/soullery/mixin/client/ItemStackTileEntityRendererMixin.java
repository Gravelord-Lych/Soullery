package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import lych.soullery.client.SoulRenderers;
import lych.soullery.util.mixin.IItemStackMixin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemStackTileEntityRenderer.class)
public abstract class ItemStackTileEntityRendererMixin {
    @NotNull
    private ItemStack stack = ItemStack.EMPTY;

    @Inject(method = "renderByItem", at = @At(value = "HEAD"), require = 0)
    private void initItemStack(ItemStack stack, ItemCameraTransforms.TransformType type, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn, CallbackInfo ci) {
        this.stack = stack;
    }

    @Redirect(method = "renderByItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/tileentity/BannerTileEntityRenderer;renderPatterns(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/ModelRenderer;Lnet/minecraft/client/renderer/model/RenderMaterial;ZLjava/util/List;Z)V"), require = 0)
    private void renderBannerPatterns(MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, ModelRenderer renderer, RenderMaterial material, boolean isBanner, List<Pair<BannerPattern, DyeColor>> list, boolean hasFoil) {
        SoulRenderers.renderBannerPatterns(matrix, buffer, combinedLight, combinedOverlay, renderer, material, isBanner, list, stack);
    }

    @Redirect(method = "renderByItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"), require = 0)
    private IVertexBuilder getCorrectBufferDirect(IRenderTypeBuffer buffer, RenderType type, boolean useGlintDirect, boolean hasFoil) {
        if (((IItemStackMixin) (Object) stack).hasSoulFoil()) {
            return SoulRenderers.getSoulFoilBufferDirect(buffer, type, useGlintDirect);
        } else {
            return ItemRenderer.getFoilBufferDirect(buffer, type, useGlintDirect, hasFoil);
        }
    }
}
