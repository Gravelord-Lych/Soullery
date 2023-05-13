package lych.soullery.client.render.renderer;

import lych.soullery.entity.functional.HorcruxEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class HorcruxRenderer extends BipedRenderer<HorcruxEntity, PlayerModel<HorcruxEntity>> {
    private final PlayerModel<HorcruxEntity> normalModel;
    private final PlayerModel<HorcruxEntity> slimModel;

    public HorcruxRenderer(EntityRendererManager manager) {
        super(manager, new PlayerModel<>(0, false), 0.5f);
        normalModel = getModel();
        slimModel = new PlayerModel<>(0, true);
        addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(0.5F)));
    }

    @Override
    public ResourceLocation getTextureLocation(HorcruxEntity horcrux) {
        Minecraft mc = Minecraft.getInstance();
        boolean slim = false;
        ResourceLocation texture = DefaultPlayerSkin.getDefaultSkin();
        if (mc.getCameraEntity() instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity client = (AbstractClientPlayerEntity) mc.getCameraEntity();
            texture = client.getSkinTextureLocation();
            slim = client.getModelName().equals("slim");
        }
        model = slim ? slimModel : normalModel;
        return texture;
    }
}
