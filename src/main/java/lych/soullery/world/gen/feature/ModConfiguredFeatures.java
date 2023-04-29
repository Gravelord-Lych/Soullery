package lych.soullery.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lych.soullery.Soullery;
import lych.soullery.block.ModBlocks;
import lych.soullery.extension.fire.Fire;
import lych.soullery.extension.fire.Fires;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.UnaryOperator;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModConfiguredFeatures {
    public static final HugeFungusConfig CRIMSON_PLAINS_CRIMSON_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfig(ModBlocks.CRIMSON_HYPHAL_SOIL.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false);
    public static final SLSpikeConfig HIGH_SOUL_SAND_SPIKE_CONFIG = new SLSpikeConfig(Blocks.SOUL_SAND.defaultBlockState(), ImmutableList.of(Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SOIL.defaultBlockState(), ModBlocks.SOUL_STONE.defaultBlockState()), 0.01);
    public static final SLSpikeConfig HIGH_SOUL_SOIL_SPIKE_CONFIG = new SLSpikeConfig(Blocks.SOUL_SOIL.defaultBlockState(), ImmutableList.of(Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SOIL.defaultBlockState(), ModBlocks.SOUL_STONE.defaultBlockState()), 0.01);
    public static final SLSpikeConfig PARCHED_SOIL_SPIKE_CONFIG = new SLSpikeConfig(ModBlocks.PARCHED_SOIL.defaultBlockState(), ImmutableList.of(ModBlocks.PARCHED_SOIL.defaultBlockState(), ModBlocks.SOUL_STONE.defaultBlockState()), 0);
    public static final SLSpikeConfig SMOOTH_SOUL_STONE_SPIKE_CONFIG = new SLSpikeConfig(ModBlocks.SMOOTH_SOUL_STONE.defaultBlockState(), ImmutableList.of(Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SOIL.defaultBlockState(), ModBlocks.SOUL_STONE.defaultBlockState()), 0.01);
    public static final SLSpikeConfig SOUL_SAND_SPIKE_CONFIG = new SLSpikeConfig(Blocks.SOUL_SAND.defaultBlockState(), ImmutableList.of(Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SOIL.defaultBlockState(), ModBlocks.SOUL_STONE.defaultBlockState()), 0);
    public static final SLSpikeConfig SOUL_SOIL_SPIKE_CONFIG = new SLSpikeConfig(Blocks.SOUL_SOIL.defaultBlockState(), ImmutableList.of(Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SOIL.defaultBlockState(), ModBlocks.SOUL_STONE.defaultBlockState()), 0);
    public static final SLSpikeConfig SOUL_STONE_SPIKE_CONFIG = new SLSpikeConfig(ModBlocks.SOUL_STONE.defaultBlockState(), ImmutableList.of(Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SOIL.defaultBlockState(), ModBlocks.SOUL_STONE.defaultBlockState()), 0.025);
    public static final BlockClusterFeatureConfig SOUL_WART_CONFIG = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.SOUL_WART.defaultBlockState()), new SimpleBlockPlacer()).tries(48).xspread(12).zspread(12).noProjection().build();
    public static final BlockClusterFeatureConfig SOULIFIED_BUSH_CONFIG = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.SOULIFIED_BUSH.defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(4).build();
    public static final HugeFungusConfig WARPED_PLAINS_WARPED_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfig(ModBlocks.WARPED_HYPHAL_SOIL.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false);

    public static final ConfiguredFeature<?, ?> CENTRAL_SOUL_CRYSTAL = ModFeatures.CENTRAL_CRYSTAL.configured(NoFeatureConfig.INSTANCE);
    public static final ConfiguredFeature<?, ?> HIGH_SOUL_SAND_SPIKE = ModFeatures.SL_SPIKE.configured(HIGH_SOUL_SAND_SPIKE_CONFIG).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(1);
    public static final ConfiguredFeature<?, ?> HIGH_SOUL_SOIL_SPIKE = ModFeatures.SL_SPIKE.configured(HIGH_SOUL_SOIL_SPIKE_CONFIG).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(1);
    public static final ConfiguredFeature<?, ?> PARCHED_SOIL_SPIKE = ModFeatures.SL_SPIKE.configured(PARCHED_SOIL_SPIKE_CONFIG).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(1).chance(30);
    public static final ConfiguredFeature<?, ?> PATCH_INFERNO = firePatch(Fires.INFERNO, ModBlocks.PARCHED_SOIL);
    public static final ConfiguredFeature<?, ?> PATCH_POISONOUS_FIRE = firePatch(Fires.POISONOUS_FIRE, ModBlocks.WARPED_HYPHAL_SOIL);
    public static final ConfiguredFeature<?, ?> PATCH_PURE_SOUL_FIRE = firePatch(Fires.PURE_SOUL_FIRE, ModBlocks.REFINED_SOUL_SAND, ModBlocks.REFINED_SOUL_SOIL);
    public static final ConfiguredFeature<?, ?> PATCH_SOUL_WART = Feature.RANDOM_PATCH.configured(SOUL_WART_CONFIG).range(128);
    public static final ConfiguredFeature<?, ?> PATCH_SOULIFIED_BUSH = Feature.RANDOM_PATCH.configured(SOULIFIED_BUSH_CONFIG).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(8);
    public static final ConfiguredFeature<?, ?> SL_CRIMSON_FUNGI = Feature.HUGE_FUNGUS.configured(CRIMSON_PLAINS_CRIMSON_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(4)));
    public static final ConfiguredFeature<?, ?> SL_CRIMSON_FUNGI_AT_THE_EDGE = Feature.HUGE_FUNGUS.configured(CRIMSON_PLAINS_CRIMSON_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2)));
    public static final ConfiguredFeature<?, ?> SL_PATCH_SOUL_FIRE = firePatch(Fires.SOUL_FIRE, b -> b.xspread(9).yspread(4).zspread(9).tries(96), Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
    public static final ConfiguredFeature<?, ?> SL_TWISTING_VINE = ModFeatures.SL_TWISTING_VINE.configured(NoFeatureConfig.INSTANCE);
    public static final ConfiguredFeature<?, ?> SL_WARPED_FUNGI = Feature.HUGE_FUNGUS.configured(WARPED_PLAINS_WARPED_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(4)));
    public static final ConfiguredFeature<?, ?> SL_WARPED_FUNGI_AT_THE_EDGE = Feature.HUGE_FUNGUS.configured(WARPED_PLAINS_WARPED_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2)));
    public static final ConfiguredFeature<?, ?> SL_WEEPING_VINE = ModFeatures.SL_WEEPING_WINE.configured(NoFeatureConfig.INSTANCE);
    public static final ConfiguredFeature<?, ?> SMOOTH_SOUL_STONE_SPIKE = ModFeatures.SL_SPIKE.configured(SMOOTH_SOUL_STONE_SPIKE_CONFIG).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(1);
    public static final ConfiguredFeature<?, ?> SOUL_SAND_SPIKE = ModFeatures.SL_SPIKE.configured(SOUL_SAND_SPIKE_CONFIG).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(1);
    public static final ConfiguredFeature<?, ?> SOUL_SOIL_SPIKE = ModFeatures.SL_SPIKE.configured(SOUL_SOIL_SPIKE_CONFIG).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(1);
    public static final ConfiguredFeature<?, ?> SOUL_STONE_SPIKE = ModFeatures.SL_SPIKE.configured(SOUL_STONE_SPIKE_CONFIG).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(1);
    public static final ConfiguredFeature<?, ?> SOUL_PLAINS_SPIKE = Feature.RANDOM_BOOLEAN_SELECTOR.configured(new TwoFeatureChoiceConfig(() -> SOUL_SAND_SPIKE, () -> SOUL_SOIL_SPIKE)).chance(50);
    public static final ConfiguredFeature<?, ?> SPIKED_SOUL_PLAINS_SPIKE = Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(HIGH_SOUL_SAND_SPIKE.weighted(0.44f), SOUL_STONE_SPIKE.weighted(0.05f), SMOOTH_SOUL_STONE_SPIKE.weighted(0.01f)), HIGH_SOUL_SOIL_SPIKE)).decorated(Features.Placements.HEIGHTMAP_SQUARE).count(FeatureSpread.of(2, 3));

    private ModConfiguredFeatures() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        register("central_soul_crystal", CENTRAL_SOUL_CRYSTAL);
        register("high_soul_sand_spike", HIGH_SOUL_SAND_SPIKE);
        register("high_soul_soil_spike", HIGH_SOUL_SOIL_SPIKE);
        register("parched_soil_spike", PARCHED_SOIL_SPIKE);
        register("patch_inferno", PATCH_INFERNO);
        register("patch_poisonous_fire", PATCH_POISONOUS_FIRE);
        register("patch_pure_soul_fire", PATCH_PURE_SOUL_FIRE);
        register("patch_soul_wart", PATCH_SOUL_WART);
        register("patch_soulified_bush", PATCH_SOULIFIED_BUSH);
        register("sl_crimson_fungi", SL_CRIMSON_FUNGI);
        register("sl_crimson_fungi_at_the_edge", SL_CRIMSON_FUNGI_AT_THE_EDGE);
        register("sl_patch_soul_fire", SL_PATCH_SOUL_FIRE);
        register("sl_twisting_vine", SL_TWISTING_VINE);
        register("sl_warped_fungi", SL_WARPED_FUNGI);
        register("sl_warped_fungi_at_the_edge", SL_WARPED_FUNGI_AT_THE_EDGE);
        register("sl_weeping_vine", SL_WEEPING_VINE);
        register("smooth_soul_stone_spike", SMOOTH_SOUL_STONE_SPIKE);
        register("soul_plains_spike", SOUL_PLAINS_SPIKE);
        register("soul_sand_spike", SOUL_SAND_SPIKE);
        register("soul_soil_spike", SOUL_SOIL_SPIKE);
        register("soul_stone_spike", SOUL_STONE_SPIKE);
        register("spiked_soul_plains_spike", SPIKED_SOUL_PLAINS_SPIKE);
    }

    private static ConfiguredFeature<?, ?> firePatch(Fire fire, Block... whiteList) {
        return firePatch(fire, UnaryOperator.identity(), whiteList);
    }

    private static ConfiguredFeature<?, ?> firePatch(Fire fire, UnaryOperator<BlockClusterFeatureConfig.Builder> operator, Block... whiteList) {
        return firePatch(fire.getBlock().defaultBlockState(), operator, whiteList);
    }

    private static ConfiguredFeature<?, ?> firePatch(BlockState fireBlock, UnaryOperator<BlockClusterFeatureConfig.Builder> operator, Block... whiteList) {
        BlockClusterFeatureConfig.Builder builder = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(fireBlock), new SimpleBlockPlacer());
        return Feature.RANDOM_PATCH.configured(operator.apply(builder).whitelist(ImmutableSet.copyOf(whiteList)).noProjection().build()).decorated(Features.Placements.FIRE);
    }

    private static <FC extends IFeatureConfig> void register(String name, ConfiguredFeature<FC, ?> feature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, Soullery.prefix(name), feature);
    }
}
