package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.entity.monster.voidwalker.VoidAlchemistEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidAlchemistRenderer extends AbstractVoidwalkerRenderer<VoidAlchemistEntity> {
    private static final ResourceLocation VOID_ALCHEMIST_CLOTHES = Soullery.prefixTex("entity/esv/void_alchemist_clothes.png");

    public VoidAlchemistRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getOuterLayer(VoidAlchemistEntity alchemist) {
        return VOID_ALCHEMIST_CLOTHES;
    }
}
