package lych.soullery.world.gen.biome.sll;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum SLConstructLayer implements IAreaTransformer0 {
    INSTANCE;

    @Override
    public int applyPixel(INoiseRandom random, int x, int y) {
        if (x == 0 && y == 0) {
            return SLLayer.LAND;
        }
        if (random.nextRandom(8) == 0) {
            return SLLayer.LAND;
        }
        return SLLayer.OCEAN;
    }
}
