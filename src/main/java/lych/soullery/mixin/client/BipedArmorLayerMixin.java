package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.client.SoulRenderers;
import lych.soullery.util.mixin.IItemStackMixin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BipedArmorLayer.class)
public abstract class BipedArmorLayerMixin<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> {
    @Shadow(remap = false) public abstract ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlotType slot, @Nullable String type);

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;renderModel(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V", ordinal = 0, remap = false), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void renderSoulFoilOnDyeable(MatrixStack stack, IRenderTypeBuffer buffer, T entity, EquipmentSlotType type, int packedLight, A model, CallbackInfo ci, ItemStack itemstack, ArmorItem armoritem, boolean flag, boolean flag1, int i, float f, float f1, float f2) {
        if (((IItemStackMixin) (Object) itemstack).hasSoulFoil()) {
            renderSoulFoiledModel(stack, buffer, packedLight, entity, model, f, f1, f2, getArmorResource(entity, itemstack, type, null));
            renderSoulFoiledModel(stack, buffer, packedLight, entity, model, getArmorResource(entity, itemstack, type, "overlay"));
            ci.cancel();
        }
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;renderModel(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V", ordinal = 2, remap = false), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void renderSoulFoil(MatrixStack stack, IRenderTypeBuffer buffer, T entity, EquipmentSlotType type, int packedLight, A model, CallbackInfo ci, ItemStack itemstack, ArmorItem armoritem, boolean flag, boolean flag1) {
        if (((IItemStackMixin) (Object) itemstack).hasSoulFoil()) {
            renderSoulFoiledModel(stack, buffer, packedLight, entity, model, getArmorResource(entity, itemstack, type, null));
            ci.cancel();
        }
    }

    private void renderSoulFoiledModel(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, A model, ResourceLocation armorResource) {
        renderSoulFoiledModel(stack, buffer, packedLight, entity, model, 1, 1, 1, armorResource);
    }

    private void renderSoulFoiledModel(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, A model, float r, float g, float b, ResourceLocation armorResource) {
        IVertexBuilder builder = SoulRenderers.getArmorSoulFoilBuffer(buffer, RenderType.armorCutoutNoCull(armorResource));
        model.renderToBuffer(stack, builder, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1);
    }
}
