package lych.soullery.world.gen.biome.sll;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lych.soullery.world.gen.biome.ModBiomes;
import net.minecraft.util.Util;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SLHillsLayer implements ICastleTransformer {
    INSTANCE;

    private static final Int2IntMap HILLS_MAP = Util.make(new Int2IntOpenHashMap(), map -> {
        SLLayer.putId(map, ModBiomes.CRIMSON_PLAINS, ModBiomes.CRIMSON_HILLS);
        SLLayer.putId(map, ModBiomes.PARCHED_DESERT, ModBiomes.PARCHED_DESERT_HILLS);
        SLLayer.putId(map, ModBiomes.SOUL_PLAINS, ModBiomes.SOUL_MOUNTAINS);
//      Spiked soul plains cannot be hills.
        SLLayer.putId(map, ModBiomes.SPIKED_SOUL_PLAINS, ModBiomes.SPIKED_SOUL_PLAINS);
        SLLayer.putId(map, ModBiomes.WARPED_PLAINS, ModBiomes.WARPED_HILLS);
    });
    private static final int HILLIFY_PROB_INV = 5;

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (SLLayer.isSame(n, e, s, w, self) && HILLS_MAP.containsKey(n)) {
            return random.nextRandom(HILLIFY_PROB_INV) == 0 ? HILLS_MAP.get(self) : self;
        }
        return self;
    }
}
