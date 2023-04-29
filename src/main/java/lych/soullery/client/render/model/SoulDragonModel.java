package lych.soullery.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * [VanillaCopy]
 * {@link net.minecraft.client.renderer.entity.EnderDragonRenderer.EnderDragonModel EnderDragonModel}
 */
@OnlyIn(Dist.CLIENT)
public class SoulDragonModel extends EntityModel<SoulDragonEntity> {
    private final ModelRenderer head;
    private final ModelRenderer neck;
    private final ModelRenderer jaw;
    private final ModelRenderer body;
    private final ModelRenderer leftWing;
    private final ModelRenderer leftWingTip;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer leftFrontLegTip;
    private final ModelRenderer leftFrontFoot;
    private final ModelRenderer leftRearLeg;
    private final ModelRenderer leftRearLegTip;
    private final ModelRenderer leftRearFoot;
    private final ModelRenderer rightWing;
    private final ModelRenderer rightWingTip;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer rightFrontLegTip;
    private final ModelRenderer rightFrontFoot;
    private final ModelRenderer rightRearLeg;
    private final ModelRenderer rightRearLegTip;
    private final ModelRenderer rightRearFoot;
    @Nullable
    private SoulDragonEntity entity;
    private float partialTicks;

    public SoulDragonModel() {
        texWidth = 256;
        texHeight = 256;
        head = new ModelRenderer(this);
        head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 0.0F, 176, 44);
        head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 0.0F, 112, 30);
        head.mirror = true;
        head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
        head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
        head.mirror = false;
        head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
        head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
        jaw = new ModelRenderer(this);
        jaw.setPos(0.0F, 4.0F, -8.0F);
        jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 0.0F, 176, 65);
        head.addChild(this.jaw);
        neck = new ModelRenderer(this);
        neck.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, 192, 104);
        neck.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 0.0F, 48, 0);
        body = new ModelRenderer(this);
        body.setPos(0.0F, 4.0F, 8.0F);
        body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0.0F, 0, 0);
        body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 0.0F, 220, 53);
        body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 0.0F, 220, 53);
        body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 0.0F, 220, 53);
        leftWing = new ModelRenderer(this);
        leftWing.mirror = true;
        leftWing.setPos(12.0F, 5.0F, 2.0F);
        leftWing.addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
        leftWing.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
        leftWingTip = new ModelRenderer(this);
        leftWingTip.mirror = true;
        leftWingTip.setPos(56.0F, 0.0F, 0.0F);
        leftWingTip.addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
        leftWingTip.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
        leftWing.addChild(this.leftWingTip);
        leftFrontLeg = new ModelRenderer(this);
        leftFrontLeg.setPos(12.0F, 20.0F, 2.0F);
        leftFrontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
        leftFrontLegTip = new ModelRenderer(this);
        leftFrontLegTip.setPos(0.0F, 20.0F, -1.0F);
        leftFrontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
        leftFrontLeg.addChild(this.leftFrontLegTip);
        leftFrontFoot = new ModelRenderer(this);
        leftFrontFoot.setPos(0.0F, 23.0F, 0.0F);
        leftFrontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
        leftFrontLegTip.addChild(this.leftFrontFoot);
        leftRearLeg = new ModelRenderer(this);
        leftRearLeg.setPos(16.0F, 16.0F, 42.0F);
        leftRearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
        leftRearLegTip = new ModelRenderer(this);
        leftRearLegTip.setPos(0.0F, 32.0F, -4.0F);
        leftRearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
        leftRearLeg.addChild(this.leftRearLegTip);
        leftRearFoot = new ModelRenderer(this);
        leftRearFoot.setPos(0.0F, 31.0F, 4.0F);
        leftRearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
        leftRearLegTip.addChild(this.leftRearFoot);
        rightWing = new ModelRenderer(this);
        rightWing.setPos(-12.0F, 5.0F, 2.0F);
        rightWing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
        rightWing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
        rightWingTip = new ModelRenderer(this);
        rightWingTip.setPos(-56.0F, 0.0F, 0.0F);
        rightWingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
        rightWingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
        rightWing.addChild(this.rightWingTip);
        rightFrontLeg = new ModelRenderer(this);
        rightFrontLeg.setPos(-12.0F, 20.0F, 2.0F);
        rightFrontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
        rightFrontLegTip = new ModelRenderer(this);
        rightFrontLegTip.setPos(0.0F, 20.0F, -1.0F);
        rightFrontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
        rightFrontLeg.addChild(this.rightFrontLegTip);
        rightFrontFoot = new ModelRenderer(this);
        rightFrontFoot.setPos(0.0F, 23.0F, 0.0F);
        rightFrontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
        rightFrontLegTip.addChild(this.rightFrontFoot);
        rightRearLeg = new ModelRenderer(this);
        rightRearLeg.setPos(-16.0F, 16.0F, 42.0F);
        rightRearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
        rightRearLegTip = new ModelRenderer(this);
        rightRearLegTip.setPos(0.0F, 32.0F, -4.0F);
        rightRearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
        rightRearLeg.addChild(this.rightRearLegTip);
        rightRearFoot = new ModelRenderer(this);
        rightRearFoot.setPos(0.0F, 31.0F, 4.0F);
        rightRearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
        rightRearLegTip.addChild(this.rightRearFoot);
    }

    @Override
    public void prepareMobModel(SoulDragonEntity dragon, float limbSwing, float limbSwingAmount, float partialTicks) {
        this.entity = dragon;
        this.partialTicks = partialTicks;
    }

    @Override
    public void setupAnim(SoulDragonEntity dragon, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @SuppressWarnings("deprecation")
    @Override
    public void renderToBuffer(MatrixStack stack, IVertexBuilder builder, int packedLight, int texType, float r, float g, float b, float a) {
        stack.pushPose();
        float flapTime = MathHelper.lerp(partialTicks, entity.oFlapTime, entity.flapTime);
        jaw.xRot = (float)(Math.sin(flapTime * ((float)Math.PI * 2F)) + 1.0D) * 0.2F;
        float f1 = (float)(Math.sin(flapTime * ((float)Math.PI * 2F) - 1.0F) + 1.0D);
        f1 = (f1 * f1 + f1 * 2.0F) * 0.05F;
        stack.translate(0.0D, f1 - 2.0F, -3.0D);
        stack.mulPose(Vector3f.XP.rotationDegrees(f1 * 2.0F));
        float f2 = 0.0F;
        float f3 = 20.0F;
        float f4 = -12.0F;
        float f5 = 1.5F;
        double[] adouble = this.entity.getLatencyPos(6, partialTicks);
        float f6 = MathHelper.rotWrap(this.entity.getLatencyPos(5, this.partialTicks)[0] - this.entity.getLatencyPos(10, this.partialTicks)[0]);
        float f7 = MathHelper.rotWrap(this.entity.getLatencyPos(5, this.partialTicks)[0] + (double)(f6 / 2.0F));
        float f8 = flapTime * ((float)Math.PI * 2F);

        for(int i = 0; i < 5; ++i) {
            double[] adouble1 = this.entity.getLatencyPos(5 - i, this.partialTicks);
            float f9 = (float)Math.cos((float)i * 0.45F + f8) * 0.15F;
            this.neck.yRot = MathHelper.rotWrap(adouble1[0] - adouble[0]) * ((float)Math.PI / 180F) * 1.5F;
            this.neck.xRot = f9 + this.entity.getHeadPartYOffset(i, adouble, adouble1) * ((float)Math.PI / 180F) * 1.5F * 5.0F;
            this.neck.zRot = -MathHelper.rotWrap(adouble1[0] - (double)f7) * ((float)Math.PI / 180F) * 1.5F;
            this.neck.y = f3;
            this.neck.z = f4;
            this.neck.x = f2;
            f3 = (float)((double)f3 + Math.sin(this.neck.xRot) * 10.0D);
            f4 = (float)((double)f4 - Math.cos(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0D);
            f2 = (float)((double)f2 - Math.sin(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0D);
            this.neck.render(stack, builder, packedLight, texType);
        }

        this.head.y = f3;
        this.head.z = f4;
        this.head.x = f2;
        double[] adouble2 = this.entity.getLatencyPos(0, this.partialTicks);
        this.head.yRot = MathHelper.rotWrap(adouble2[0] - adouble[0]) * ((float)Math.PI / 180F);
        this.head.xRot = MathHelper.rotWrap(this.entity.getHeadPartYOffset(6, adouble, adouble2)) * ((float)Math.PI / 180F) * 1.5F * 5.0F;
        this.head.zRot = -MathHelper.rotWrap(adouble2[0] - (double)f7) * ((float)Math.PI / 180F);
        this.head.render(stack, builder, packedLight, texType);
        stack.pushPose();
        stack.translate(0.0D, 1.0D, 0.0D);
        stack.mulPose(Vector3f.ZP.rotationDegrees(-f6 * 1.5F));
        stack.translate(0.0D, -1.0D, 0.0D);
        this.body.zRot = 0.0F;
        this.body.render(stack, builder, packedLight, texType);
        float f10 = flapTime * ((float)Math.PI * 2F);
        this.leftWing.xRot = 0.125F - (float)Math.cos(f10) * 0.2F;
        this.leftWing.yRot = -0.25F;
        this.leftWing.zRot = -((float)(Math.sin(f10) + 0.125D)) * 0.8F;
        this.leftWingTip.zRot = (float)(Math.sin(f10 + 2.0F) + 0.5D) * 0.75F;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.zRot = -this.leftWing.zRot;
        this.rightWingTip.zRot = -this.leftWingTip.zRot;
        this.renderSide(stack, builder, packedLight, texType, f1, this.leftWing, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftRearLeg, this.leftRearLegTip, this.leftRearFoot);
        this.renderSide(stack, builder, packedLight, texType, f1, this.rightWing, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightRearLeg, this.rightRearLegTip, this.rightRearFoot);
        stack.popPose();
        float f11 = -((float)Math.sin(flapTime * ((float)Math.PI * 2F))) * 0.0F;
        f8 = flapTime * ((float)Math.PI * 2F);
        f3 = 10.0F;
        f4 = 60.0F;
        f2 = 0.0F;
        adouble = this.entity.getLatencyPos(11, this.partialTicks);

        for(int j = 0; j < 12; ++j) {
            adouble2 = this.entity.getLatencyPos(12 + j, this.partialTicks);
            f11 = (float)((double)f11 + Math.sin((float)j * 0.45F + f8) * (double)0.05F);
            this.neck.yRot = (MathHelper.rotWrap(adouble2[0] - adouble[0]) * 1.5F + 180.0F) * ((float)Math.PI / 180F);
            this.neck.xRot = f11 + (float)(adouble2[1] - adouble[1]) * ((float)Math.PI / 180F) * 1.5F * 5.0F;
            this.neck.zRot = MathHelper.rotWrap(adouble2[0] - (double)f7) * ((float)Math.PI / 180F) * 1.5F;
            this.neck.y = f3;
            this.neck.z = f4;
            this.neck.x = f2;
            f3 = (float)((double)f3 + Math.sin(this.neck.xRot) * 10.0D);
            f4 = (float)((double)f4 - Math.cos(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0D);
            f2 = (float)((double)f2 - Math.sin(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0D);
            this.neck.render(stack, builder, packedLight, texType);
        }

        stack.popPose();
    }

    private void renderSide(MatrixStack stack, IVertexBuilder builder, int packedLight, int texType, float mod, ModelRenderer wing, ModelRenderer leg, ModelRenderer legTip, ModelRenderer foot, ModelRenderer rearLeg, ModelRenderer rearLegTip, ModelRenderer rearFoot) {
        rearLeg.xRot = 1.0F + mod * 0.1F;
        rearLegTip.xRot = 0.5F + mod * 0.1F;
        rearFoot.xRot = 0.75F + mod * 0.1F;
        leg.xRot = 1.3F + mod * 0.1F;
        legTip.xRot = -0.5F - mod * 0.1F;
        foot.xRot = 0.75F + mod * 0.1F;
        wing.render(stack, builder, packedLight, texType);
        leg.render(stack, builder, packedLight, texType);
        rearLeg.render(stack, builder, packedLight, texType);
    }
}
