package lych.soullery.data;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lych.soullery.Soullery;
import lych.soullery.util.data.*;
import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.dimension.ModDimensionNames;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biomes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static lych.soullery.Soullery.MOD_ID;

public class DimensionDataGen implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final Set<DimensionBuilder> dimensionBuilders = new HashSet<>();
    private final Set<DimensionTypeBuilder> dimensionTypeBuilders = new HashSet<>();

    public DimensionDataGen(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DirectoryCache cache) {
        clearAll();
        registerDimensions();
        generateDimensions(cache);
    }

    protected void clearAll() {
        dimensionBuilders.clear();
        dimensionTypeBuilders.clear();
    }

    protected void registerDimensions() {
        makeESV();
        makeEthereal();
        makeSoulLand();
        makeSubworld();
    }

    private void makeSoulLand() {
        final BiomeProviderBuilder soulLandBiomeProvider = BiomeProviderBuilder.soulLand(MOD_ID, ModDimensionNames.SOUL_LAND);
        final ChunkGeneratorBuilder soulLandChunkGenerator = ChunkGeneratorBuilder.noise(MOD_ID, ModDimensionNames.SOUL_LAND)
                .biomeSource(soulLandBiomeProvider)
                .noSeed()
                .settings(Soullery.prefix(ModDimensionNames.SOUL_LAND));

        final DimensionTypeBuilder soulLandDimensionType = new DimensionTypeBuilder(MOD_ID, ModDimensionNames.SOUL_LAND)
                .ambientLight(0.1f)
                .fixedTime(18000)
                .ultraWarm()
                .respawnAnchorWorks()
                .hasRaids()
                .logicalHeight(256)
                .infiniburn(BlockTags.INFINIBURN_NETHER.getName())
                .effectsLocation(Soullery.prefix(ModDimensionNames.SOUL_LAND));

        final DimensionBuilder soulLandDimension = new DimensionBuilder(MOD_ID, ModDimensionNames.SOUL_LAND)
                .type(Soullery.prefix(ModDimensionNames.SOUL_LAND))
                .generator(soulLandChunkGenerator);

        registerDimension(soulLandDimension);
        registerDimensionType(soulLandDimensionType);
    }

    private void makeSubworld() {
        final BiomeProviderBuilder subworldBiomeProvider = BiomeProviderBuilder.overworld(false, MOD_ID, ModDimensionNames.SUBWORLD, 0);
        final ChunkGeneratorBuilder subworldChunkGenerator = ChunkGeneratorBuilder.subworld(MOD_ID, ModDimensionNames.SUBWORLD)
                .biomeSource(subworldBiomeProvider)
                .noSeed()
                .settings(Soullery.prefix(ModDimensionNames.SUBWORLD));

        final DimensionTypeBuilder subworldDimensionType = new DimensionTypeBuilder(MOD_ID, ModDimensionNames.SUBWORLD)
                .ambientLight(0)
                .natural()
                .bedWorks()
                .hasRaids()
                .infiniburn(BlockTags.INFINIBURN_OVERWORLD.getName())
                .effectsLocation(Soullery.prefix(ModDimensionNames.SUBWORLD));

        final DimensionBuilder subworldDimension = new DimensionBuilder(MOD_ID, ModDimensionNames.SUBWORLD)
                .type(Soullery.prefix(ModDimensionNames.SUBWORLD))
                .generator(subworldChunkGenerator);

        registerDimensionType(subworldDimensionType);
        registerDimension(subworldDimension);
    }

    private void makeESV() {
        final BiomeProviderBuilder esvBiomeProvider = BiomeProviderBuilder.fixed(ModBiomes.ESV.location(), MOD_ID, ModDimensionNames.ESV);
        final ChunkGeneratorBuilder esvChunkGenerator = ChunkGeneratorBuilder.esv(MOD_ID, ModDimensionNames.ESV).biomeSource(esvBiomeProvider);

        final DimensionTypeBuilder esvDimensionType = new DimensionTypeBuilder(MOD_ID, ModDimensionNames.ESV)
                .ambientLight(0)
                .fixedTime(6000)
                .logicalHeight(256)
                .infiniburn(BlockTags.INFINIBURN_END.getName())
                .effectsLocation(Soullery.prefix(ModDimensionNames.ESV));

        final DimensionBuilder esvDimension = new DimensionBuilder(MOD_ID, ModDimensionNames.ESV)
                .type(Soullery.prefix(ModDimensionNames.ESV))
                .generator(esvChunkGenerator);

        registerDimensionType(esvDimensionType);
        registerDimension(esvDimension);
    }

    private void makeEthereal() {
        final BiomeProviderBuilder etheBiomeProvider = BiomeProviderBuilder.fixed(Biomes.PLAINS.location(), MOD_ID, ModDimensionNames.ETHEREAL);
        final ChunkGeneratorBuilder etheChunkGenerator = ChunkGeneratorBuilder.ethe(MOD_ID, ModDimensionNames.ETHEREAL)
                .biomeSource(etheBiomeProvider)
                .noSeed()
                .settings(Soullery.prefix(ModDimensionNames.ETHEREAL));

        final DimensionTypeBuilder etheDimensionType = new DimensionTypeBuilder(MOD_ID, ModDimensionNames.ETHEREAL)
                .ambientLight(0)
                .fixedTime(6000)
                .logicalHeight(256)
                .infiniburn(BlockTags.INFINIBURN_OVERWORLD.getName())
                .effectsLocation(Soullery.prefix(ModDimensionNames.ETHEREAL));

        final DimensionBuilder etheDimension = new DimensionBuilder(MOD_ID, ModDimensionNames.ETHEREAL)
                .type(Soullery.prefix(ModDimensionNames.ETHEREAL))
                .generator(etheChunkGenerator);

        registerDimensionType(etheDimensionType);
        registerDimension(etheDimension);
    }

    protected void registerDimensionType(DimensionTypeBuilder builder) {
        Preconditions.checkState(dimensionTypeBuilders.add(builder), String.format("Duplicate dimension type: %s", builder.getLocation()));
    }

    protected void registerDimension(DimensionBuilder builder) {
        Preconditions.checkState(dimensionBuilders.add(builder), String.format("Duplicate dimension: %s", builder.getLocation()));
    }

    protected void generateDimensions(DirectoryCache cache) {
        for (DimensionBuilder builder : dimensionBuilders) {
            Path target = getDimensionPath(builder);
            try {
                IDataProvider.save(GSON, cache, builder.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (DimensionTypeBuilder builder : dimensionTypeBuilders) {
            Path target = getDimensionTypePath(builder);
            try {
                IDataProvider.save(GSON, cache, builder.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getName() {
        return String.format("Dimensions: %s", getModid());
    }

    protected String getModid() {
        return MOD_ID;
    }

    private Path getDimensionPath(IDataBuilder builder) {
        return getPath("dimension", builder);
    }

    private Path getDimensionTypePath(IDataBuilder builder) {
        return getPath("dimension_type", builder);
    }

    private Path getPath(String type, IDataBuilder builder) {
        ResourceLocation location = builder.getLocation();
        return generator.getOutputFolder().resolve("data/" + location.getNamespace() + "/" + type + "/" + location.getPath() + ".json");
    }
}
