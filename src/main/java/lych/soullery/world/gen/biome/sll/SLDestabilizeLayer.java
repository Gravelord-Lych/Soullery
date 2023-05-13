package lych.soullery.world.gen.biome.sll;

import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SLBiomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SLDestabilizeLayer implements ICastleTransformer {
    INSTANCE;

    private static final int DESTABILIZE_PROB_INV = 75;

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        return SLLayer.allOcean(n, e, s, w, self) && random.nextRandom(DESTABILIZE_PROB_INV) == 0 ? ModBiomes.getId(SLBiomes.UNSTABLE_SOUL_LAVA_OCEAN) : self;
    }
}
