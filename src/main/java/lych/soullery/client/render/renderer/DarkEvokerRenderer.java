package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.Soullery;
import lych.soullery.entity.monster.raider.DarkEvokerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DarkEvokerRenderer extends IllagerRenderer<DarkEvokerEntity> {
    private static final ResourceLocation DARK_EVOKER = Soullery.prefixTex("entity/raider/illager/dark_evoker.png");

    public DarkEvokerRenderer(EntityRendererManager manager) {
        super(manager, new IllagerModel<>(0, 0, 64, 64), 0.5f);
        addLayer(new HeldItemLayer<DarkEvokerEntity, IllagerModel<DarkEvokerEntity>>(this) {
            @Override
            public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, DarkEvokerEntity evoker, float limbSwing, float limbSwingAmount, float partialRenderTick, float ageInTicks, float netHeadYaw, float headPitch) {
                if (evoker.isCastingSpell()) {
                    super.render(stack, buffer, packedLight, evoker, limbSwing, limbSwingAmount, partialRenderTick, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(DarkEvokerEntity entity) {
        return DARK_EVOKER;
    }
}
