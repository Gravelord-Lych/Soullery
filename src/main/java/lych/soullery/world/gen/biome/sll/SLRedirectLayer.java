package lych.soullery.world.gen.biome.sll;

import com.google.common.collect.ImmutableList;
import lych.soullery.util.WeightedRandom;
import lych.soullery.world.gen.biome.ModBiomes;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import java.util.List;
import java.util.Objects;

public enum SLRedirectLayer implements IC0Transformer {
    INSTANCE;

    private static final List<WeightedBiome> REDIRECTABLE_BIOMES = ImmutableList.of(withWeight(ModBiomes.SOUL_PLAINS, 100),
            withWeight(ModBiomes.PARCHED_DESERT, 80),
            withWeight(ModBiomes.WARPED_PLAINS, 60),
            withWeight(ModBiomes.CRIMSON_PLAINS, 60),
            withWeight(ModBiomes.SPIKED_SOUL_PLAINS, 5));
    private static final List<WeightedBiome> REDIRECTABLE_OCEANS = ImmutableList.of(withWeight(ModBiomes.SOUL_LAVA_OCEAN, 100));

    public static WeightedBiome withWeight(RegistryKey<Biome> biome, int weight) {
        return new WeightedBiome(biome, weight);
    }

    @Override
    public int apply(INoiseRandom random, int self) {
        if (self == SLLayer.PURE) {
            return SLLayer.getId(ModBiomes.INNERMOST_SOUL_LAND);
        }
        if (self == SLLayer.PURE_PLATEAU) {
            return SLLayer.getId(ModBiomes.INNERMOST_PLATEAU);
        }
        return self == SLLayer.LAND ? getRandom(REDIRECTABLE_BIOMES, random) : getRandom(REDIRECTABLE_OCEANS, random);
    }

    private int getRandom(List<WeightedBiome> biomeKeys, INoiseRandom random) {
        Objects.requireNonNull(biomeKeys);
        if (biomeKeys.isEmpty()) {
            throw new IllegalStateException();
        }
        if (biomeKeys.size() == 1) {
            return SLLayer.getId(biomeKeys.get(0).getBiome());
        }
        return SLLayer.getId(WeightedRandom.getRandomItem(random::nextRandom, biomeKeys).getBiome());
    }

    public static class WeightedBiome implements WeightedRandom.Item {
        private final RegistryKey<Biome> biome;
        private final int weight;

        private WeightedBiome(RegistryKey<Biome> biome, int weight) {
            this.biome = biome;
            this.weight = weight;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        public RegistryKey<Biome> getBiome() {
            return biome;
        }
    }
}
