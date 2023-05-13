package lych.soullery.world.gen.biome.sll;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SLBiomes;
import net.minecraft.util.Util;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SLBeachesLayer implements ICastleTransformer {
    INSTANCE;

    private static final Int2IntMap BEACHES_MAP = Util.make(new Int2IntOpenHashMap(), map -> {
        map.defaultReturnValue(ModBiomes.getId(SLBiomes.SOUL_SAND_BEACH));
        ModBiomes.putId(map, SLBiomes.CRIMSON_PLAINS, SLBiomes.CRIMSON_PLAINS_EDGE);
        ModBiomes.putId(map, SLBiomes.CRIMSON_HILLS, SLBiomes.CRIMSON_PLAINS_EDGE);
        ModBiomes.putId(map, SLBiomes.INNERMOST_PLATEAU, SLBiomes.INNERMOST_SOUL_LAND);
        ModBiomes.putId(map, SLBiomes.WARPED_PLAINS, SLBiomes.WARPED_PLAINS_EDGE);
        ModBiomes.putId(map, SLBiomes.WARPED_HILLS, SLBiomes.WARPED_PLAINS_EDGE);
    });

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (!SLLayer.isOcean(self) && SLLayer.anyOcean(n, e, s, w)) {
            return BEACHES_MAP.get(self);
        }
        return self;
    }
}
