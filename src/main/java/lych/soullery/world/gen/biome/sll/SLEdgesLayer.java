package lych.soullery.world.gen.biome.sll;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SLBiomes;
import net.minecraft.util.Util;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import java.util.stream.IntStream;

public enum SLEdgesLayer implements ICastleTransformer {
    INSTANCE;

    private static final Int2IntMap EDGES_MAP = Util.make(new Int2IntOpenHashMap(), map -> {
        ModBiomes.putId(map, SLBiomes.CRIMSON_PLAINS, SLBiomes.CRIMSON_PLAINS_EDGE);
        ModBiomes.putId(map, SLBiomes.CRIMSON_HILLS, SLBiomes.CRIMSON_PLAINS_EDGE);
        ModBiomes.putId(map, SLBiomes.WARPED_PLAINS, SLBiomes.WARPED_PLAINS_EDGE);
        ModBiomes.putId(map, SLBiomes.WARPED_HILLS, SLBiomes.WARPED_PLAINS_EDGE);
    });
    private static final Int2IntMap SIMILARITY_MAP = Util.make(new Int2IntOpenHashMap(), map -> {
        map.put(ModBiomes.getId(SLBiomes.CRIMSON_HILLS), Flags.CRIMSON);
        map.put(ModBiomes.getId(SLBiomes.CRIMSON_PLAINS), Flags.CRIMSON);
        map.put(ModBiomes.getId(SLBiomes.CRIMSON_PLAINS_EDGE), Flags.CRIMSON);
        map.put(ModBiomes.getId(SLBiomes.WARPED_HILLS), Flags.WARPED);
        map.put(ModBiomes.getId(SLBiomes.WARPED_PLAINS), Flags.WARPED);
        map.put(ModBiomes.getId(SLBiomes.WARPED_PLAINS_EDGE), Flags.WARPED);
    });

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (EDGES_MAP.containsKey(self) && IntStream.of(n, e, s, w).anyMatch(id -> !isSimilar(id, self))) {
            return EDGES_MAP.get(self);
        }
        return self;
    }

    private static boolean isSimilar(int a, int b) {
        return SIMILARITY_MAP.get(a) == SIMILARITY_MAP.get(b);
    }

    public static final class Flags {
        public static final int CRIMSON = 1;
        public static final int WARPED = 2;

        private Flags() {}
    }
}
