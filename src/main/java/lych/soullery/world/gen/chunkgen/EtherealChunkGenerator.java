package lych.soullery.world.gen.chunkgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.world.SeedHelper;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.DimensionSettings;

import java.util.function.Supplier;

public class EtherealChunkGenerator extends CustomNoiseChunkGenerator {
    public static final Codec<EtherealChunkGenerator> CODEC = RecordCodecBuilder.create(EtherealChunkGenerator::makeCodec);

    public EtherealChunkGenerator(BiomeProvider biomeSource, long seed, Supplier<DimensionSettings> settingsSupplier) {
        super(biomeSource, seed, settingsSupplier);
    }

    @Override
    protected int getOctaveCount() {
        return 16;
    }

    @Override
    protected Codec<? extends EtherealChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public EtherealChunkGenerator withSeed(long seed) {
        return new EtherealChunkGenerator(biomeSource.withSeed(seed), seed, getSettingsSupplier());
    }

    private static App<RecordCodecBuilder.Mu<EtherealChunkGenerator>, EtherealChunkGenerator> makeCodec(RecordCodecBuilder.Instance<EtherealChunkGenerator> instance) {
        return instance
                .group(BiomeProvider.CODEC.fieldOf("biome_source").forGetter(EtherealChunkGenerator::getBiomeSource),
                        Codec.LONG.fieldOf("seed").orElseGet(SeedHelper::getSeed).stable().forGetter(EtherealChunkGenerator::getSeed),
                        DimensionSettings.CODEC.fieldOf("settings").forGetter(EtherealChunkGenerator::getSettingsSupplier))
                .apply(instance, instance.stable(EtherealChunkGenerator::new));
    }
}
