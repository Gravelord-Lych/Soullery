package lych.soullery.world.gen.chunkgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.world.SeedHelper;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

import java.util.function.Supplier;

public class DynamicSeedNoiseChunkGenerator extends CustomNoiseChunkGenerator {
    public static final Codec<DynamicSeedNoiseChunkGenerator> CODEC = RecordCodecBuilder.create(DynamicSeedNoiseChunkGenerator::makeCodec);

    public DynamicSeedNoiseChunkGenerator(BiomeProvider biomeSource, long seed, Supplier<DimensionSettings> settingsSupplier) {
        super(biomeSource, seed, settingsSupplier);
    }

    private static App<RecordCodecBuilder.Mu<DynamicSeedNoiseChunkGenerator>, DynamicSeedNoiseChunkGenerator> makeCodec(RecordCodecBuilder.Instance<DynamicSeedNoiseChunkGenerator> instance) {
        return instance.group(BiomeProvider.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                Codec.LONG.fieldOf("seed").stable().orElseGet(SeedHelper::getSeed).forGetter(gen -> gen.seed),
                DimensionSettings.CODEC.fieldOf("settings").forGetter(gen -> gen.settings)).apply(instance, instance.stable(DynamicSeedNoiseChunkGenerator::new));
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new DynamicSeedNoiseChunkGenerator(biomeSource.withSeed(seed), seed, settings);
    }
}
