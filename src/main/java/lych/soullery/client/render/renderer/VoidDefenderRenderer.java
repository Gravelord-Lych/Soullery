package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.entity.monster.voidwalker.VoidDefenderEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidDefenderRenderer extends AbstractVoidwalkerRenderer<VoidDefenderEntity> {
    private static final ResourceLocation VOID_DEFENDER_CLOTHES = Soullery.prefixTex("entity/esv/void_defender_clothes.png");

    public VoidDefenderRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getOuterLayer(VoidDefenderEntity voidwalker) {
        return VOID_DEFENDER_CLOTHES;
    }
}
