package lych.soullery.client.render.model;

import lych.soullery.client.SoulRenderers;
import lych.soullery.entity.monster.boss.AbstractSkeletonKingEntity;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonKingModel<T extends AbstractSkeletonKingEntity> extends SkeletonModel<T> {
    public SkeletonKingModel() {
        this(0, false);
    }

    public SkeletonKingModel(float posOffset, boolean noModel) {
        super(posOffset, noModel);
    }

    @Override
    public void setupAnim(T skeleton, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(skeleton, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (skeleton.isCastingSpell()) {
            SoulRenderers.rotateArmsToCastSpell(this, ageInTicks);
        }
    }
}
