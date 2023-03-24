package lych.soullery.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.entity.monster.raider.EngineerEntity;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.AbstractIllagerEntity.ArmPose;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EngineerModel extends IllagerModel<EngineerEntity> {
    private final ModelRenderer head;
    private final ModelRenderer hat;
    private final ModelRenderer body;
    private final ModelRenderer arms;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;

    public EngineerModel() {
        super(0, 0, 64, 64);
        this.head = (new ModelRenderer(this)).setTexSize(64, 64);
        this.head.setPos(0.0F, 0.0F + 0, 0.0F);
        this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0);
        this.hat = (new ModelRenderer(this, 32, 0)).setTexSize(64, 64);
        this.hat.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, 0 + 0.45F);
        this.head.addChild(this.hat);
        this.hat.visible = false;
        ModelRenderer renderer = (new ModelRenderer(this)).setTexSize(64, 64);
        renderer.setPos(0.0F, 0 - 2.0F, 0.0F);
        renderer.texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0);
        this.head.addChild(renderer);
        this.body = (new ModelRenderer(this)).setTexSize(64, 64);
        this.body.setPos(0.0F, 0.0F + 0, 0.0F);
        this.body.texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0);
        this.body.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0 + 0.5F);
        this.arms = (new ModelRenderer(this)).setTexSize(64, 64);
        this.arms.setPos(0.0F, 0.0F + 0 + 2.0F, 0.0F);
        this.arms.texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0);
        ModelRenderer renderer1 = (new ModelRenderer(this, 44, 22)).setTexSize(64, 64);
        renderer1.mirror = true;
        renderer1.addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0);
        this.arms.addChild(renderer1);
        this.arms.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0);
        this.leftLeg = (new ModelRenderer(this, 0, 22)).setTexSize(64, 64);
        this.leftLeg.setPos(-2.0F, 12.0F + 0, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0);
        this.rightLeg = (new ModelRenderer(this, 0, 22)).setTexSize(64, 64);
        this.rightLeg.mirror = true;
        this.rightLeg.setPos(2.0F, 12.0F + 0, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0);
        this.rightArm = (new ModelRenderer(this, 40, 46)).setTexSize(64, 64);
        this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0);
        this.rightArm.setPos(-5.0F, 2.0F + 0, 0.0F);
        this.leftArm = (new ModelRenderer(this, 40, 46)).setTexSize(64, 64);
        this.leftArm.mirror = true;
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0);
        this.leftArm.setPos(5.0F, 2.0F + 0, 0.0F);
    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(this.head, this.body, this.leftLeg, this.rightLeg, this.arms, this.rightArm, this.leftArm);
    }

    @Override
    public void setupAnim(EngineerEntity engineer, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        head.xRot = headPitch * ((float)Math.PI / 180F);
        arms.y = 3.0F;
        arms.z = -1.0F;
        arms.xRot = -0.75F;
        if (this.riding) {
            this.rightArm.xRot = (-(float)Math.PI / 5F);
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = (-(float)Math.PI / 5F);
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = ((float)Math.PI / 10F);
            this.leftLeg.zRot = 0.07853982F;
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = (-(float)Math.PI / 10F);
            this.rightLeg.zRot = -0.07853982F;
        } else {
            this.rightArm.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.leftLeg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.leftLeg.yRot = 0.0F;
            this.leftLeg.zRot = 0.0F;
            this.rightLeg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            this.rightLeg.yRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
        }

        ArmPose armPose = engineer.getArmPose();
        if (armPose == ArmPose.ATTACKING) {
            if (engineer.getMainHandItem().isEmpty()) {
                ModelHelper.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, ageInTicks);
            } else {
                ModelHelper.swingWeaponDown(this.rightArm, this.leftArm, engineer, this.attackTime, ageInTicks);
            }
        } else if (armPose == ArmPose.SPELLCASTING || engineer.isDelaying()) {
            if (engineer.isLeftHanded()) {
                this.leftArm.z = 0.0F;
                this.leftArm.x = 5.0F;
                this.leftArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
                this.leftArm.zRot = -2.3561945F;
                this.leftArm.yRot = 0.0F;
            } else {
                this.rightArm.z = 0.0F;
                this.rightArm.x = -5.0F;
                this.rightArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
                this.rightArm.zRot = 2.3561945F;
                this.rightArm.yRot = 0.0F;
            }
        } else if (armPose == ArmPose.BOW_AND_ARROW) {
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
            this.leftArm.xRot = -0.9424779F + this.head.xRot;
            this.leftArm.yRot = this.head.yRot - 0.4F;
            this.leftArm.zRot = ((float)Math.PI / 2F);
        } else if (armPose == ArmPose.CROSSBOW_HOLD) {
            ModelHelper.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
        } else if (armPose == ArmPose.CROSSBOW_CHARGE) {
            ModelHelper.animateCrossbowCharge(this.rightArm, this.leftArm, engineer, true);
        } else if (armPose == ArmPose.CELEBRATING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.rightArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
            this.rightArm.zRot = 2.670354F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.leftArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
            this.leftArm.zRot = -2.3561945F;
            this.leftArm.yRot = 0.0F;
        }

        boolean flag = armPose == ArmPose.CROSSED;
        this.arms.visible = flag;
        this.leftArm.visible = !flag;
        this.rightArm.visible = !flag;
    }

    @Override
    public ModelRenderer getHat() {
        return this.hat;
    }

    @Override
    public ModelRenderer getHead() {
        return this.head;
    }

    @Override
    public void translateToHand(HandSide side, MatrixStack stack) {
        getArm(side).translateAndRotate(stack);
    }

    private ModelRenderer getArm(HandSide side) {
        return side == HandSide.LEFT ? leftArm : rightArm;
    }
}
