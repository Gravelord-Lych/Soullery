package lych.soullery.world.gen.biome.swl;

import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SWBiomes;
import lych.soullery.world.gen.biome.sll.SLLayer;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum SWMakeStrategicPlaceLayer implements IC0Transformer {
    INSTANCE;

    @Override
    public int apply(INoiseRandom random, int self) {
        if (SLLayer.isOcean(self)) {
            return self;
        }
        return random.nextRandom(100) == 0 ? ModBiomes.getId(SWBiomes.STRATEGIC_PLACE) : self;
    }
}
