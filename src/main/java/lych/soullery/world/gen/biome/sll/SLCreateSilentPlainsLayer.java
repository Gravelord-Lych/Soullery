package lych.soullery.world.gen.biome.sll;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public enum SLCreateSilentPlainsLayer implements IBishopTransformer {
    INSTANCE;

    private static final int CREATE_PROB_INV = 10;

    @Override
    public int apply(INoiseRandom random, int sw, int se, int nw, int ne, int self) {
        if (random.nextRandom(CREATE_PROB_INV) == 0 && SLLayer.allOcean(sw, se, nw, ne, self)) {
            return SLLayer.SILENT_PLAINS;
        }
        return self;
    }
}
