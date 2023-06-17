package lych.soullery.world.gen.biome.swl;

import lych.soullery.Soullery;
import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SLBiomes;
import lych.soullery.world.gen.biome.SWBiomes;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;
import java.util.stream.Stream;

public final class SWLayer {
    private static final boolean debug = Soullery.DEBUG_BIOMES;
    private static final List<RegistryKey<Biome>> ALL_BIOMES = new ArrayList<>();
    private static final Marker SWL = MarkerManager.getMarker("SoulWastelandBiomes");
    private static final Map<Integer, Integer> remapColors;

    static {
        remapColors = new HashMap<>();
        remapColors.put(ModBiomes.getId(SWBiomes.SOUL_WASTELANDS), 0x349593);
        remapColors.put(ModBiomes.getId(SWBiomes.SOUL_WASTELANDS_II), 0x2d8381);
        remapColors.put(ModBiomes.getId(SWBiomes.SOUL_WASTELANDS_III), 0x256f6d);
        remapColors.put(ModBiomes.getId(SWBiomes.SOUL_WASTELANDS_IV), 0x216261);
        remapColors.put(ModBiomes.getId(SWBiomes.SOUL_WASTELANDS_V), 0x1a5150);
        remapColors.put(ModBiomes.getId(SWBiomes.STRATEGIC_PLACE), 0x103332);
        remapColors.put(ModBiomes.getId(SLBiomes.SOUL_LAVA_OCEAN), 0x00DDDD);
        initBiomes();
    }

    private SWLayer() {}

    @SuppressWarnings("unchecked")
    private static void initBiomes() {
        for (Field field : SWBiomes.class.getFields()) {
            if (field.getType() == RegistryKey.class && !field.isAnnotationPresent(NonSoulWastelandBiome.class)) {
                try {
                    ALL_BIOMES.add((RegistryKey<Biome>) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ALL_BIOMES.add(SLBiomes.SOUL_LAVA_OCEAN);
    }

    public static Layer getDefaultLayer(long seed, int biomeSize, int riverSize) {
        int maxCache = 25;
        IAreaFactory<LazyArea> factory = makeDefaultLayer(biomeSize, riverSize, seed2 -> new LazyAreaLayerContext(maxCache, seed, seed2));
        return new Layer(factory);
    }

    public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> makeDefaultLayer(int biomeSize, int riverSize, LongFunction<C> seedFunc) {
        IAreaFactory<T> factory;
        factory = SWConstructLayer.INSTANCE.run(seedFunc.apply(3));
        debug(factory, "construct");
        factory = ZoomLayer.FUZZY.run(seedFunc.apply(2222), factory);
        debug(factory, "zoom1");
        factory = SWMakeStrategicPlaceLayer.INSTANCE.run(seedFunc.apply(33), factory);
        debug(factory, "strategic");
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(4), factory);
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(9), factory);
        factory = ZoomLayer.NORMAL.run(seedFunc.apply(16), factory);
        for (int i = 0; i < 3; i++) {
            factory = SWSurroundLayer.PRE_ENLARGER.run(seedFunc.apply(1000 + i), factory);
        }
        debug(factory, "zoom2");
        List<SWSurroundLayer> transformOrder = SWSurroundLayer.TRANSFORM_ORDER;
        for (int i = 0; i < transformOrder.size(); i++) {
            SWSurroundLayer layer = transformOrder.get(i);
            factory = layer.run(seedFunc.apply(5000 + i), factory);
        }
        factory = SmoothLayer.INSTANCE.run(seedFunc.apply(101), factory);
        factory = SWRemoveOceanLayer.INSTANCE.run(seedFunc.apply(9999), factory);
        debug(factory, "surround");
        for (int i = 0; i < 4; i++) {
            factory = ZoomLayer.NORMAL.run(seedFunc.apply(2000 + i), factory);
        }
        factory = SmoothLayer.INSTANCE.run(seedFunc.apply(102), factory);
        debug(factory, "output");
        return factory;
    }

    private static <T extends IArea> void debug(IAreaFactory<T> factory, String name) {
        if (debug) {
            ModBiomes.debug(SWL, remapColors, factory, name);
        }
    }

    public static Stream<RegistryKey<Biome>> getAllBiomes() {
        return ALL_BIOMES.stream();
    }
}
