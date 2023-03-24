package lych.soullery.world.gen.biome.sll;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public enum SLPurifyLayer implements IBishopTransformer {
    INSTANCE;

    @Override
    public int apply(INoiseRandom random, int sw, int se, int nw, int ne, int self) {
        if (self == SLLayer.LAND && !SLLayer.anyOcean(sw, se, nw, ne)) {
            return SLLayer.PURE;
        }
        return self;
    }
}
