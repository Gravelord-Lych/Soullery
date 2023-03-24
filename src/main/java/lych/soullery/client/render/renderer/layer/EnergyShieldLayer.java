package lych.soullery.client.render.renderer.layer;

import lych.soullery.Soullery;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.EnergyLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnergyShieldLayer<T extends Entity & IChargeableMob, M extends EntityModel<T>> extends EnergyLayer<T, M> {
    private static final ResourceLocation ENERGY_LAYER = Soullery.prefixTex("entity/meta8/meta8_armor.png");
    private final M model;

    public EnergyShieldLayer(IEntityRenderer<T, M> renderer, M model) {
        super(renderer);
        this.model = model;
    }

    @Override
    protected float xOffset(float tickCount) {
        return MathHelper.cos(tickCount * 0.02f) * 3;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return ENERGY_LAYER;
    }

    @Override
    protected M model() {
        return model;
    }
}
