package lych.soullery.world.gen.biome.swl;

import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SWBiomes;
import lych.soullery.world.gen.biome.sll.SLLayer;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import java.util.stream.IntStream;

public enum SWRemoveOceanLayer implements ICastleTransformer {
    INSTANCE;

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (SLLayer.isOcean(self) && IntStream.of(n, e, s, w).mapToObj(ModBiomes::byId).anyMatch(SWSurroundLayer.STEPS_SET::contains)) {
            return ModBiomes.getId(SWBiomes.SOUL_WASTELANDS);
        }
        return self;
    }
}
