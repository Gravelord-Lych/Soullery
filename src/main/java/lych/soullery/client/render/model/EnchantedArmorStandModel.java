package lych.soullery.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.entity.monster.boss.enchanter.EnchantedArmorStandEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantedArmorStandModel extends BipedModels.Size64<EnchantedArmorStandEntity> {
    public final ModelRenderer block;
    private final ModelRenderer bodyStick1;
    private final ModelRenderer bodyStick2;
    private final ModelRenderer shoulderStick;

    public EnchantedArmorStandModel() {
        this(0);
    }

    public EnchantedArmorStandModel(float offs) {
        super(offs);
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-1, -7, -1, 2, 7, 2, offs);
        head.setPos(0, 0, 0);
        block = new ModelRenderer(this, 0, 32);
        block.addBox(-8, -20, -8, 16, 16, 16, offs);
        block.setPos(0, 0, 0);
        body = new ModelRenderer(this, 0, 26);
        body.addBox(-6, 0, -1.5f, 12, 3, 3, offs);
        body.setPos(0, 0, 0);
        rightArm = new ModelRenderer(this, 24, 0);
        rightArm.addBox(-2, -2, -1, 2, 12, 2, offs);
        rightArm.setPos(-5, 2, 0);
        leftArm = new ModelRenderer(this, 32, 16);
        leftArm.mirror = true;
        leftArm.addBox(0, -2, -1, 2, 12, 2, offs);
        leftArm.setPos(5, 2, 0);
        rightLeg = new ModelRenderer(this, 8, 0);
        rightLeg.addBox(-1, 0, -1, 2, 11, 2, offs);
        rightLeg.setPos(-1.9F, 12, 0);
        leftLeg = new ModelRenderer(this, 40, 16);
        leftLeg.mirror = true;
        leftLeg.addBox(-1, 0, -1, 2, 11, 2, offs);
        leftLeg.setPos(1.9F, 12, 0);
        bodyStick1 = new ModelRenderer(this, 16, 0);
        bodyStick1.addBox(-3, 3, -1, 2, 7, 2, offs);
        bodyStick1.setPos(0, 0, 0);
        bodyStick1.visible = true;
        bodyStick2 = new ModelRenderer(this, 48, 16);
        bodyStick2.addBox(1, 3, -1, 2, 7, 2, offs);
        bodyStick2.setPos(0, 0, 0);
        shoulderStick = new ModelRenderer(this, 32, 0);
        shoulderStick.addBox(-4, 10, -1, 8, 2, 2, offs);
        shoulderStick.setPos(0, 0, 0);
        hat.visible = false;
    }

    @Override
    public void setupAnim(EnchantedArmorStandEntity eas, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(eas, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        leftArm.visible = true;
        rightArm.visible = true;
        leftLeg.setPos(1.9f, 12, 0);
        rightLeg.setPos(-1.9f, 12, 0);
        block.copyFrom(head);
    }

    @Override
    public void renderToBuffer(MatrixStack stack, IVertexBuilder builder, int packedLight, int overlay, float r, float g, float b, float a) {
        stack.pushPose();
        stack.scale(0.6f, 0.6f, 0.6f);
        stack.translate(0, 0.1, 0);
        block.render(stack, builder, packedLight, overlay, r, g, b, a);
        stack.popPose();
        super.renderToBuffer(stack, builder, packedLight, overlay, r, g, b, a);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(bodyStick1, bodyStick2, shoulderStick));
    }

    @Override
    public void translateToHand(HandSide side, MatrixStack stack) {
        ModelRenderer renderer = getArm(side);
        boolean visible = renderer.visible;
        renderer.visible = true;
        super.translateToHand(side, stack);
        renderer.visible = visible;
    }
}
