package lych.soullery.client.render.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.Soullery;
import lych.soullery.client.SoulRenderers;
import lych.soullery.client.render.model.EnchantedArmorStandModel;
import lych.soullery.client.render.renderer.EnchantedArmorStandRenderer;
import lych.soullery.entity.monster.boss.enchanter.EnchantedArmorStandEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class HeadBlockLayer extends LayerRenderer<EnchantedArmorStandEntity, EnchantedArmorStandModel> {
    private final EnchantedArmorStandModel model;

    public HeadBlockLayer(IEntityRenderer<EnchantedArmorStandEntity, EnchantedArmorStandModel> renderer, EnchantedArmorStandModel model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, EnchantedArmorStandEntity eas, float limbSwing, float limbSwingAmount, float partialRenderTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (eas.getSpecialType() == null) {
            return;
        }
        SoulRenderers.coloredTranslucentModelCopyLayerRender(getParentModel(), model, location(eas), stack, buffer, packedLight, eas, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialRenderTick, 1, 1, 1);
    }

    private ResourceLocation location(EnchantedArmorStandEntity eas) {
        return Soullery.prefixTex(EnchantedArmorStandRenderer.PREFIX + eas.getSpecialType().getName().toString().replace(':', '_') + ".png");
    }
}
