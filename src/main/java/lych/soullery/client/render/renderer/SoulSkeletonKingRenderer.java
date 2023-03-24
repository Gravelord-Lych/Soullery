package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.client.render.model.SkeletonKingModel;
import lych.soullery.entity.monster.boss.SoulSkeletonKingEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulSkeletonKingRenderer extends BipedRenderer<SoulSkeletonKingEntity, SkeletonKingModel<SoulSkeletonKingEntity>> {
    private static final ResourceLocation PURIFIED_SOUL_SKELETON_KING = Soullery.prefixTex("entity/purified_soul_skeleton_king.png");
    private static final ResourceLocation SOUL_SKELETON_KING = Soullery.prefixTex("entity/soul_skeleton_king.png");

    public SoulSkeletonKingRenderer(EntityRendererManager manager) {
        super(manager, new SkeletonKingModel<>(), 0.5f);
        addLayer(new BipedArmorLayer<>(this, new SkeletonKingModel<>(0.5f, true), new SkeletonKingModel<>(1, true)));
    }

    @Override
    public ResourceLocation getTextureLocation(SoulSkeletonKingEntity skeleton) {
        return skeleton.isPurified() ? PURIFIED_SOUL_SKELETON_KING : SOUL_SKELETON_KING;
    }
}
