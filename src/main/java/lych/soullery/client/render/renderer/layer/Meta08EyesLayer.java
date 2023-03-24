package lych.soullery.client.render.renderer.layer;

import lych.soullery.Soullery;
import lych.soullery.client.render.model.BipedModels;
import lych.soullery.entity.monster.boss.Meta08Entity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Meta08EyesLayer extends DynamicEyesLayer<Meta08Entity, BipedModels.Size64<Meta08Entity>> {
    private static final String EYES = "entity/meta8/meta8";

    public Meta08EyesLayer(IEntityRenderer<Meta08Entity, BipedModels.Size64<Meta08Entity>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType(Meta08Entity meta8) {
        return RenderType.entityCutout(Soullery.prefixTex(String.format("%s_%s_%s.png", EYES, meta8.getPhaseClientSide().name().toLowerCase(), meta8.isAttacking() ? "powered" : "common")));
    }
}
