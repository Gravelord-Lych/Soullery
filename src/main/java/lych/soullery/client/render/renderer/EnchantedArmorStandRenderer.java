package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.Soullery;
import lych.soullery.client.render.model.EnchantedArmorStandModel;
import lych.soullery.client.render.renderer.layer.HeadBlockLayer;
import lych.soullery.entity.monster.boss.enchanter.EnchantedArmorStandEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantedArmorStandRenderer extends BipedRenderer<EnchantedArmorStandEntity, EnchantedArmorStandModel> {
    public static final String PREFIX = "entity/eas/";
    private static final ResourceLocation ENCHANTED_ARMOR_STAND_DEFAULT = Soullery.prefixTex(PREFIX + "default.png");
    private static final float MIN_SCALE = 0.2f;

    public EnchantedArmorStandRenderer(EntityRendererManager manager) {
        super(manager, new EnchantedArmorStandModel(), 0);
        addLayer(new BipedArmorLayer<>(this, new EnchantedArmorStandModel(0.5f), new EnchantedArmorStandModel(1)));
        addLayer(new HeadBlockLayer(this, new EnchantedArmorStandModel()));
        shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(EnchantedArmorStandEntity eas) {
        return ENCHANTED_ARMOR_STAND_DEFAULT;
    }

    @Override
    protected void scale(EnchantedArmorStandEntity eas, MatrixStack stack, float partialTicks) {
        if (eas.getSpawnInvulTicks() > 0) {
            float scale = 0.2f;
            scale += (20 - eas.getSpawnInvulTicks() + partialTicks) / 20 * 0.8f;
            stack.scale(scale, scale, scale);
        }
    }
}
