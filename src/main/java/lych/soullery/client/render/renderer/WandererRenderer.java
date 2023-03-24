package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.Soullery;
import lych.soullery.entity.monster.WandererEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WandererRenderer extends BipedRenderer<WandererEntity, BipedModel<WandererEntity>> {
    private static final ResourceLocation WANDERER = Soullery.prefixTex("entity/wanderer.png");
    private static final ResourceLocation WANDERER_ATTACKING = Soullery.prefixTex("entity/wanderer_attacking.png");

    public WandererRenderer(EntityRendererManager manager) {
        super(manager, new BipedModel<WandererEntity>(0, 0, 64, 64){}, 0.6f);
    }

    @Override
    protected void scale(WandererEntity wanderer, MatrixStack stack, float scale) {
        stack.scale(1.2f, 1.2f, 1.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(WandererEntity wanderer) {
        return wanderer.isAttacking() ? WANDERER_ATTACKING : WANDERER;
    }
}
