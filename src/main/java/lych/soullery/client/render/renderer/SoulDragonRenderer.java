package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.Soullery;
import lych.soullery.client.render.model.SoulDragonModel;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;
import java.util.Random;

/**
 * [VanillaCopy]
 * {@link net.minecraft.client.renderer.entity.EnderDragonRenderer EnderDragonRenderer}
 */
@OnlyIn(Dist.CLIENT)
public class SoulDragonRenderer extends EntityRenderer<SoulDragonEntity> {
    private static final ResourceLocation DRAGON_EXPLODING = Soullery.prefixTex("entity/souldragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON = Soullery.prefixTex("entity/souldragon/dragon.png");
    private static final ResourceLocation DRAGON_PURE = Soullery.prefixTex("entity/souldragon/dragon_pure.png");
    private static final ResourceLocation DRAGON_EYES = Soullery.prefixTex("entity/souldragon/dragon_eyes.png");
    private static final ResourceLocation DRAGON_EYES_PURE = Soullery.prefixTex("entity/souldragon/dragon_eyes_pure.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON);
    private static final RenderType RENDER_TYPE_PURE = RenderType.entityCutoutNoCull(DRAGON_PURE);
    private static final RenderType DECAL = RenderType.entityDecal(DRAGON);
    private static final RenderType DECAL_PURE = RenderType.entityDecal(DRAGON_PURE);
    private static final RenderType EYES = RenderType.eyes(DRAGON_EYES);
    private static final RenderType EYES_PURE = RenderType.eyes(DRAGON_EYES_PURE);
    private static final RenderType BEAM = RenderType.entitySmoothCutout(EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3) / 2);
    private final SoulDragonModel model = new SoulDragonModel();

    public SoulDragonRenderer(EntityRendererManager manager) {
        super(manager);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(SoulDragonEntity dragon, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        stack.pushPose();
        float f = (float)dragon.getLatencyPos(7, partialTicks)[0];
        float f1 = (float)(dragon.getLatencyPos(5, partialTicks)[1] - dragon.getLatencyPos(10, partialTicks)[1]);
        stack.mulPose(Vector3f.YP.rotationDegrees(-f));
        stack.mulPose(Vector3f.XP.rotationDegrees(f1 * 10.0F));
        stack.translate(0.0D, 0.0D, 1.0D);
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.translate(0.0D, -1.501F, 0.0D);
        boolean hurt = dragon.hurtTime > 0;
        model.prepareMobModel(dragon, 0.0F, 0.0F, partialTicks);
        if (dragon.dragonDeathTime > 0) {
            float deathAmount = (float) dragon.dragonDeathTime / 200.0F;
            IVertexBuilder explosion = buffer.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING, deathAmount));
            model.renderToBuffer(stack, explosion, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            IVertexBuilder decal = buffer.getBuffer(dragon.isPurified() ? DECAL_PURE : DECAL);
            model.renderToBuffer(stack, decal, packedLight, OverlayTexture.pack(0.0F, hurt), 1, 1, 1, 1);
        } else {
            IVertexBuilder main = buffer.getBuffer(dragon.isPurified() ? RENDER_TYPE_PURE : RENDER_TYPE);
            model.renderToBuffer(stack, main, packedLight, OverlayTexture.pack(0.0F, hurt), 1, 1, 1, 1);
        }

        IVertexBuilder eye = buffer.getBuffer(dragon.isPurified() ? EYES_PURE : EYES);
        model.renderToBuffer(stack, eye, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        if (dragon.dragonDeathTime > 0) {
            float deathAmount = ((float) dragon.dragonDeathTime + partialTicks) / 200.0F;
            float alphaMod = Math.min(deathAmount > 0.8F ? (deathAmount - 0.8F) / 0.2F : 0.0F, 1.0F);
            Random random = new Random(432L);
            IVertexBuilder lightning = buffer.getBuffer(RenderType.lightning());
            stack.pushPose();
            stack.translate(0.0D, -1.0D, -2.0D);

            for(int i = 0; i < (deathAmount + deathAmount * deathAmount) / 2 * 60; i++) {
                stack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360));
                stack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360));
                stack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360));
                stack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360));
                stack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360));
                stack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360 + deathAmount * 90));
                float f3 = random.nextFloat() * 20.0F + 5.0F + alphaMod * 10.0F;
                float f4 = random.nextFloat() * 2.0F + 1.0F + alphaMod * 2.0F;
                Matrix4f matrix4f = stack.last().pose();
                int alpha = (int)(255.0F * (1 - alphaMod));
                vertex01(lightning, matrix4f, alpha);
                vertex2(lightning, matrix4f, f3, f4, dragon.isPurified());
                vertex3(lightning, matrix4f, f3, f4, dragon.isPurified());
                vertex01(lightning, matrix4f, alpha);
                vertex3(lightning, matrix4f, f3, f4, dragon.isPurified());
                vertex4(lightning, matrix4f, f3, f4, dragon.isPurified());
                vertex01(lightning, matrix4f, alpha);
                vertex4(lightning, matrix4f, f3, f4, dragon.isPurified());
                vertex2(lightning, matrix4f, f3, f4, dragon.isPurified());
            }

            stack.popPose();
        }

        stack.popPose();
        if (dragon.nearestCrystal != null) {
            stack.pushPose();
            float tx = (float) (dragon.nearestCrystal.getX() - MathHelper.lerp(partialTicks, dragon.xo, dragon.getX()));
            float ty = (float) (dragon.nearestCrystal.getY() - MathHelper.lerp(partialTicks, dragon.yo, dragon.getY()));
            float tz = (float) (dragon.nearestCrystal.getZ() - MathHelper.lerp(partialTicks, dragon.zo, dragon.getZ()));
            renderSoulCrystalBeams(tx, ty + EnderCrystalRenderer.getY(dragon.nearestCrystal, partialTicks), tz, partialTicks, dragon.tickCount, stack, buffer, packedLight);
            stack.popPose();
        }

        super.render(dragon, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    private static void vertex01(IVertexBuilder builder, Matrix4f matrix4f, int a) {
        builder.vertex(matrix4f, 0, 0, 0).color(245, 255, 255, a).endVertex();
        builder.vertex(matrix4f, 0, 0, 0).color(245, 255, 255, a).endVertex();
    }

    private static void vertex2(IVertexBuilder builder, Matrix4f matrix4f, float y, float xzMul, boolean purified) {
        builder.vertex(matrix4f, -HALF_SQRT_3 * xzMul, y, -0.5F * xzMul).color(0, purified ? 128 : 255, 255, 0).endVertex();
    }

    private static void vertex3(IVertexBuilder builder, Matrix4f matrix4f, float y, float xzMul, boolean purified) {
        builder.vertex(matrix4f, HALF_SQRT_3 * xzMul, y, -0.5F * xzMul).color(0, purified ? 128 : 255, 255, 0).endVertex();
    }

    private static void vertex4(IVertexBuilder builder, Matrix4f matrix4f, float y, float z, boolean purified) {
        builder.vertex(matrix4f, 0, y, z).color(0, purified ? 128 : 255, 255, 0).endVertex();
    }

    public static void renderSoulCrystalBeams(float tx, float ty, float tz, float partialTicks, int tickCount,   MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        renderSoulCrystalBeams(tx, ty, tz, partialTicks, tickCount, new Color(200, 255, 255, 255), new Color(0, 22, 33, 255), stack, buffer, packedLight);
    }

    public static void renderSoulCrystalBeams(float tx, float ty, float tz, float partialTicks, int tickCount, Color color1, Color color2, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        float hDist = MathHelper.sqrt(tx * tx + tz * tz);
        float distSqr = tx * tx + ty * ty + tz * tz;
        float dist = MathHelper.sqrt(distSqr);
        stack.pushPose();
        stack.translate(0.0D, 2.0D, 0.0D);
        stack.mulPose(Vector3f.YP.rotation((float)(-Math.atan2(tz, tx)) - ((float)Math.PI / 2F)));
        stack.mulPose(Vector3f.XP.rotation((float)(-Math.atan2(hDist, ty)) - ((float)Math.PI / 2F)));
        IVertexBuilder builder = buffer.getBuffer(BEAM);
        float v1 = -((float)tickCount + partialTicks) * 0.01F;
        float v2 = MathHelper.sqrt(distSqr) / 32.0F - ((float)tickCount + partialTicks) * 0.01F;
        int cnt = 8;
        float x1 = 0.0F;
        float y1 = 0.75F;
        float u1 = 0.0F;
        MatrixStack.Entry entry = stack.last();
        Matrix4f mat4 = entry.pose();
        Matrix3f mat3 = entry.normal();

        for (int i = 1; i <= cnt; i++) {
            float x2 = MathHelper.sin((float)i * ((float)Math.PI * 2F) / 8.0F) * 0.75F;
            float y2 = MathHelper.cos((float)i * ((float)Math.PI * 2F) / 8.0F) * 0.75F;
            float u2 = (float) i / 8;
            float tScaleOffs = 0.2F;
            builder.vertex(mat4, x1 * tScaleOffs, y1 * tScaleOffs, 0.0F).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            builder.vertex(mat4, x1, y1, dist).color(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            builder.vertex(mat4, x2, y2, dist).color(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            builder.vertex(mat4, x2 * tScaleOffs, y2 * tScaleOffs, 0.0F).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            x1 = x2;
            y1 = y2;
            u1 = u2;
        }

        stack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SoulDragonEntity dragon) {
        return dragon.isPurified() ? DRAGON_PURE : DRAGON;
    }
}
