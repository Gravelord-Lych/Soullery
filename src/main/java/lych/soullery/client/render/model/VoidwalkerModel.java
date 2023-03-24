package lych.soullery.client.render.model;

import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidwalkerModel<T extends AbstractVoidwalkerEntity> extends BipedModels.Size64<T> {
    public VoidwalkerModel(float offs) {
        super(RenderType::entityTranslucent, offs, 0, 64, 64);
    }

    @Override
    public void setupAnim(T voidwalker, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(voidwalker, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (voidwalker.isCastingSpell()) {
            rightArm.z = 0.0F;
            rightArm.x = -5.0F;
            leftArm.z = 0.0F;
            leftArm.x = 5.0F;
            rightArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
            leftArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
            rightArm.zRot = 2.3561945F;
            leftArm.zRot = -2.3561945F;
            rightArm.yRot = 0.0F;
            leftArm.yRot = 0.0F;
        }
    }

    @Override
    public void prepareMobModel(T voidwalker, float limbSwing, float limbSwingAmount, float partialTicks) {
        rightArmPose = ArmPose.EMPTY;
        leftArmPose = ArmPose.EMPTY;
        ItemStack itemInHand = voidwalker.getItemInHand(Hand.MAIN_HAND);
        if (itemInHand.getItem() instanceof BowItem && voidwalker.isAggressive()) {
            if (voidwalker.getMainArm() == HandSide.RIGHT) {
                rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }
        super.prepareMobModel(voidwalker, limbSwing, limbSwingAmount, partialTicks);
    }
}
