package lych.soullery.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.mojang.datafixers.util.Pair;
import lych.soullery.util.mixin.IItemStackMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SoulRenderers {
    public static IVertexBuilder getCompassSoulFoilBufferDirect(IRenderTypeBuffer buffer, RenderType type, MatrixStack.Entry entry) {
        return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(buffer.getBuffer(ModRenderTypes.SOUL_GLINT_DIRECT), entry.pose(), entry.normal()), buffer.getBuffer(type));
    }

    public static IVertexBuilder getCompassSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type, MatrixStack.Entry entry) {
        return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(buffer.getBuffer(ModRenderTypes.SOUL_GLINT), entry.pose(), entry.normal()), buffer.getBuffer(type));
    }

    public static IVertexBuilder getSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type, boolean useGlint) {
        if (Minecraft.useShaderTransparency() && type == Atlases.translucentItemSheet()) {
            return VertexBuilderUtils.create(buffer.getBuffer(ModRenderTypes.SOUL_GLINT_TRANSLUCENT), buffer.getBuffer(type));
        } else {
            return VertexBuilderUtils.create(buffer.getBuffer(useGlint ? ModRenderTypes.SOUL_GLINT : ModRenderTypes.ENTITY_SOUL_GLINT), buffer.getBuffer(type));
        }
    }

    public static IVertexBuilder getSoulFoilBufferDirect(IRenderTypeBuffer buffer, RenderType type, boolean useGlintDirect) {
        return VertexBuilderUtils.create(buffer.getBuffer(useGlintDirect ? ModRenderTypes.SOUL_GLINT_DIRECT : ModRenderTypes.ENTITY_SOUL_GLINT_DIRECT), buffer.getBuffer(type));
    }

    public static IVertexBuilder getArmorSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type) {
        return getArmorSoulFoilBuffer(buffer, type, false);
    }

    public static IVertexBuilder getArmorSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type, boolean useArmorGlint) {
        return VertexBuilderUtils.create(buffer.getBuffer(useArmorGlint ? ModRenderTypes.ARMOR_SOUL_GLINT : ModRenderTypes.ARMOR_ENTITY_SOUL_GLINT), buffer.getBuffer(type));
    }

    @SuppressWarnings("resource")
    public static void renderBannerPatterns(MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, ModelRenderer renderer, RenderMaterial material, boolean isBanner, List<Pair<BannerPattern, DyeColor>> list, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (((IItemStackMixin) (Object) stack).hasSoulFoil()) {
            renderer.render(matrix, material.sprite().wrap(getSoulFoilBufferDirect(buffer, material.renderType(RenderType::entitySolid), true)), combinedLight, combinedOverlay);
        } else {
            renderer.render(matrix, material.buffer(buffer, RenderType::entitySolid, stack.hasFoil()), combinedLight, combinedOverlay);
        }
        for (int i = 0; i < 17 && i < list.size(); ++i) {
            Pair<BannerPattern, DyeColor> pair = list.get(i);
            float[] diffuseColors = pair.getSecond().getTextureDiffuseColors();
            RenderMaterial newMaterial = new RenderMaterial(isBanner ? Atlases.BANNER_SHEET : Atlases.SHIELD_SHEET, pair.getFirst().location(isBanner));
            renderer.render(matrix, newMaterial.buffer(buffer, RenderType::entityNoOutline), combinedLight, combinedOverlay, diffuseColors[0], diffuseColors[1], diffuseColors[2], 1.0F);
        }
    }

    public static void translate(MatrixStack matrixStack) {
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
    }

    public static void rotateArmsToCastSpell(BipedModel<?> model, float ageInTicks) {
        model.rightArm.z = 0.0F;
        model.rightArm.x = -5.0F;
        model.leftArm.z = 0.0F;
        model.leftArm.x = 5.0F;
        model.rightArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
        model.leftArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
        model.rightArm.zRot = 2.3561945F;
        model.leftArm.zRot = -2.3561945F;
        model.rightArm.yRot = 0.0F;
        model.leftArm.yRot = 0.0F;
    }

    public static <T extends LivingEntity> void coloredTranslucentModelCopyLayerRender(EntityModel<T> parentModel, EntityModel<T> model, ResourceLocation location, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialRenderTick, float r, float g, float b) {
        coloredTranslucentModelCopyLayerRender(parentModel, model, location, stack, buffer, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialRenderTick, r, g, b, 1);
    }

    public static <T extends LivingEntity> void coloredTranslucentModelCopyLayerRender(EntityModel<T> parentModel, EntityModel<T> model, ResourceLocation location, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialRenderTick, float r, float g, float b, float a) {
        if (!entity.isInvisible()) {
            parentModel.copyPropertiesTo(model);
            model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialRenderTick);
            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            renderColoredTranslucentModel(model, location, stack, buffer, packedLight, entity, r, g, b, a);
        }
    }

    public static <T extends LivingEntity> void renderColoredTranslucentModel(EntityModel<T> parentModel, ResourceLocation location, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, float r, float g, float b, float a) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.entityTranslucent(location));
        parentModel.renderToBuffer(stack, builder, packedLight, LivingRenderer.getOverlayCoords(entity, 0), r, g, b, a);
    }
}
