package lych.soullery.client.render.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DynamicEyesLayer<T extends Entity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    public DynamicEyesLayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialRenderTick, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder builder = buffer.getBuffer(renderType(entity));
        getParentModel().renderToBuffer(stack, builder, 0xf00000, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }

    public abstract RenderType renderType(T entity);
}
