package lych.soullery.client.render.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.client.render.model.BipedModels;
import lych.soullery.client.render.model.VoidwalkerModel;
import lych.soullery.client.render.renderer.AbstractVoidwalkerRenderer;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class VoidwalkerClothesLayer<T extends AbstractVoidwalkerEntity> extends LayerRenderer<T, BipedModels.Size64<T>> {
    private final Function<? super T, ? extends ResourceLocation> clothesGetter;
    private final VoidwalkerModel<T> model = new VoidwalkerModel<>(0.25f);

    public VoidwalkerClothesLayer(AbstractVoidwalkerRenderer<T> renderer) {
        super(renderer);
        clothesGetter = renderer::getOuterLayer;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T voidwalker, float limbSwing, float limbSwingAmount, float partialRenderTick, float ageInTicks, float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(getParentModel(), model, clothesGetter.apply(voidwalker), stack, buffer, packedLight, voidwalker, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialRenderTick, 1, 1, 1);
    }
}
