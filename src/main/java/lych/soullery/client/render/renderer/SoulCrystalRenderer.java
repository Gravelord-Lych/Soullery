package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.Soullery;
import lych.soullery.entity.functional.SoulCrystalEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulCrystalRenderer<T extends SoulCrystalEntity> extends EntityRenderer<T> {
    private static final float BASE_SCALE = 2;
    private static final float OFFSET_SCALE = 0.875f;
    private static final ResourceLocation SOUL_CRYSTAL = Soullery.prefixTex("entity/souldragon/soul_crystal.png");
    private final LazyValue<RenderType> renderType = new LazyValue<>(this::getRenderType);
    private static final float SIN_45 = (float) Math.sin(Math.PI / 4);
    private final ModelRenderer cube;
    private final ModelRenderer glass;
    private final ModelRenderer base;

    public SoulCrystalRenderer(EntityRendererManager manager) {
        super(manager);
        this.shadowRadius = 0.5F;
        this.glass = new ModelRenderer(64, 32, 0, 0);
        this.glass.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        this.cube = new ModelRenderer(64, 32, 32, 0);
        this.cube.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        this.base = new ModelRenderer(64, 32, 0, 16);
        this.base.addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
    }

    @Override
    public void render(T crystal, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        stack.pushPose();
        float y = getY(crystal, partialTicks);
        float time = ((float) crystal.time + partialTicks) * 3.0F;
        IVertexBuilder builder = buffer.getBuffer(renderType.get());
        stack.pushPose();
        stack.scale(BASE_SCALE , BASE_SCALE, BASE_SCALE);
        stack.translate(0, -0.5, 0);
        int texType = OverlayTexture.NO_OVERLAY;
        if (crystal.showsBottom()) {
            base.render(stack, builder, packedLight, texType);
        }
        stack.mulPose(Vector3f.YP.rotationDegrees(time));
        stack.translate(0, 1.5 + y / 2, 0);
        stack.mulPose(getMultiplier());
        glass.render(stack, builder, packedLight, texType);
        stack.scale(OFFSET_SCALE, OFFSET_SCALE, OFFSET_SCALE);
        stack.mulPose(getMultiplier());
        stack.mulPose(Vector3f.YP.rotationDegrees(time));
        glass.render(stack, builder, packedLight, texType);
        stack.scale(OFFSET_SCALE, OFFSET_SCALE, OFFSET_SCALE);
        stack.mulPose(getMultiplier());
        stack.mulPose(Vector3f.YP.rotationDegrees(time));
        cube.render(stack, builder, packedLight, texType);
        stack.popPose();
        stack.popPose();
        renderBeams(crystal, partialTicks, stack, buffer, packedLight, y);
        super.render(crystal, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    protected RenderType getRenderType() {
        return RenderType.entityCutoutNoCull(SOUL_CRYSTAL);
    }

    protected void renderBeams(T crystal, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float y) {
        BlockPos beamTarget = crystal.getBeamTarget();
        if (beamTarget != null) {
            float bx = (float) beamTarget.getX() + 0.5F;
            float by = (float) beamTarget.getY() + 0.5F;
            float bz = (float) beamTarget.getZ() + 0.5F;
            float tx = (float) (bx - crystal.getX());
            float ty = (float) (by - crystal.getY());
            float tz = (float) (bz - crystal.getZ());
            stack.translate(tx, ty, tz);
            renderBeam(crystal, partialTicks, stack, buffer, packedLight, y, tx, ty, tz);
            stack.translate(-tx, -ty, -tz);
        }
    }

    protected void renderBeam(T crystal, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float y, float tx, float ty, float tz) {
        SoulDragonRenderer.renderSoulCrystalBeams(-tx, -ty + y, -tz, partialTicks, crystal.time, stack, buffer, packedLight);
    }

    private static Quaternion getMultiplier() {
        return new Quaternion(new Vector3f(SIN_45, 0, SIN_45), 60, true);
    }

    public static float getY(SoulCrystalEntity crystal, float partialTicks) {
        float totalTicks = (float) crystal.time + partialTicks;
        float y = MathHelper.sin(totalTicks * 0.2f) / 2 + 0.5f;
        y = (y * y + y) * 0.4F;
        return y - 1.4F;
    }

    @Override
    public ResourceLocation getTextureLocation(SoulCrystalEntity crystal) {
        return SOUL_CRYSTAL;
    }

    @Override
    public boolean shouldRender(T crystal, ClippingHelper helper, double x, double y, double z) {
        return super.shouldRender(crystal, helper, x, y, z) || crystal.getBeamTarget() != null;
    }
}
