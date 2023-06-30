package lych.soullery.client.render;

import lych.soullery.Soullery;
import lych.soullery.client.render.model.EnchanterModel;
import lych.soullery.entity.monster.boss.enchanter.EnchanterEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class EnchanterRenderer extends BipedRenderer<EnchanterEntity, EnchanterModel> {
    private static final ResourceLocation ENCHANTER = Soullery.prefixTex("entity/enchanter.png");
    private static final ResourceLocation ENCHANTER_INVUL = Soullery.prefixTex("entity/enchanter_invulnerable.png");

    public EnchanterRenderer(EntityRendererManager manager) {
        super(manager, new EnchanterModel(), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EnchanterEntity enchanter) {
        return enchanter.isEthereallyInvulnerable() ? ENCHANTER_INVUL : ENCHANTER;
    }
}
