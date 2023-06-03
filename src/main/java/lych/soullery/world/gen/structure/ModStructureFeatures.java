package lych.soullery.world.gen.structure;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import lych.soullery.Soullery;
import lych.soullery.world.gen.biome.ModBiomeNames;
import lych.soullery.world.gen.config.SoulTowerConfig;
import lych.soullery.world.gen.config.SoulTowerConfigs;
import lych.soullery.world.gen.dimension.ModDimensions;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ModStructureFeatures {
    private static final Table<RegistryKey<World>, Structure<?>, StructureSeparationSettings> ALL_STRUCTURES = HashBasedTable.create();
    public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> FIRE_TEMPLE = register(ModDimensions.SOUL_LAND, ModStructureNames.FIRE_TEMPLE, ModStructures.FIRE_TEMPLE, NoFeatureConfig.INSTANCE, new StructureSeparationSettings(25, 10, 14523310));
    public static final StructureFeature<SoulTowerConfig, ? extends Structure<SoulTowerConfig>> SOUL_TOWER_DEFAULT = register(ModDimensions.SOUL_LAND,
            merge(ModStructureNames.SOUL_TOWER, "default"),
            ModStructures.SOUL_TOWER,
            new SoulTowerConfig(90, 150, SoulTowerConfigs.GLOWSTONE, SoulTowerConfigs.OUTER_SOUL_STONE_BRICKS, SoulTowerConfigs.INNER_SOUL_STONE_BRICKS, SoulTowerConfigs.INNER_SOUL_STONE_BRICK_SLAB, SoulTowerConfigs.LIGHT_BLUE_STAINED_GLASS),
            new StructureSeparationSettings(50, 30, 14523311));
    public static final StructureFeature<SoulTowerConfig, ? extends Structure<SoulTowerConfig>> SOUL_TOWER_PARCHED_DESERT = registerVariant(merge(ModStructureNames.SOUL_TOWER, ModBiomeNames.PARCHED_DESERT),
            ModStructures.SOUL_TOWER,
            new SoulTowerConfig(100, 160, SoulTowerConfigs.GLOWSTONE, SoulTowerConfigs.OUTER_NETHER_BRICKS, SoulTowerConfigs.INNER_NETHER_BRICKS, SoulTowerConfigs.INNER_NETHER_BRICK_SLAB, SoulTowerConfigs.ORANGE_STAINED_GLASS));
    public static final StructureFeature<SoulTowerConfig, ? extends Structure<SoulTowerConfig>> SOUL_TOWER_CRIMSON_PLAINS = registerVariant(merge(ModStructureNames.SOUL_TOWER, ModBiomeNames.CRIMSON_PLAINS),
            ModStructures.SOUL_TOWER,
            new SoulTowerConfig(85, 130, SoulTowerConfigs.CRIMSON_PLANKS, SoulTowerConfigs.OUTER_RED_NETHER_BRICKS, SoulTowerConfigs.INNER_RED_NETHER_BRICKS, SoulTowerConfigs.INNER_RED_NETHER_BRICK_SLAB, SoulTowerConfigs.RED_STAINED_GLASS));
    public static final StructureFeature<SoulTowerConfig, ? extends Structure<SoulTowerConfig>> SOUL_TOWER_WARPED_PLAINS = registerVariant(merge(ModStructureNames.SOUL_TOWER, ModBiomeNames.WARPED_PLAINS),
            ModStructures.SOUL_TOWER,
            new SoulTowerConfig(85, 130, SoulTowerConfigs.WARPED_PLANKS, SoulTowerConfigs.OUTER_NETHER_BRICKS, SoulTowerConfigs.INNER_NETHER_BRICKS, SoulTowerConfigs.INNER_NETHER_BRICK_SLAB, SoulTowerConfigs.GREEN_STAINED_GLASS));

    private ModStructureFeatures() {}

    private static <T extends Structure<C>, C extends IFeatureConfig> StructureFeature<C, ? extends Structure<C>> register(RegistryKey<World> dimension, String name, T structure, C config, StructureSeparationSettings settings) {
        addAdditionalStructureTo(dimension, structure, settings);
        return registerVariant(name, structure, config);
    }

    @NotNull
    private static <T extends Structure<C>, C extends IFeatureConfig> StructureFeature<C, ? extends Structure<C>> registerVariant(String name, T structure, C config) {
        StructureFeature<C, ? extends Structure<C>> configured = structure.configured(config);
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, Soullery.prefix(name), configured);
    }

    public static void addAdditionalStructureTo(RegistryKey<World> dimension, Structure<?> structure, StructureSeparationSettings settings) {
        ALL_STRUCTURES.put(dimension, structure, settings);
    }

    public static Map<Structure<?>, StructureSeparationSettings> getStructureMap(RegistryKey<World> dimension) {
        return ImmutableMap.copyOf(ALL_STRUCTURES.rowMap().getOrDefault(dimension, ImmutableMap.of()));
    }

    private static String merge(String structure, String type) {
        return structure + "_" + type;
    }
}
