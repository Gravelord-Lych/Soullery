package lych.soullery.world.gen.biome.swl;

import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SLBiomes;
import lych.soullery.world.gen.biome.SWBiomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum SWConstructLayer implements IAreaTransformer0 {
    INSTANCE;

    @Override
    public int applyPixel(INoiseRandom random, int x, int y) {
        if (x == 0 && y == 0) {
            return ModBiomes.getId(SWBiomes.SOUL_WASTELANDS);
        }
        if (random.nextRandom(30) == 0) {
            return ModBiomes.getId(SLBiomes.SOUL_LAVA_OCEAN);
        }
        return ModBiomes.getId(SWBiomes.SOUL_WASTELANDS);
    }
}
