package lych.soullery.world.gen.structure;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import lych.soullery.Soullery;
import lych.soullery.world.gen.dimension.ModDimensions;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

import java.util.Map;

public final class ModStructureFeatures {
    private static final Table<RegistryKey<World>, Structure<?>, StructureSeparationSettings> ALL_STRUCTURES = HashBasedTable.create();
    public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> FIRE_TEMPLE = register(ModDimensions.SOUL_LAND, ModStructureNames.FIRE_TEMPLE, ModStructures.FIRE_TEMPLE, NoFeatureConfig.INSTANCE, new StructureSeparationSettings(25, 10, 14523310));

    private ModStructureFeatures() {}

    private static <T extends Structure<C>, C extends IFeatureConfig> StructureFeature<C, ? extends Structure<C>> register(RegistryKey<World> dimension, String name, T structure, C config, StructureSeparationSettings settings) {
        addAdditionalStructureTo(dimension, structure, settings);
        StructureFeature<C, ? extends Structure<C>> configured = structure.configured(config);
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, Soullery.prefix(name), configured);
    }

    public static void addAdditionalStructureTo(RegistryKey<World> dimension, Structure<?> structure, StructureSeparationSettings settings) {
        ALL_STRUCTURES.put(dimension, structure, settings);
    }

    public static Map<Structure<?>, StructureSeparationSettings> getStructureMap(RegistryKey<World> dimension) {
        return ImmutableMap.copyOf(ALL_STRUCTURES.rowMap().getOrDefault(dimension, ImmutableMap.of()));
    }
}
