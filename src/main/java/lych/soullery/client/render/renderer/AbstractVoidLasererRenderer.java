package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.monster.voidwalker.AbstractVoidLasererEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractVoidLasererRenderer<T extends AbstractVoidLasererEntity<T>> extends AbstractVoidwalkerRenderer<T> {
    protected static final float HALF_PI = (float) (Math.PI / 2);
    protected final int renderCount;

    protected AbstractVoidLasererRenderer(EntityRendererManager manager) {
        this(manager, 12);
    }

    protected AbstractVoidLasererRenderer(EntityRendererManager manager, int renderCount) {
        super(manager);
        this.renderCount = renderCount;
    }

    @Override
    public void render(T laserer, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        super.render(laserer, entityYaw, partialTicks, stack, buffer, packedLight);
        renderLaser(laserer, partialTicks, stack, buffer, packedLight);
    }

    protected void renderLaser(T laserer, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        Entity target = laserer.getLaserTarget();
        if (target != null) {
            Vector3d vecToTarget = laserer.getSelfPosition(partialTicks).vectorTo(laserer.getTargetPosition(target, partialTicks));
            float tx = (float) vecToTarget.x;
            float ty = (float) vecToTarget.y;
            float tz = (float) vecToTarget.z;
            renderLaserTo(laserer, target, tx, ty, tz, partialTicks, laserer.tickCount, stack, buffer, packedLight);
        }
    }

    protected void renderLaserTo(T laserer, Entity target, float tx, float ty, float tz, float partialTicks, int tickCount, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        float hDist = MathHelper.sqrt(tx * tx + tz * tz);
        float dist = MathHelper.sqrt(tx * tx + ty * ty + tz * tz);
        stack.pushPose();
        stack.translate(0, laserer.getEyeOffset(), 0);
        stack.mulPose(Vector3f.YP.rotation((float) (-Math.atan2(tz, tx)) - HALF_PI));
        stack.mulPose(Vector3f.XP.rotation((float) (-Math.atan2(hDist, ty)) - HALF_PI));
        IVertexBuilder builder = buffer.getBuffer(makeRenderType(laserer, target));
        float v1 = -((float) tickCount + partialTicks) * 0.01f;
        float v2 = dist / 32 - ((float) tickCount + partialTicks) * 0.01f;
        float x1 = 0;
        float y1 = 0.75f;
        float u1 = 0;
        MatrixStack.Entry entry = stack.last();
        Matrix4f mat4 = entry.pose();
        Matrix3f mat3 = entry.normal();
        for (int i = 1; i <= renderCount; ++i) {
            float x2 = MathHelper.sin((float) i * ((float) Math.PI * 2F) / renderCount) * 0.75f;
            float y2 = MathHelper.cos((float) i * ((float) Math.PI * 2F) / renderCount) * 0.75f;
            float u2 = (float) i / renderCount;
            int sr = getSrcColor(laserer, target) >> 16 & 255;
            int sg = getSrcColor(laserer, target) >> 8 & 255;
            int sb = getSrcColor(laserer, target) & 255;
            int dr = getDestColor(laserer, target) >> 16 & 255;
            int dg = getDestColor(laserer, target) >> 8 & 255;
            int db = getDestColor(laserer, target) & 255;
            builder.vertex(mat4, x1 * getSrcScale(laserer, target), y1 * getSrcScale(laserer, target), 0).color(sr, sg, sb, 255).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            builder.vertex(mat4, x1 * getDestScale(laserer, target), y1 * getDestScale(laserer, target), dist).color(dr, dg, db, 255).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            builder.vertex(mat4, x2 * getDestScale(laserer, target), y2 * getDestScale(laserer, target), dist).color(dr, dg, db, 255).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            builder.vertex(mat4, x2 * getSrcScale(laserer, target), y2 * getSrcScale(laserer, target), 0).color(sr, sg, sb, 255).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, -1, 0).endVertex();
            x1 = x2;
            y1 = y2;
            u1 = u2;
        }
        stack.popPose();
    }

    @Override
    public boolean shouldRender(T laserer, ClippingHelper helper, double x, double y, double z) {
        return super.shouldRender(laserer, helper, x, y, z) || laserer.getLaserTarget() != null;
    }

    protected RenderType makeRenderType(T laserer, Entity target) {
        AbstractVoidLasererEntity.ILaserProvider<? super T> provider = laserer.provideLaser();
        if (provider == null) {
            throwErrorIfFailhard();
            return RenderType.cutout();
        }
        return RenderType.entitySmoothCutout(provider.getTextureLocation(laserer, target));
    }

    protected int getSrcColor(T laserer, Entity target) {
        AbstractVoidLasererEntity.ILaserProvider<? super T> provider = laserer.provideLaser();
        if (provider == null) {
            throwErrorIfFailhard();
            return 0;
        }
        return provider.getSrcColor(laserer, target);
    }

    protected int getDestColor(T laserer, Entity target) {
        AbstractVoidLasererEntity.ILaserProvider<? super T> provider = laserer.provideLaser();
        if (provider == null) {
            throwErrorIfFailhard();
            return 0;
        }
        return provider.getDestColor(laserer, target);
    }

    protected float getSrcScale(T laserer, Entity target) {
        AbstractVoidLasererEntity.ILaserProvider<? super T> provider = laserer.provideLaser();
        if (provider == null) {
            throwErrorIfFailhard();
            return AbstractVoidLasererEntity.ILaserProvider.DEFAULT_LASER_SCALE;
        }
        return provider.getSrcScale(laserer, target);
    }

    protected float getDestScale(T laserer, Entity target) {
        AbstractVoidLasererEntity.ILaserProvider<? super T> provider = laserer.provideLaser();
        if (provider == null) {
            throwErrorIfFailhard();
            return AbstractVoidLasererEntity.ILaserProvider.DEFAULT_LASER_SCALE;
        }
        return provider.getDestScale(laserer, target);
    }

    private static void throwErrorIfFailhard() {
        if (ConfigHelper.shouldFailhard()) {
            throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + "Laser target is non-null but Laser provider is null. This should never happen");
        }
    }
}
