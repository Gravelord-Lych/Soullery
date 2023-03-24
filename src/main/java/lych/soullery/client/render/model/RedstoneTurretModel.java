package lych.soullery.client.render.model;

import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class RedstoneTurretModel<T extends LivingEntity> extends SegmentedModel<T> implements IHasHead {
    private final ModelRenderer fenceBottom;
    private final ModelRenderer fenceMid;
    private final ModelRenderer dispenser;

    public RedstoneTurretModel() {
        this.texHeight = 64;
        this.texWidth = 64;
        this.fenceBottom = new ModelRenderer(this, 0, 0);
        this.fenceBottom.setPos(0, 16, 0);
        this.fenceBottom.addBox(-2, -8, -2, 4, 16, 4, 0, 0, 0);
        this.fenceMid = new ModelRenderer(this, 0, 0);
        this.fenceMid.setPos(0, 0, 0);
        this.fenceMid.addBox(-2, -8, -2, 4, 16, 4, 0, 0, 0);
        this.dispenser = new ModelRenderer(this, 0, 32);
        this.dispenser.setPos(0, -16, 0);
        this.dispenser.addBox(-8, -8, -8, 16, 16, 16, 0, 0, 0);
    }

    @Override
    public void setupAnim(T turret, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public Iterable<ModelRenderer> parts() {
        return Arrays.asList(fenceBottom, fenceMid, dispenser);
    }

    @Override
    public ModelRenderer getHead() {
        return dispenser;
    }
}

