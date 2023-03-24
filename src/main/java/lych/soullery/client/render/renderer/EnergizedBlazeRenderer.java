package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.entity.monster.boss.EnergizedBlazeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.BlazeModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnergizedBlazeRenderer extends MobRenderer<EnergizedBlazeEntity, BlazeModel<EnergizedBlazeEntity>> {
    private static final ResourceLocation ENERGIZED_BLAZE = Soullery.prefixTex("entity/energized_blaze.png");

    public EnergizedBlazeRenderer(EntityRendererManager manager) {
        super(manager, new BlazeModel<>(), 0.5f);
    }

    @Override
    protected int getBlockLightLevel(EnergizedBlazeEntity blaze, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(EnergizedBlazeEntity blaze) {
        return ENERGIZED_BLAZE;
    }
}