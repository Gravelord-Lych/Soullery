package lych.soullery.client.render.world.dimension;

import lych.soullery.world.gen.dimension.ModDimensionNames;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static lych.soullery.Soullery.prefix;

@OnlyIn(Dist.CLIENT)
public class ModDimensionRenderers {
    public static void registerDimensionRenderers() {
        register(prefix(ModDimensionNames.SOUL_LAND), new SoulLandRenderInfo());
        register(prefix(ModDimensionNames.SOUL_WASTELAND), new SoulLandRenderInfo());
        register(prefix(ModDimensionNames.ESV), new ESVRenderInfo());
    }

    public static void register(ResourceLocation location, DimensionRenderInfo renderInfo) {
        DimensionRenderInfo.EFFECTS.put(location, renderInfo);
    }
}
