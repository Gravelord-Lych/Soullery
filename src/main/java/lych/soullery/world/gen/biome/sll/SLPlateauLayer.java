package lych.soullery.world.gen.biome.sll;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SLPlateauLayer implements ICastleTransformer {
    INSTANCE;

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        return SLLayer.isSame(n, e, s, w, self) && SLLayer.isPure(self) ? SLLayer.PURE_PLATEAU : self;
    }
}
