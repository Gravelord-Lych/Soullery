package lych.soullery.world.gen.biome;

import lych.soullery.Soullery;
import lych.soullery.world.gen.biome.provider.SoulLandBiomeProvider;
import lych.soullery.world.gen.biome.sll.NonSoulLandBiome;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;
import static lych.soullery.world.gen.biome.ModBiomeMakers.*;
import static net.minecraftforge.common.BiomeDictionary.addTypes;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBiomes {
    public static final Type ESV_TYPE = Type.getType("ESV");
    public static final Type SOUL_TYPE = Type.getType("SOUL");
    public static final Type SOUL_LAND_TYPE = Type.getType("SOULLAND");
    public static final Type PURE_TYPE = Type.getType("PURE");
    public static final RegistryKey<Biome> CRIMSON_HILLS = makeKey(ModBiomeNames.CRIMSON_HILLS);
    public static final RegistryKey<Biome> CRIMSON_PLAINS = makeKey(ModBiomeNames.CRIMSON_PLAINS);
    public static final RegistryKey<Biome> CRIMSON_PLAINS_EDGE = makeKey(ModBiomeNames.CRIMSON_PLAINS_EDGE);
    @NonSoulLandBiome
    public static final RegistryKey<Biome> ESV = makeKey(ModBiomeNames.ESV);
    public static final RegistryKey<Biome> INNERMOST_PLATEAU = makeKey(ModBiomeNames.INNERMOST_PLATEAU);
    public static final RegistryKey<Biome> INNERMOST_SOUL_LAND = makeKey(ModBiomeNames.INNERMOST_SOUL_LAND);
    public static final RegistryKey<Biome> PARCHED_DESERT = makeKey(ModBiomeNames.PARCHED_DESERT);
    public static final RegistryKey<Biome> PARCHED_DESERT_HILLS = makeKey(ModBiomeNames.PARCHED_DESERT_HILLS);
    public static final RegistryKey<Biome> SOUL_MOUNTAINS = makeKey(ModBiomeNames.SOUL_MOUNTAINS);
    public static final RegistryKey<Biome> SOUL_PLAINS = makeKey(ModBiomeNames.SOUL_PLAINS);
    public static final RegistryKey<Biome> SOUL_SAND_BEACH = makeKey(ModBiomeNames.SOUL_SAND_BEACH);
    public static final RegistryKey<Biome> SOUL_LAVA_OCEAN = makeKey(ModBiomeNames.SOUL_LAVA_OCEAN);
    public static final RegistryKey<Biome> SPIKED_SOUL_PLAINS = makeKey(ModBiomeNames.SPIKED_SOUL_PLAINS);
    public static final RegistryKey<Biome> UNSTABLE_SOUL_LAVA_OCEAN = makeKey(ModBiomeNames.UNSTABLE_SOUL_LAVA_OCEAN);
    public static final RegistryKey<Biome> WARPED_HILLS = makeKey(ModBiomeNames.WARPED_HILLS);
    public static final RegistryKey<Biome> WARPED_PLAINS = makeKey(ModBiomeNames.WARPED_PLAINS);
    public static final RegistryKey<Biome> WARPED_PLAINS_EDGE = makeKey(ModBiomeNames.WARPED_PLAINS_EDGE);

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
        registry.register(make(makeSoulBiome(1, 0.3f), ModBiomeNames.SOUL_MOUNTAINS));
        registry.register(make(makeSoulBiome(0.15f, 0.05f), ModBiomeNames.SOUL_PLAINS));
        registry.register(make(makeSoulBeach(0, 0.01f), ModBiomeNames.SOUL_SAND_BEACH));
        registry.register(make(makeSoulLavaOcean(-1, 0.075f), ModBiomeNames.SOUL_LAVA_OCEAN));
        registry.register(make(makeSoulBiome(0.15f, 0.05f, true), ModBiomeNames.SPIKED_SOUL_PLAINS));
        registry.register(make(makeSoulLavaOcean(-0.6f, 0.32f), ModBiomeNames.UNSTABLE_SOUL_LAVA_OCEAN));
        registry.register(make(makeWarpedBiome(0.45f, 0.3f), ModBiomeNames.WARPED_HILLS));
        registry.register(make(makeWarpedBiome(0.15f, 0.045f), ModBiomeNames.WARPED_PLAINS));
        registry.register(make(makeWarpedBiome(0.15f, 0.045f, true), ModBiomeNames.WARPED_PLAINS_EDGE));
    }

    @SubscribeEvent
    public static void registerBiomeProviders(FMLCommonSetupEvent event) {
        Registry.register(Registry.BIOME_SOURCE, SoulLandBiomeProvider.SOUL_LAND, SoulLandBiomeProvider.CODEC);
    }

    private static RegistryKey<Biome> makeKey(String name) {
        return RegistryKey.create(Registry.BIOME_REGISTRY, Soullery.prefix(name));
    }

    static {
        addTypes(CRIMSON_HILLS, Type.PLAINS, Type.HOT, Type.DRY, Type.HILLS, SOUL_LAND_TYPE);
        addTypes(CRIMSON_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(CRIMSON_PLAINS_EDGE, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(ESV, Type.COLD, Type.DRY, ESV_TYPE);
        addTypes(INNERMOST_PLATEAU, Type.PLATEAU, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE, PURE_TYPE);
        addTypes(INNERMOST_SOUL_LAND, Type.PLAINS, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE, PURE_TYPE);
        addTypes(PARCHED_DESERT, Type.SANDY, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(PARCHED_DESERT, Type.SANDY, Type.HOT, Type.DRY, Type.HILLS, SOUL_LAND_TYPE);
        addTypes(SOUL_MOUNTAINS, Type.MOUNTAIN, Type.HOT, Type.DRY, Type.MOUNTAIN, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SOUL_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SOUL_LAVA_OCEAN, Type.OCEAN, Type.HOT, Type.DRY, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(SPIKED_SOUL_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(UNSTABLE_SOUL_LAVA_OCEAN, Type.OCEAN, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE, SOUL_LAND_TYPE);
        addTypes(WARPED_HILLS, Type.PLAINS, Type.HOT, Type.DRY, Type.HILLS, SOUL_LAND_TYPE);
        addTypes(WARPED_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
        addTypes(WARPED_PLAINS_EDGE, Type.PLAINS, Type.HOT, Type.DRY, SOUL_LAND_TYPE);
    }
}
