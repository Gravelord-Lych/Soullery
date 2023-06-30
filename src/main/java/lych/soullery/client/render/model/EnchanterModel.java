package lych.soullery.client.render.model;

import lych.soullery.entity.monster.boss.enchanter.EnchanterEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchanterModel extends BipedModels.Size64<EnchanterEntity> {
    protected final ModelRenderer jacket;
    protected final ModelRenderer addition;

    public EnchanterModel() {
        this(0);
    }

    public EnchanterModel(float offs) {
        super(RenderType::entityTranslucent, offs, 0, 64, 96);
        leftLeg = new ModelRenderer(this, 0, 22);
        leftLeg.setPos(2.0F, 12.0F, 0.0F);
        leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        leftLeg.mirror = true;
        body = new ModelRenderer(this, 0, 0);
        body.setPos(0.0F, 0.0F, 0.0F);
        body.texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F);
        hat = new ModelRenderer(this, 0, 0);
        hat.setPos(0.0F, 0.0F, 0.0F);
        hat.texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.5F);
        rightLeg = new ModelRenderer(this, 0, 22);
        rightLeg.setPos(-2.0F, 12.0F, 0.0F);
        rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        head = new ModelRenderer(this, 0, 0);
        head.setPos(0.0F, 0.0F, 0.0F);
        head.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F);
        leftArm = new ModelRenderer(this, 40, 46);
        leftArm.setPos(5.0F, 0.0F, 0.0F);
        leftArm.addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        leftArm.mirror = true;
        jacket = new ModelRenderer(this, 0, 0);
        jacket.setPos(0.0F, 0.0F, 0.0F);
        jacket.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.5F);
        addition = new ModelRenderer(this, 0, 0);
        addition.setPos(0.0F, 0.0F, 0.0F);
        addition.texOffs(0, 62).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.8F);
        rightArm = new ModelRenderer(this, 40, 46);
        rightArm.setPos(-5.0F, 0.0F, 0.0F);
        rightArm.addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        body.addChild(jacket);
        jacket.addChild(addition);
    }

    @Override
    public void setupAnim(EnchanterEntity enchanter, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(enchanter, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        jacket.copyFrom(body);
        addition.copyFrom(jacket);
    }
}
