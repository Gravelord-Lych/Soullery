package lych.soullery.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public final class BipedModels {
    private BipedModels() {}

    public static class Size64<T extends LivingEntity> extends BipedModel<T> {
        public Size64() {
            super(0, 0, 64, 64);
        }

        public Size64(float offs) {
            super(RenderType::entityCutoutNoCull, offs, 0,64, 64);
        }

        public Size64(Function<? super ResourceLocation, ? extends RenderType> function) {
            super(function::apply, 0, 0, 64, 64);
        }

        public Size64(Function<? super ResourceLocation, ? extends RenderType> function, float boxOffs, float posOffs, int width, int height) {
            super(function::apply, boxOffs, posOffs, width, height);
        }
    }
}
