package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.client.render.model.BipedModels;
import lych.soullery.client.render.model.VoidwalkerModel;
import lych.soullery.client.render.renderer.layer.EnergyShieldLayer;
import lych.soullery.client.render.renderer.layer.VoidwalkerClothesLayer;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.entity.monster.voidwalker.VoidwalkerTier;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.UnaryOperator;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractVoidwalkerRenderer<T extends AbstractVoidwalkerEntity> extends BipedRenderer<T, BipedModels.Size64<T>> {
    private static final UnaryOperator<String> NAME_MAKER = name -> String.format("entity/esv/voidwalker%s.png", name);
    private static final String NORMAL_SUFFIX = "base";
    private static final String ETHEREAL_SUFFIX = "base_ethereal";
    private static final LazyValue<ResourceLocation[]> TEXTURES = new LazyValue<>(() -> new ResourceLocation[VoidwalkerTier.values().length * 2]);

    public AbstractVoidwalkerRenderer(EntityRendererManager manager) {
        super(manager, new VoidwalkerModel<>(0), 0.5f);
        addLayer(new VoidwalkerClothesLayer<>(this));
        addLayer(new EnergyShieldLayer<>(this, new VoidwalkerModel<>(0.5f)));
    }

    @Override
    public ResourceLocation getTextureLocation(T voidwalker) {
        VoidwalkerTier tier = voidwalker.getTier();
        int id = tier.getId() * 2;
        boolean ethereal = voidwalker.isEthereal();
        if (ethereal) {
            id++;
        }
        if (TEXTURES.get()[id] == null) {
            TEXTURES.get()[id] = Soullery.prefixTex(NAME_MAKER.apply(tier.suffixTextureName(ethereal, NORMAL_SUFFIX, ETHEREAL_SUFFIX)));
        }
        return TEXTURES.get()[id];
    }

    public abstract ResourceLocation getOuterLayer(T voidwalker);
}

