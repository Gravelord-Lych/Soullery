package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.entity.GravitationalDragonFireballEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GravitationalDragonFireballRenderer extends EntityRenderer<GravitationalDragonFireballEntity> {
    private static final ResourceLocation DRAGON_FIREBALL = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_FIREBALL);

    public GravitationalDragonFireballRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    protected int getBlockLightLevel(GravitationalDragonFireballEntity fireball, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(GravitationalDragonFireballEntity fireball, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        stack.pushPose();
        stack.scale(1, 1, 1);
        stack.mulPose(entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        MatrixStack.Entry matrixstack$entry = stack.last();
        Matrix4f mat4 = matrixstack$entry.pose();
        Matrix3f mat3 = matrixstack$entry.normal();
        IVertexBuilder builder = buffer.getBuffer(RENDER_TYPE);
        vertex(builder, mat4, mat3, packedLight, 0, 0, 0, 1);
        vertex(builder, mat4, mat3, packedLight, 1, 0, 1, 1);
        vertex(builder, mat4, mat3, packedLight, 1, 1, 1, 0);
        vertex(builder, mat4, mat3, packedLight, 0, 1, 0, 0);
        stack.popPose();
        super.render(fireball, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    private static void vertex(IVertexBuilder builder, Matrix4f mat4, Matrix3f mat3, int packedLight, float x, float y, int u, int v) {
        builder.vertex(mat4, x - 0.5f, y - 0.25f, 0).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(GravitationalDragonFireballEntity fireball) {
        return DRAGON_FIREBALL;
    }
}
