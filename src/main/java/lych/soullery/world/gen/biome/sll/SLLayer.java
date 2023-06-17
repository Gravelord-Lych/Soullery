package lych.soullery.world.gen.biome.sll;

import lych.soullery.Soullery;
import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SLBiomes;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.LongFunction;
import java.util.stream.Stream;

public final class SLLayer {
    private static final boolean debug = Soullery.DEBUG_BIOMES;
    public static final int OCEAN = 0;
    public static final int LAND = 1;
    public static final int PURE = 2;
    public static final int PURE_PLATEAU = 3;

    private static final List<RegistryKey<Biome>> ALL_BIOMES = new ArrayList<>();
    private static final Marker SLL = MarkerManager.getMarker("SoulLandBiomes");
    private static final Map<Integer, Integer> remapColors;

    static {
        remapColors = new HashMap<>();
        remapColors.put(ModBiomes.getId(SLBiomes.CRIMSON_PLAINS), 0x940000);
        remapColors.put(ModBiomes.getId(SLBiomes.CRIMSON_HILLS), 0xC40000);
        remapColors.put(ModBiomes.getId(SLBiomes.CRIMSON_PLAINS_EDGE), 0x4A2525);
        remapColors.put(ModBiomes.getId(SLBiomes.INNERMOST_PLATEAU), 0x061e96);
        remapColors.put(ModBiomes.getId(SLBiomes.INNERMOST_SOUL_LAND), 0x172466);
        remapColors.put(ModBiomes.getId(SLBiomes.SOUL_LAVA_OCEAN), 0x00DDDD);
        remapColors.put(ModBiomes.getId(SLBiomes.UNSTABLE_SOUL_LAVA_OCEAN), 0x006666);
        remapColors.put(ModBiomes.getId(SLBiomes.SOUL_PLAINS), 0x664A17);
        remapColors.put(ModBiomes.getId(SLBiomes.SOUL_MOUNTAINS), 0x886017);
        remapColors.put(ModBiomes.getId(SLBiomes.PARCHED_DESERT), 0x572F2F);
        remapColors.put(ModBiomes.getId(SLBiomes.PARCHED_DESERT_HILLS), 0x834747);
        remapColors.put(ModBiomes.getId(SLBiomes.SOUL_SAND_BEACH), 0xEFA420);
        remapColors.put(ModBiomes.getId(SLBiomes.SPIKED_SOUL_PLAINS), 0xB28E51);
        remapColors.put(ModBiomes.getId(SLBiomes.WARPED_PLAINS), 0x8C0094);
        remapColors.put(ModBiomes.getId(SLBiomes.WARPED_HILLS), 0xCA00D5);
        remapColors.put(ModBiomes.getId(SLBiomes.WARPED_PLAINS_EDGE), 0x831BD2);
        remapColors.put(OCEAN, 0x007FFF);
        remapColors.put(LAND, 0x00FF00);
        remapColors.put(PURE, 0x0000FF);
        remapColors.put(PURE_PLATEAU, 0x7F00FF);
        initBiomes();
    }

    @SuppressWarnings("unchecked")
    private static void initBiomes() {
        for (Field field : SLBiomes.class.getFields()) {
            if (field.getType() == RegistryKey.class && !field.isAnnotationPresent(NonSoulLandBiome.class)) {
                try {
                    ALL_BIOMES.add((RegistryKey<Biome>) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private SLLayer() {}

    public static Layer getDefaultLayer(long seed, int biomeSize, int riverSize) {
        int maxCache = 25;
        IAreaFactory<LazyArea> factory = makeDefaultLayer(biomeSize, riverSize, seed2 -> new LazyAreaLayerContext(maxCache, seed, seed2));
        return new Layer(factory);
    }

    public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> makeDefaultLayer(int biomeSize, int riverSize, LongFunction<C> seedFunc) {
        IAreaFactory<T> factory;
        factory = SLConstructLayer.INSTANCE.run(seedFunc.apply(1));
        debug(factory, "construct");
        factory = ZoomLayer.FUZZY.run(seedFunc.apply(2000), factory);
        debug(factory, "zoom1");
        factory = SLPurifyLayer.INSTANCE.run(seedFunc.apply(3), factory);
        debug(factory, "purify1");
        factory = SLEnlargeIslandLayer.INSTANCE.run(seedFunc.apply(1), factory);
        debug(factory, "enlarge1");
        factory = SLEnlargeIslandLayer.INSTANCE.run(seedFunc.apply(2), factory);
        debug(factory, "enlarge2");
        factory = SLExpandPurificationLayer.INSTANCE.run(seedFunc.apply(6), factory);
        factory = SLExpandPurificationLayer.INSTANCE.run(seedFunc.apply(12), factory);
        debug(factory, "purify2");
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(2001), factory);
        debug(factory, "zoom2");
        factory = SLPlateauLayer.INSTANCE.run(seedFunc.apply(50), factory);
        debug(factory, "plateaus");
        factory = SLRedirectLayer.INSTANCE.run(seedFunc.apply(200), factory);
        factory = SLDestabilizeLayer.INSTANCE.run(seedFunc.apply(3969), factory);
        debug(factory, "redirect");
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(2002), factory);
        factory = SLSoulifyLayer.INSTANCE.run(seedFunc.apply(3000), factory);
        debug(factory, "soulify");
        factory = SLHillsLayer.INSTANCE.run(seedFunc.apply(1000), factory);
        debug(factory, "hills");
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(2003), factory);
        factory = SLEdgesLayer.INSTANCE.run(seedFunc.apply(1000), factory);
        factory = SLEnlargeIslandLayer.INSTANCE.run(seedFunc.apply(70), factory);
        debug(factory, "edges");
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(2004), factory);
        factory = SLBeachesLayer.INSTANCE.run(seedFunc.apply(1000), factory);
        debug(factory, "beaches");
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(2005), factory);
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(2006), factory);
        debug(factory, "rough");
        factory = SmoothLayer.INSTANCE.run(seedFunc.apply(101), factory);
        factory = SmoothLayer.INSTANCE.run(seedFunc.apply(102), factory);
        debug(factory, "output");
        return factory;
    }

    private static <T extends IArea> void debug(IAreaFactory<T> factory, String name) {
        if (debug) {
            ModBiomes.debug(SLL, remapColors, factory, name);
        }
    }

    public static boolean isPure(int id) {
        return id == PURE || id == PURE_PLATEAU || id == ModBiomes.getId(SLBiomes.INNERMOST_SOUL_LAND) || id == ModBiomes.getId(SLBiomes.INNERMOST_PLATEAU);
    }

    public static boolean isSame(int... ids) {
        if (ids.length < 2) {
            return true;
        }
        int first = ids[0];
        for (int i = 1; i < ids.length; i++) {
            if (ids[i] != first) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOcean(int id) {
        return id == OCEAN || id == ModBiomes.getId(SLBiomes.SOUL_LAVA_OCEAN) || id == ModBiomes.getId(SLBiomes.UNSTABLE_SOUL_LAVA_OCEAN);
    }

    public static boolean allOcean(int... ids) {
        return Arrays.stream(ids).allMatch(SLLayer::isOcean);
    }

    public static boolean anyOcean(int... ids) {
        return Arrays.stream(ids).anyMatch(SLLayer::isOcean);
    }

    public static Stream<RegistryKey<Biome>> getAllBiomes() {
        return ALL_BIOMES.stream();
    }
}
