package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.Soullery;
import lych.soullery.entity.functional.FortifiedSoulCrystalEntity;
import lych.soullery.entity.monster.SoulSkeletonEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EnderCrystalRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;

public class FortifiedSoulCrystalRenderer extends SoulCrystalRenderer<FortifiedSoulCrystalEntity> {
    private static final ResourceLocation FORTIFIED_SOUL_CRYSTAL = Soullery.prefixTex("entity/esv/fortified_soul_crystal.png");

    public FortifiedSoulCrystalRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    protected void renderBeams(FortifiedSoulCrystalEntity crystal, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float y) {
        super.renderBeams(crystal, partialTicks, stack, buffer, packedLight, y);
        SoulSkeletonEntity skeleton = crystal.getAbsorbingSkeleton();
        if (skeleton != null) {
            float tx = (float) (MathHelper.lerp(partialTicks, skeleton.xo, skeleton.getX()) - MathHelper.lerp(partialTicks, crystal.xo, crystal.getX()));
            float ty = (float) (MathHelper.lerp(partialTicks, skeleton.yo, skeleton.getY()) - skeleton.getBbHeight() * 0.5 - MathHelper.lerp(partialTicks, crystal.yo, crystal.getY()));
            float tz = (float) (MathHelper.lerp(partialTicks, skeleton.zo, skeleton.getZ()) - MathHelper.lerp(partialTicks, crystal.zo, crystal.getZ()));
            stack.translate(tx, ty, tz);
            Color color1 = new Color(222, 219, 255);
            Color color2 = new Color(87, 17, 122);
            SoulDragonRenderer.renderSoulCrystalBeams(-tx, -ty + EnderCrystalRenderer.getY(crystal, partialTicks), -tz, partialTicks, crystal.tickCount, color1, color2, stack, buffer, packedLight);
        }
    }

    @Override
    protected void renderBeam(FortifiedSoulCrystalEntity crystal, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float y, float tx, float ty, float tz) {
        Color color1 = new Color(255, 200, 200);
        Color color2 = new Color(103, 10, 10);
        SoulDragonRenderer.renderSoulCrystalBeams(-tx, -ty + y, -tz, partialTicks, crystal.time, color1, color2, stack, buffer, packedLight);
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.entityCutoutNoCull(FORTIFIED_SOUL_CRYSTAL);
    }

    @Override
    public boolean shouldRender(FortifiedSoulCrystalEntity crystal, ClippingHelper helper, double x, double y, double z) {
        return crystal.getAbsorbingSkeleton() != null || super.shouldRender(crystal, helper, x, y, z);
    }
}
