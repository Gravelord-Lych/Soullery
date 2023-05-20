package lych.soullery.world.gen.dimension.setting;

import lych.soullery.Soullery;
import lych.soullery.block.ModBlocks;
import lych.soullery.util.data.DimensionSettingsBuilder;
import lych.soullery.util.data.NoiseSettingsBuilder;
import lych.soullery.world.gen.dimension.ModDimensionNames;
import lych.soullery.world.gen.dimension.ModDimensions;
import lych.soullery.world.gen.structure.ModStructureFeatures;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Optional;

import static lych.soullery.Soullery.prefix;
import static net.minecraft.util.registry.Registry.register;
import static net.minecraft.util.registry.WorldGenRegistries.NOISE_GENERATOR_SETTINGS;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModDimensionSettings {
    public static final int NO_BEDROCKS = -10;
    public static final DimensionSettings ESV = makeESV();
    public static final DimensionSettings ETHEREAL = makeTheEthereal();
    public static final DimensionSettings SOUL_LAND = makeSoulLand();
    public static final DimensionSettings SOUL_WASTELAND = makeSoulWasteland();
    public static final DimensionSettings SUBWORLD = makeSubworld();

    private ModDimensionSettings() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        register(NOISE_GENERATOR_SETTINGS, prefix(ModDimensionNames.ESV), ESV);
        register(NOISE_GENERATOR_SETTINGS, prefix(ModDimensionNames.ETHEREAL), ETHEREAL);
        register(NOISE_GENERATOR_SETTINGS, prefix(ModDimensionNames.SOUL_LAND), SOUL_LAND);
        register(NOISE_GENERATOR_SETTINGS, prefix(ModDimensionNames.SOUL_WASTELAND), SOUL_WASTELAND);
        register(NOISE_GENERATOR_SETTINGS, prefix(ModDimensionNames.SUBWORLD), SUBWORLD);
    }

    public static DimensionSettings makeSoulLand() {
        return new DimensionSettingsBuilder()
                .structureSettings(new DimensionStructuresSettings(Optional.empty(), ModStructureFeatures.getStructureMap(ModDimensions.SOUL_LAND)))
                .noiseSettings(new NoiseSettingsBuilder()
                        .height(256)
                        .noiseSamplingSettings(new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D))
                        .topSlideSettings(new SlideSettings(-10, 3, 0))
                        .bottomSlideSettings(new SlideSettings(-30, 0, 0))
                        .noiseSizeHorizontal(1)
                        .noiseSizeVertical(3)
                        .densityFactor(1)
                        .densityOffset(-0.46875D)
                        .useSimplexSurfaceNoise()
                        .randomDensityOffset()
                        .build())
                .defaultBlock(ModBlocks.SOUL_STONE.defaultBlockState())
                .defaultFluid(ModBlocks.SOUL_LAVA_FLUID_BLOCK.defaultBlockState())
                .bedrockFloorPosition(0)
                .bedrockRoofPosition(NO_BEDROCKS)
                .seaLevel(63)
                .build();
    }

    public static DimensionSettings makeSoulWasteland() {
        return new DimensionSettingsBuilder()
                .structureSettings(new DimensionStructuresSettings(Optional.empty(), ModStructureFeatures.getStructureMap(ModDimensions.SOUL_WASTELAND)))
                .noiseSettings(new NoiseSettingsBuilder()
                        .height(256)
                        .noiseSamplingSettings(new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D))
                        .topSlideSettings(new SlideSettings(-10, 3, 0))
                        .bottomSlideSettings(new SlideSettings(-30, 0, 0))
                        .noiseSizeHorizontal(1)
                        .noiseSizeVertical(6)
                        .densityFactor(1)
                        .densityOffset(-0.46875D)
                        .useSimplexSurfaceNoise()
                        .randomDensityOffset()
                        .build())
                .defaultBlock(ModBlocks.SOUL_STONE.defaultBlockState())
                .defaultFluid(ModBlocks.SOUL_LAVA_FLUID_BLOCK.defaultBlockState())
                .bedrockFloorPosition(0)
                .bedrockRoofPosition(NO_BEDROCKS)
                .seaLevel(58)
                .build();
    }

    public static DimensionSettings makeSubworld() {
        return new DimensionSettingsBuilder()
                .structureSettings(new DimensionStructuresSettings(Optional.empty(), ModStructureFeatures.getStructureMap(ModDimensions.SUBWORLD)))
                .noiseSettings(new NoiseSettingsBuilder()
                        .height(256)
                        .noiseSamplingSettings(new ScalingSettings(1, 1, 85, 170))
                        .topSlideSettings(new SlideSettings(-10, 3, 0))
                        .bottomSlideSettings(new SlideSettings(-30, 0, 0))
                        .noiseSizeHorizontal(1)
                        .noiseSizeVertical(2)
                        .densityFactor(1.2)
                        .densityOffset(-0.46875D)
                        .useSimplexSurfaceNoise()
                        .randomDensityOffset()
                        .build())
                .defaultBlock(Blocks.STONE.defaultBlockState())
                .defaultFluid(Blocks.WATER.defaultBlockState())
                .bedrockFloorPosition(0)
                .bedrockRoofPosition(NO_BEDROCKS)
                .build();
    }

    public static DimensionSettings makeESV() {
        return new DimensionSettingsBuilder()
                .structureSettings(new DimensionStructuresSettings(Optional.empty(), ModStructureFeatures.getStructureMap(ModDimensions.ESV)))
                .noiseSettings(new NoiseSettingsBuilder()
                        .height(256)
                        .noiseSamplingSettings(new ScalingSettings(3, 2, 160, 240))
                        .topSlideSettings(new SlideSettings(-4000, 64, -46))
                        .bottomSlideSettings(new SlideSettings(-25, 7, 1))
                        .noiseSizeHorizontal(2)
                        .noiseSizeVertical(2)
                        .densityFactor(2)
                        .densityOffset(-0.3)
                        .useSimplexSurfaceNoise()
                        .randomDensityOffset()
                        .islandNoiseOverride()
                        .build())
                .defaultBlock(ModBlocks.SOUL_STONE.defaultBlockState())
                .defaultFluid(ModBlocks.SOUL_LAVA_FLUID_BLOCK.defaultBlockState())
                .bedrockFloorPosition(NO_BEDROCKS)
                .bedrockRoofPosition(NO_BEDROCKS)
                .build();
    }

    public static DimensionSettings makeTheEthereal() {
        return new DimensionSettingsBuilder()
                .structureSettings(new DimensionStructuresSettings(Optional.empty(), ModStructureFeatures.getStructureMap(ModDimensions.ETHEREAL)))
                .noiseSettings(new NoiseSettingsBuilder()
                        .height(256)
                        .noiseSamplingSettings(new ScalingSettings(1, 1, 85, 170))
                        .topSlideSettings(new SlideSettings(-10, 3, 0))
                        .bottomSlideSettings(new SlideSettings(-30, 0, 0))
                        .noiseSizeHorizontal(1)
                        .noiseSizeVertical(2)
                        .densityFactor(1.2)
                        .densityOffset(-0.46875D)
                        .useSimplexSurfaceNoise()
                        .randomDensityOffset()
                        .build())
                .defaultBlock(Blocks.STONE.defaultBlockState())
                .defaultFluid(Blocks.WATER.defaultBlockState())
                .bedrockFloorPosition(0)
                .bedrockRoofPosition(NO_BEDROCKS)
                .build();
    }
}
