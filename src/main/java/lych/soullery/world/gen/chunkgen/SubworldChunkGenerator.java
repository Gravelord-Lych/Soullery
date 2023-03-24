package lych.soullery.world.gen.chunkgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.world.SeedHelper;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.DimensionSettings;

import java.util.function.Supplier;

public class SubworldChunkGenerator extends CustomNoiseChunkGenerator {
    public static final Codec<SubworldChunkGenerator> CODEC = RecordCodecBuilder.create(SubworldChunkGenerator::makeCodec);

    public SubworldChunkGenerator(BiomeProvider biomeSource, long seed, Supplier<DimensionSettings> settingsSupplier) {
        super(biomeSource, seed, settingsSupplier);
    }

    @Override
    protected Codec<? extends SubworldChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public SubworldChunkGenerator withSeed(long seed) {
        return new SubworldChunkGenerator(biomeSource.withSeed(seed), seed, getSettingsSupplier());
    }

    private static App<RecordCodecBuilder.Mu<SubworldChunkGenerator>, SubworldChunkGenerator> makeCodec(RecordCodecBuilder.Instance<SubworldChunkGenerator> instance) {
        return instance
                .group(BiomeProvider.CODEC.fieldOf("biome_source").forGetter(SubworldChunkGenerator::getBiomeSource),
                        Codec.LONG.fieldOf("seed").orElseGet(SeedHelper::getSeed).stable().forGetter(SubworldChunkGenerator::getSeed),
                        DimensionSettings.CODEC.fieldOf("settings").forGetter(SubworldChunkGenerator::getSettingsSupplier))
                .apply(instance, instance.stable(SubworldChunkGenerator::new));
    }
}
