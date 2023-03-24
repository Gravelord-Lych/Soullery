package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.Soullery;
import lych.soullery.entity.projectile.EtherealArrowEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EtherealArrowRenderer extends EntityRenderer<EtherealArrowEntity> {
    private static final ResourceLocation ETHEREAL_ARROW = Soullery.prefixTex("entity/projectiles/ethereal_arrow.png");
    private static final ResourceLocation ENHANCED_ETHEREAL_ARROW = Soullery.prefixTex("entity/projectiles/enhanced_ethereal_arrow.png");

    public EtherealArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(EtherealArrowEntity arrow, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, arrow.yRotO, arrow.yRot) - 90));
        stack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, arrow.xRotO, arrow.xRot)));
        float shakeValue = arrow.shakeTime - partialTicks;
        if (shakeValue > 0) {
            float rotation = -MathHelper.sin(shakeValue * 3) * shakeValue;
            stack.mulPose(Vector3f.ZP.rotationDegrees(rotation));
        }
        stack.mulPose(Vector3f.XP.rotationDegrees(45));
        stack.scale(0.05625F, 0.05625F, 0.05625F);
        stack.translate(-4, 0, 0);
        IVertexBuilder builder = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(arrow)));
        MatrixStack.Entry entry = stack.last();
        Matrix4f mat4 = entry.pose();
        Matrix3f mat3 = entry.normal();
        vertex(mat4, mat3, builder, -7, -2, -2, 0, 0.15625F, -1, 0, 0, packedLight);
        vertex(mat4, mat3, builder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
        vertex(mat4, mat3, builder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
        vertex(mat4, mat3, builder, -7, 2, -2, 0, 0.3125F, -1, 0, 0, packedLight);
        vertex(mat4, mat3, builder, -7, 2, -2, 0, 0.15625F, 1, 0, 0, packedLight);
        vertex(mat4, mat3, builder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
        vertex(mat4, mat3, builder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
        vertex(mat4, mat3, builder, -7, -2, -2, 0, 0.3125F, 1, 0, 0, packedLight);
        for (int i = 0; i < 4; i++) {
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
            vertex(mat4, mat3, builder, -8, -2, 0, 0, 0, 0, 1, 0, packedLight);
            vertex(mat4, mat3, builder, 8, -2, 0, 0.5F, 0, 0, 1, 0, packedLight);
            vertex(mat4, mat3, builder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
            vertex(mat4, mat3, builder, -8, 2, 0, 0, 0.15625F, 0, 1, 0, packedLight);
        }
        stack.popPose();
        super.render(arrow, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EtherealArrowEntity arrow) {
        return arrow.isEnhanced() ? ENHANCED_ETHEREAL_ARROW : ETHEREAL_ARROW;
    }

    @Override
    protected int getBlockLightLevel(EtherealArrowEntity arrow, BlockPos pos) {
        return 15;
    }

    private void vertex(Matrix4f mat4, Matrix3f mat3, IVertexBuilder builder, int x, int y, int z, float u, float v, int nx, int ny, int nz, int light) {
        builder.vertex(mat4, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, (float) nx, (float) nz, (float) ny).endVertex();
    }
}
