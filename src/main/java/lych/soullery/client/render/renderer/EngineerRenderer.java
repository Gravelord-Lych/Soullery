package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.client.render.model.EngineerModel;
import lych.soullery.entity.monster.raider.EngineerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EngineerRenderer extends IllagerRenderer<EngineerEntity> {
    private static final ResourceLocation ENGINEER = Soullery.prefixTex("entity/raider/illager/engineer.png");

    public EngineerRenderer(EntityRendererManager manager) {
        super(manager, new EngineerModel(), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EngineerEntity engineer) {
        return ENGINEER;
    }
}
