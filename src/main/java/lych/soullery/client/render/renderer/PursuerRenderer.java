package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.Soullery;
import lych.soullery.entity.projectile.PursuerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ShulkerBulletModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PursuerRenderer extends EntityRenderer<PursuerEntity> {
    private static final ResourceLocation PURSUER = Soullery.prefixTex("entity/esv/pursuer_1.png");
    private static final ResourceLocation PURSUER_TYPE_2 = Soullery.prefixTex("entity/esv/pursuer_2.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(PURSUER);
    private static final RenderType RENDER_TYPE_2 = RenderType.entityTranslucent(PURSUER_TYPE_2);
    private static final float SCALE = 1.25f;

    private final ShulkerBulletModel<PursuerEntity> model = new ShulkerBulletModel<>();

    public PursuerRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    protected int getBlockLightLevel(PursuerEntity pursuer, BlockPos pos) {
        return 15;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(PursuerEntity pursuer, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        stack.pushPose();
        float yRot = MathHelper.rotlerp(pursuer.yRotO, pursuer.yRot, partialTicks);
        float xRot = MathHelper.lerp(partialTicks, pursuer.xRotO, pursuer.xRot);
        float ticks = (float) pursuer.tickCount + partialTicks;
        stack.translate(0, 0.15, 0);
        stack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.sin(ticks * 0.1f) * 180));
        stack.mulPose(Vector3f.XP.rotationDegrees(MathHelper.cos(ticks * 0.1f) * 180));
        stack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.sin(ticks * 0.15f) * 360));
        stack.scale(-0.5f, -0.5f, 0.5f);
        model.setupAnim(pursuer, 0, 0, 0, yRot, xRot);
        IVertexBuilder builder = buffer.getBuffer(model.renderType(pursuer.isAltType() ? PURSUER_TYPE_2 : PURSUER));
        model.renderToBuffer(stack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        stack.scale(SCALE, SCALE, SCALE);
        IVertexBuilder builder1 = buffer.getBuffer(pursuer.isAltType() ? RENDER_TYPE_2 : RENDER_TYPE);
        model.renderToBuffer(stack, builder1, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.15f);
        stack.popPose();
        super.render(pursuer, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PursuerEntity pursuer) {
        return pursuer.isAltType() ? PURSUER_TYPE_2 : PURSUER;
    }
}