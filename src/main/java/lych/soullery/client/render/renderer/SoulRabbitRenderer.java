package lych.soullery.client.render.renderer;

import lych.soullery.entity.passive.SoulRabbitEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.RabbitModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulRabbitRenderer extends MobRenderer<SoulRabbitEntity, RabbitModel<SoulRabbitEntity>> {
    public SoulRabbitRenderer(EntityRendererManager manager) {
        super(manager, new RabbitModel<>(), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulRabbitEntity rabbit) {
        return rabbit.getSoulRabbitType().getTextureLocation();
    }
}
