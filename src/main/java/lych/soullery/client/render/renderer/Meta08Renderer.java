package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.Soullery;
import lych.soullery.client.render.model.BipedModels;
import lych.soullery.client.render.renderer.layer.EnergyShieldLayer;
import lych.soullery.client.render.renderer.layer.Meta08EyesLayer;
import lych.soullery.entity.monster.boss.Meta08Entity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Meta08Renderer extends BipedRenderer<Meta08Entity, BipedModels.Size64<Meta08Entity>> {
    private static final ResourceLocation META_08 = Soullery.prefixTex("entity/meta8/meta8.png");

    public Meta08Renderer(EntityRendererManager manager) {
        super(manager, new BipedModels.Size64<>(), 1);
        addLayer(new Meta08EyesLayer(this));
        addLayer(new EnergyShieldLayer<>(this, new BipedModels.Size64<>(0.5f)));
    }

    @Override
    protected void scale(Meta08Entity meta8, MatrixStack stack, float scale) {
        stack.scale(2, 2, 2);
    }

    @Override
    public ResourceLocation getTextureLocation(Meta08Entity meta8) {
        return META_08;
    }
}
