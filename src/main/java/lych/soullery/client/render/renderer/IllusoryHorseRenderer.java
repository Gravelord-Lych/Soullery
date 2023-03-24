package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.client.render.model.IllusoryHorseModel;
import lych.soullery.entity.passive.IllusoryHorseEntity;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllusoryHorseRenderer extends AbstractHorseRenderer<IllusoryHorseEntity, IllusoryHorseModel<IllusoryHorseEntity>> {
    private static final ResourceLocation ILLUSORY_HORSE = Soullery.prefixTex("entity/esv/illusory_horse.png");
    private static final ResourceLocation ILLUSORY_HORSE_ETHEREAL = Soullery.prefixTex("entity/esv/illusory_horse_ethereal.png");

    public IllusoryHorseRenderer(EntityRendererManager manager) {
        super(manager, new IllusoryHorseModel<>(0), 1);
    }

    @Override
    public ResourceLocation getTextureLocation(IllusoryHorseEntity horse) {
        return horse.isEthereal() ? ILLUSORY_HORSE_ETHEREAL : ILLUSORY_HORSE;
    }
}
