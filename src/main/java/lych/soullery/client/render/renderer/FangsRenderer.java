package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.entity.functional.FangsEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EvokerFangsModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FangsRenderer extends EntityRenderer<FangsEntity> {
    private final EvokerFangsModel<EvokerFangsEntity> model = new EvokerFangsModel<>();

    public FangsRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(FangsEntity fangs, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        float progress = fangs.getAnimationProgress(partialTicks);
        if (progress != 0) {
            float scale = 2;
            if (progress > 0.9) {
                scale = (float) (scale * (1 - progress) / 0.1);
            }
            stack.pushPose();
            stack.mulPose(Vector3f.YP.rotationDegrees(90.0F - fangs.yRot));
            stack.scale(-scale, -scale, scale);
            stack.translate(0.0D, (double)-0.626F, 0.0D);
            stack.scale(0.5F, 0.5F, 0.5F);
            model.setupAnim(fangs, progress, 0.0F, 0.0F, fangs.yRot, fangs.xRot);
            IVertexBuilder ivertexbuilder = buffer.getBuffer(model.renderType(fangs.getTextureLocation()));
            model.renderToBuffer(stack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            stack.popPose();
            super.render(fangs, entityYaw, partialTicks, stack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FangsEntity fangs) {
        return fangs.getTextureLocation();
    }
}