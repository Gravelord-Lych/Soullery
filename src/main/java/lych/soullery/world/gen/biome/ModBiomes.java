package lych.soullery.world.gen.biome;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.Soullery;
import lych.soullery.world.gen.biome.provider.SoulLandBiomeProvider;
import lych.soullery.world.gen.biome.provider.SoulWastelandBiomeProvider;
import lych.soullery.world.gen.biome.swl.SWSurroundLayer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.BiPredicate;

import static lych.soullery.Soullery.make;
import static lych.soullery.world.gen.biome.ModBiomeMakers.*;
import static net.minecraftforge.common.BiomeDictionary.addTypes;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBiomes {
    public static final Type ESV_TYPE = Type.getType("ESV");
    public static final Type SOUL_TYPE = Type.getType("SOUL");
    public static final Type SOUL_LAND_TYPE = Type.getType("SOULLAND");
    public static final Type SOUL_WASTELAND_TYPE = Type.getType("SOULWASTELAND");
    public static final Type PURE_TYPE = Type.getType("PURE");
    public static final RegistryKey<Biome> ESV = makeKey(ModBiomeNames.ESV);

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        IForgeRegistry<Biome> registry = event.getRegistry();
        registry.register(make(makeCrimsonBiome(0.45f, 0.3f), ModBiomeNames.CRIMSON_HILLS));
        registry.register(make(makeCrimsonBiome(0.15f, 0.045f), ModBiomeNames.CRIMSON_PLAINS));
        registry.register(make(makeCrimsonBiome(0.15f, 0.045f, true), ModBiomeNames.CRIMSON_PLAINS_EDGE));
        registry.register(make(makeESVBiome(), ModBiomeNames.ESV));
        registry.register(make(makeInnermostSoulLand(0.16f, 0.03f), ModBiomeNames.INNERMOST_SOUL_LAND));
        registry.register(make(makeInnermostSoulLand(1, 0.02f), ModBiomeNames.INNERMOST_PLATEAU));
        registry.register(make(makeParchedDesertBiome(0.16f, 0.06f), ModBiomeNames.PARCHED_DESERT));
        registry.register(make(makeParchedDesertBiome(0.45f, 0.3f), ModBiomeNames.PARCHED_DESERT_HILLS));
        registry.register(make(makeSoulBiome(1, 0.3f, false), ModBiomeNames.SOUL_MOUNTAINS));
        registry.register(make(makeSoulBiome(0.15f, 0.05f, true), ModBiomeNames.SOUL_PLAINS));
        registry.register(make(makeSilentPlains(0.15f, 0.02f), ModBiomeNames.SILENT_PLAINS));
        registry.register(make(makeSilentPlains(0.5f, 0.02f), ModBiomeNames.SILENT_PLATEAU));
        registry.register(make(makeSoulBeach(0, 0.01f), ModBiomeNames.SOUL_SAND_BEACH));
        registry.register(make(makeSoulLavaOcean(-1, 0.075f), ModBiomeNames.SOUL_LAVA_OCEAN));
        registry.register(make(makeSoulWastelands(0.15f, 0.03f, false, true, false), ModBiomeNames.SOUL_WASTELANDS));
        registry.register(make(makeSoulWastelands(1.2f, 0.025f, true, true, false), ModBiomeNames.SOUL_WASTELANDS_II));
        registry.register(make(makeSoulWastelands(2.6f, 0.02f, true, true, true), ModBiomeNames.SOUL_WASTELANDS_III));
        registry.register(make(makeSoulWastelands(4.3f, 0.015f, true, false, true), ModBiomeNames.SOUL_WASTELANDS_IV));
        registry.register(make(makeSoulWastelands(6.3f, 0.01f, true, false, true), ModBiomeNames.SOUL_WASTELANDS_V));
        registry.register(make(makeSoulWastelands(8.5f, 0.005f, true, false, false), ModBiomeNames.STRATEGIC_PLACE));
        registry.register(make(makeSoulBiome(0.15f, 0.05f, true, false), ModBiomeNames.SPIKED_SOUL_PLAINS));
        registry.register(make(makeSoulLavaOcean(-0.6f, 0.32f), ModBiomeNames.UNSTABLE_SOUL_LAVA_OCEAN));
        registry.register(make(makeWarpedBiome(0.45f, 0.3f), ModBiomeNames.WARPED_HILLS));
        registry.register(make(makeWarpedBiome(0.15f, 0.045f), ModBiomeNames.WARPED_PLAINS));
        registry.register(make(makeWarpedBiome(0.15f, 0.045f, true), ModBiomeNames.WARPED_PLAINS_EDGE));
    }

    @SubscribeEvent
    public static void registerBiomeProviders(FMLCommonSetupEvent event) {
        Registry.register(Registry.BIOME_SOURCE, SoulLandBiomeProvider.SOUL_LAND, SoulLandBiomeProvider.CODEC);
        Registry.register(Registry.BIOME_SOURCE, SoulWastelandBiomeProvider.SOUL_WASTELAND, SoulWastelandBiomeProvider.CODEC);
    }

    static {
        addTypes(SLBiomes.CRIMSON_HILLS, Type.PLAINS, Type.HOT, Type.DRY, Type.HILLS, SOUL_LAND_TYPE);
        addTypes(SLBiomes.CRIMSON_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(SLBiomes.CRIMSON_PLAINS_EDGE, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(ESV, Type.COLD, Type.DRY, ESV_TYPE);
        addTypes(SLBiomes.INNERMOST_PLATEAU, Type.PLATEAU, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE, PURE_TYPE);
        addTypes(SLBiomes.INNERMOST_SOUL_LAND, Type.PLAINS, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE, PURE_TYPE);
        addTypes(SLBiomes.PARCHED_DESERT, Type.SANDY, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(SLBiomes.PARCHED_DESERT_HILLS, Type.SANDY, Type.HOT, Type.DRY, Type.HILLS, SOUL_LAND_TYPE);
        addTypes(SLBiomes.SILENT_PLAINS, Type.HOT, Type.DRY, Type.PLAINS, SOUL_LAND_TYPE);
        addTypes(SLBiomes.SILENT_PLATEAU, Type.HOT, Type.DRY, Type.PLATEAU, SOUL_LAND_TYPE);
        addTypes(SLBiomes.SOUL_MOUNTAINS, Type.MOUNTAIN, Type.HOT, Type.DRY, Type.MOUNTAIN, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SLBiomes.SOUL_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SLBiomes.SOUL_LAVA_OCEAN, Type.OCEAN, Type.HOT, Type.DRY, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SLBiomes.SPIKED_SOUL_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SLBiomes.UNSTABLE_SOUL_LAVA_OCEAN, Type.OCEAN, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SLBiomes.WARPED_HILLS, Type.PLAINS, Type.HOT, Type.DRY, Type.HILLS, SOUL_LAND_TYPE);
        addTypes(SLBiomes.WARPED_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(SLBiomes.WARPED_PLAINS_EDGE, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);

        for (RegistryKey<Biome> biome : SWSurroundLayer.STEPS) {
            addTypes(biome, Type.HOT, Type.DRY, Type.WASTELAND, Type.SPARSE, Type.DEAD, SOUL_WASTELAND_TYPE);
        }
    }

    public static RegistryKey<Biome> makeKey(String name) {
        return RegistryKey.create(Registry.BIOME_REGISTRY, Soullery.prefix(name));
    }

    public static <T extends IArea> void debug(Marker marker, Map<Integer, Integer> remapColors, IAreaFactory<T> factory, String name) {
        Path path = Paths.get(String.format("D:\\debug\\%s.png", name));
        if (Files.exists(path)) {
            Soullery.LOGGER.info(marker, "Skipped debug for {} because file exists", name);
            return;
        }
        final int size = 2048;
        final int rad = size / 2;
        final int ox = 0;
        final int oz = 0;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D display = image.createGraphics();
        IArea area = factory.make();
        BiPredicate<Integer, Integer> line = (i, mod) -> {
            for (int j = -5; j < 5; j++) {
                if ((i + j) % mod == 0)
                    return true;
            }
            return false;
        };
        Soullery.LOGGER.info(marker, "Started drawing for {}", name);
        for (int x = -rad; x < rad - 1; x++) {
            for (int z = -rad; z < rad - 1; z++) {
                int xx = x + (ox * 64);
                int zz = z + (oz * 64);
                int c = area.get(x, z);
                display.setColor(line.test(xx, 512) || line.test(zz, 512) ? new Color(0xFF0000) : new Color(remapColors.getOrDefault(c, c)));
                display.drawRect(x + rad, z + rad, 1, 1);
            }
        }
        Soullery.LOGGER.info(marker, "breakpoint {}", name);
        try {
            if (ImageIO.write(image, "png", Files.newOutputStream(path))) {
                Soullery.LOGGER.info(marker, "written");
            } else {
                Soullery.LOGGER.warn(marker, "not written");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static RegistryKey<Biome> byId(int biomeId) {
        return ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getKey(biomeId);
    }

    public static int getId(RegistryKey<Biome> biome) {
        return ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(biome.location());
    }

    public static void putId(Int2IntMap map, RegistryKey<Biome> key, RegistryKey<Biome> value) {
        map.put(getId(key), getId(value));
    }
}
