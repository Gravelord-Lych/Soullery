package lych.soullery.world.gen.biome;

import lych.soullery.entity.ModEntities;
import lych.soullery.world.gen.carver.ModConfiguredCarvers;
import lych.soullery.world.gen.feature.ModConfiguredFeatures;
import lych.soullery.world.gen.structure.ModStructureFeatures;
import lych.soullery.world.gen.surface.ModConfiguredSurfaceBuilders;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;

public final class ModBiomeMakers {
    public static final int DEFAULT_WATER_COLOR = 0x3f76e4;
    public static final int DEFAULT_WATER_FOG_COLOR = 0x050533;

    private ModBiomeMakers() {}

    public static Biome makeSoulBiome(float depth, float scale, boolean towers) {
        return makeSoulBiome(depth, scale, false, towers);
    }

    public static Biome makeSoulBiome(float depth, float scale, boolean spiked, boolean towers) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        soulMobs(spawnBuilder);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        addDefaultSoulBiomeCarvers(genBuilder, false);
        genBuilder.addFeature(Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.SL_PATCH_SOUL_FIRE);
        defaultSoulBiomeGeneration(genBuilder);

        if (spiked) {
            genBuilder.addFeature(Decoration.SURFACE_STRUCTURES, ModConfiguredFeatures.SPIKED_SOUL_PLAINS_SPIKE);
        } else if (depth < 0.2f) {
            genBuilder.addFeature(Decoration.SURFACE_STRUCTURES, ModConfiguredFeatures.SOUL_PLAINS_SPIKE);
        }
        if (towers) {
            genBuilder.addStructureStart(ModStructureFeatures.SOUL_TOWER_DEFAULT);
        }

        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_LAND);

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(2)
                .downfall(0)
                .specialEffects(defaultSoulBiomeAmbience(0.01f).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeSilentPlains(float depth, float scale) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 4, 1, 1));
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WANDERER, 1, 1, 1));

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        addDefaultSoulBiomeCarvers(genBuilder, false);
        genBuilder.addStructureStart(ModStructureFeatures.SKY_CITY);
        genBuilder.addFeature(Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.SL_PATCH_SOUL_FIRE);
        defaultSoulBiomeOres(genBuilder);

        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.SILENT_PLAINS);

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(1.5f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(DEFAULT_WATER_COLOR)
                        .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                        .fogColor(0x285869)
                        .skyColor(calculateSkyColor(1.5f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeSoulLavaOcean(float depth, float scale) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_LAVA_OCEAN);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(depth)
                .scale(scale)
                .temperature(2.5f)
                .downfall(0)
                .specialEffects(defaultSoulBiomeAmbience(0.005f).build())
                .mobSpawnSettings(MobSpawnInfo.EMPTY)
                .generationSettings(builder.build())
                .build();
    }

    private static BiomeAmbience.Builder defaultSoulBiomeAmbience(float prob) {
        return new BiomeAmbience.Builder()
                .waterColor(DEFAULT_WATER_COLOR)
                .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                .fogColor(0xbffdff)
                .skyColor(0x0849cd)
                .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, prob))
                .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS);
    }

    public static Biome makeESVBiome() {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.NOPE);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(0.1f)
                .scale(0.2f)
                .temperature(0.2f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(DEFAULT_WATER_COLOR)
                        .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                        .fogColor(0xC4D0E3)
                        .skyColor(calculateSkyColor(0.2f))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.02f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(MobSpawnInfo.EMPTY)
                .generationSettings(builder.build())
                .build();
    }

    public static Biome makeSoulWastelands(float depth, float scale, boolean rareCaves, boolean hasSoulLavaLakes, boolean guarded) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.VOIDWALKER, 50, 2, 2));
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.VOID_DEFENDER, 10, 1, 2));
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 1, 1));

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        genBuilder.addFeature(Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_SOUL_FIRE_ON_SOUL_STONE);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_WASTELANDS);
        if (hasSoulLavaLakes) {
            genBuilder.addFeature(Decoration.LAKES, ModConfiguredFeatures.LAKE_SOUL_LAVA);
        }
        genBuilder.addFeature(Decoration.SURFACE_STRUCTURES, guarded ? ModConfiguredFeatures.DENSE_STREETLIGHT : ModConfiguredFeatures.SPARSE_STREETLIGHT);
        genBuilder.addFeature(Decoration.SURFACE_STRUCTURES, guarded ? ModConfiguredFeatures.DENSE_SMALL_WATCHTOWER : ModConfiguredFeatures.SPARSE_SMALL_WATCHTOWER);
        if (guarded) {
            genBuilder.addFeature(Decoration.SURFACE_STRUCTURES, ModConfiguredFeatures.LARGE_WATCHTOWER);
        }
        addDefaultSoulBiomeCarvers(genBuilder, rareCaves);

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(depth)
                .scale(scale)
                .temperature(1)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x3F5EE4)
                        .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                        .fogColor(0x45D1DA)
                        .skyColor(calculateFantasticalBiomeSkyColor(1))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.003f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeParchedDesertBiome(float depth, float scale) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.BLAZE, 100, 1, 1));
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIFIED_PIGLIN, 50, 1, 2));
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 65, 2, 2));
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 1, 1));
        spawnBuilder.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.SOUL_RABBIT, 100, 2, 3));
        spawnBuilder.addMobCharge(EntityType.BLAZE, 0.4, 0.4);
        spawnBuilder.addMobCharge(EntityType.ZOMBIFIED_PIGLIN, 0.8, 0.2);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        genBuilder.addFeature(Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_INFERNO);
        genBuilder.addStructureStart(ModStructureFeatures.FIRE_TEMPLE);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.PARCHED_DESERT);
        genBuilder.addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.PATCH_SOULIFIED_BUSH);
        addDefaultSoulBiomeCarvers(genBuilder, false);
        if (depth < 0.2f) {
            genBuilder.addFeature(Decoration.SURFACE_STRUCTURES, ModConfiguredFeatures.PARCHED_SOIL_SPIKE);
            genBuilder.addStructureStart(ModStructureFeatures.SOUL_TOWER_PARCHED_DESERT);
        }

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(depth)
                .scale(scale)
                .temperature(2.5f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(DEFAULT_WATER_COLOR)
                        .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                        .fogColor(0xCE5E5E)
                        .skyColor(calculateFantasticalBiomeSkyColor(2.5f))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.SMOKE, 0.002f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeCrimsonBiome(float depth, float scale) {
        return makeCrimsonBiome(depth, scale, false);
    }

    public static Biome makeCrimsonBiome(float depth, float scale, boolean edge) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 75, 2, 3))
                .addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOGLIN, 30, 1, 2))
                .addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIFIED_PIGLIN, 30, 1, 2))
                .addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 1, 1));

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.CRIMSON_PLAINS);
        genBuilder.addFeature(Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE);
        DefaultBiomeFeatures.addDefaultMushrooms(genBuilder);
        genBuilder.addFeature(Decoration.VEGETAL_DECORATION, Features.CRIMSON_FOREST_VEGETATION);
        addDefaultSoulBiomeCarvers(genBuilder, true);
        if (!edge) {
            genBuilder.addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_CRIMSON_FUNGI)
                    .addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_WEEPING_VINE);
        } else {
            genBuilder.addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_CRIMSON_FUNGI_AT_THE_EDGE);
            defaultSoulBiomeGeneration(genBuilder);
        }
        if (!edge && depth < 0.2f) {
            genBuilder.addStructureStart(ModStructureFeatures.SOUL_TOWER_CRIMSON_PLAINS);
        }
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(2)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(DEFAULT_WATER_COLOR)
                        .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                        .fogColor(edge ? 0x602020 : 0x980B0B)
                        .skyColor(calculateSkyColor(2))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.CRIMSON_SPORE, 0.05f))
                        .ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
                        .ambientMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2))
                        .ambientAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111))
                        .backgroundMusic(BackgroundMusicTracks.createGameMusic(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST))
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeWarpedBiome(float depth, float scale) {
        return makeWarpedBiome(depth, scale, false);
    }

    public static Biome makeWarpedBiome(float depth, float scale, boolean edge) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 100, 2, 2))
                .addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 5, 1, 1))
                .addMobCharge(EntityType.ENDERMAN, 1, 0.12);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.WARPED_PLAINS);
        genBuilder.addFeature(Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_POISONOUS_FIRE);
        DefaultBiomeFeatures.addDefaultMushrooms(genBuilder);
        genBuilder.addFeature(Decoration.VEGETAL_DECORATION, Features.WARPED_FOREST_VEGETATION);
        addDefaultSoulBiomeCarvers(genBuilder, true);
        if (!edge) {
            genBuilder.addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_WARPED_FUNGI)
                    .addFeature(Decoration.VEGETAL_DECORATION, Features.NETHER_SPROUTS)
                    .addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_TWISTING_VINE);
        } else {
            genBuilder.addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_WARPED_FUNGI_AT_THE_EDGE);
            defaultSoulBiomeGeneration(genBuilder);
        }
        if (!edge && depth < 0.2f) {
            genBuilder.addStructureStart(ModStructureFeatures.SOUL_TOWER_WARPED_PLAINS);
        }
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(2)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(DEFAULT_WATER_COLOR)
                        .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                        .fogColor(edge ? 0x6C148C : 0x8C148C)
                        .skyColor(calculateSkyColor(2))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.02857f))
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .ambientMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2))
                        .ambientAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111))
                        .backgroundMusic(BackgroundMusicTracks.createGameMusic(SoundEvents.MUSIC_BIOME_WARPED_FOREST))
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeInnermostSoulLand(float depth, float scale) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        soulMobs(spawnBuilder);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        addDefaultSoulBiomeCarvers(genBuilder, true);
        genBuilder.addFeature(Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_PURE_SOUL_FIRE);
        defaultSoulBiomeGeneration(genBuilder);

        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.INNERMOST_SOUL_LAND);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(1.8f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(DEFAULT_WATER_COLOR)
                        .waterFogColor(DEFAULT_WATER_FOG_COLOR)
                        .fogColor(0xbfccff)
                        .skyColor(0x0818cd)
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.006f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeSoulBeach(float depth, float scale) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        soulMobs(spawnBuilder);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();

        defaultSoulBiomeGeneration(genBuilder);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_BEACH);
        addDefaultSoulBiomeCarvers(genBuilder, true);

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.BEACH)
                .depth(depth)
                .scale(scale)
                .temperature(1.8f)
                .downfall(0)
                .specialEffects(defaultSoulBiomeAmbience(0.01f).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    private static void defaultSoulBiomeGeneration(BiomeGenerationSettings.Builder genBuilder) {
        defaultSoulBiomeOres(genBuilder);
        defaultSoulBiomeVegetation(genBuilder);
    }

    private static void defaultSoulBiomeOres(BiomeGenerationSettings.Builder genBuilder) {
        genBuilder.addFeature(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.ORE_PROFOUND_STONE);
    }

    private static void defaultSoulBiomeVegetation(BiomeGenerationSettings.Builder genBuilder) {
        genBuilder.addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.PATCH_SOUL_WART);
        genBuilder.addFeature(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.PATCH_SOULIFIED_BUSH);
    }

    private static void soulMobs(MobSpawnInfo.Builder builder) {
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 100, 4, 4));
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.WANDERER, 10, 1, 2));
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 1, 1));
        builder.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.SOUL_RABBIT, 100, 2, 3));
    }

    private static void addDefaultSoulBiomeCarvers(BiomeGenerationSettings.Builder builder, boolean rare) {
        builder.addCarver(GenerationStage.Carving.AIR, rare ? ModConfiguredCarvers.RARE_SOUL_CAVE : ModConfiguredCarvers.SOUL_CAVE);
        if (!rare) {
            builder.addCarver(GenerationStage.Carving.AIR, ModConfiguredCarvers.SOUL_CANYON);
        }
    }

    private static int calculateSkyColor(float temperature) {
        float colorFactor = temperature / 3f;
        colorFactor = MathHelper.clamp(colorFactor, -1, 1);
        return MathHelper.hsvToRgb(0.62222224F - colorFactor * 0.05F, 0.5F + colorFactor * 0.1F, 1);
    }

    private static int calculateFantasticalBiomeSkyColor(float temperature) {
        float colorFactor = (temperature - 0.5f) / 2;
        colorFactor = MathHelper.clamp(colorFactor, -1, 1);
        return MathHelper.hsvToRgb(0.33333333f - colorFactor * 0.3f, Math.min(0.05f + colorFactor * colorFactor, 1), 1);
    }
}
