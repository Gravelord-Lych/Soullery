package lych.soullery.client.render.renderer;

import lych.soullery.entity.monster.voidwalker.AbstractVoidLasererEntity;
import lych.soullery.entity.monster.voidwalker.ComputerScientistEntity;
import lych.soullery.entity.monster.voidwalker.VoidwalkerTier;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;

public class ComputerScientistRenderer extends AbstractVoidLasererRenderer<ComputerScientistEntity> {
    private static final String COMPUTER_SCIENTIST_CLOTHES_BASE = "computer_scientist_clothes";
    private static final LazyValue<ResourceLocation[]> CLOTHES = new LazyValue<>(() -> new ResourceLocation[VoidwalkerTier.values().length]);

    public ComputerScientistRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getOuterLayer(ComputerScientistEntity voidwalker) {
        int id = voidwalker.getTier().getId();
        if (CLOTHES.get()[id] == null) {
            CLOTHES.get()[id] = AbstractVoidLasererEntity.prefixTex(COMPUTER_SCIENTIST_CLOTHES_BASE + voidwalker.getTier().suffixTextureName(false, "", "") + ".png");
        }
        return CLOTHES.get()[id];
    }
}
