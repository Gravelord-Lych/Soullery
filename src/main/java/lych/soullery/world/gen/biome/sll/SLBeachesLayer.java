package lych.soullery.world.gen.biome.sll;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lych.soullery.world.gen.biome.ModBiomes;
import net.minecraft.util.Util;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SLBeachesLayer implements ICastleTransformer {
    INSTANCE;

    private static final Int2IntMap BEACHES_MAP = Util.make(new Int2IntOpenHashMap(), map -> {
        map.defaultReturnValue(SLLayer.getId(ModBiomes.SOUL_SAND_BEACH));
        SLLayer.putId(map, ModBiomes.CRIMSON_PLAINS, ModBiomes.CRIMSON_PLAINS_EDGE);
        SLLayer.putId(map, ModBiomes.CRIMSON_HILLS, ModBiomes.CRIMSON_PLAINS_EDGE);
        SLLayer.putId(map, ModBiomes.INNERMOST_PLATEAU, ModBiomes.INNERMOST_SOUL_LAND);
        SLLayer.putId(map, ModBiomes.WARPED_PLAINS, ModBiomes.WARPED_PLAINS_EDGE);
        SLLayer.putId(map, ModBiomes.WARPED_HILLS, ModBiomes.WARPED_PLAINS_EDGE);
    });

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (!SLLayer.isOcean(self) && SLLayer.anyOcean(n, e, s, w)) {
            return BEACHES_MAP.get(self);
        }
        return self;
    }
}
