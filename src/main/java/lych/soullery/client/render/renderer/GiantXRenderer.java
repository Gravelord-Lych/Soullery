package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.entity.monster.boss.GiantXEntity;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantXRenderer extends AbstractZombieRenderer<GiantXEntity, ZombieModel<GiantXEntity>> {
    public GiantXRenderer(EntityRendererManager manager) {
        super(manager, new ZombieModel<>(0, false), new ZombieModel<>(0.5f, true), new ZombieModel<>(1, true));
    }

    @Override
    protected void scale(GiantXEntity giant, MatrixStack stack, float scale) {
        stack.scale(6, 6, 6);
    }
}
