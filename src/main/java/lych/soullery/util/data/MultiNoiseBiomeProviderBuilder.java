package lych.soullery.util.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lych.soullery.util.ArrayUtils;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleConsumer;

public class MultiNoiseBiomeProviderBuilder extends BiomeProviderBuilder {
    private static final ResourceLocation MULTI_NOISE = new ResourceLocation("multi_noise");

    private Noise humidityNoise;
    private Noise altitudeNoise;
    private Noise weirdnessNoise;
    private Noise temperatureNoise;
    private long seed;
    private final List<ParameterizedBiome> biomes = new ArrayList<>();

    MultiNoiseBiomeProviderBuilder(String modid, String name) {
        super(modid, name);
    }

    @Override
    protected void addDetails(JsonObject root) {
        ArrayUtils.checkNonNull(humidityNoise, altitudeNoise, weirdnessNoise, temperatureNoise, biomes);
        root.add("humidity_noise", humidityNoise.toJson());
        root.add("altitude_noise", altitudeNoise.toJson());
        root.add("weirdness_noise", weirdnessNoise.toJson());
        root.add("temperature_noise", temperatureNoise.toJson());
        root.addProperty("seed", seed);
        JsonArray biomeArray = new JsonArray();
        biomes.stream().filter(Objects::nonNull).map(ParameterizedBiome::toJson).forEach(biomeArray::add);
        root.add("biomes", biomeArray);
    }

    public MultiNoiseBiomeProviderBuilder humidityNoise(NoiseBuilder humidityNoise) {
        Objects.requireNonNull(humidityNoise);
        this.humidityNoise = humidityNoise.build();
        return this;
    }

    public MultiNoiseBiomeProviderBuilder altitudeNoise(NoiseBuilder altitudeNoise) {
        Objects.requireNonNull(altitudeNoise);
        this.altitudeNoise = altitudeNoise.build();
        return this;
    }

    public MultiNoiseBiomeProviderBuilder weirdnessNoise(NoiseBuilder weirdnessNoise) {
        Objects.requireNonNull(weirdnessNoise);
        this.weirdnessNoise = weirdnessNoise.build();
        return this;
    }

    public MultiNoiseBiomeProviderBuilder temperatureNoise(NoiseBuilder temperatureNoise) {
        Objects.requireNonNull(temperatureNoise);
        this.temperatureNoise = temperatureNoise.build();
        return this;
    }

    public MultiNoiseBiomeProviderBuilder addBiome(ParameterizedBiomeBuilder biome) {
        Objects.requireNonNull(biome);
        this.biomes.add(biome.build());
        return this;
    }

    public MultiNoiseBiomeProviderBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return MULTI_NOISE;
    }

    private static class Noise {
        private final int firstOctave;
        private final DoubleList amplitudes;

        private Noise(int firstOctave, DoubleList amplitudes) {
            this.firstOctave = firstOctave;
            this.amplitudes = amplitudes;
        }

        private JsonObject toJson() {
            Objects.requireNonNull(amplitudes);
            Preconditions.checkState(amplitudes.size() == 2, String.format("Illegal size: %d", amplitudes.size()));
            JsonObject root = new JsonObject();
            root.addProperty("firstOctave", firstOctave);
            JsonArray amplitudeArray = new JsonArray();
            amplitudes.forEach((DoubleConsumer) amplitudeArray::add);
            root.add("amplitudes", amplitudeArray);
            return root;
        }
    }

    public static class NoiseBuilder {
        private int firstOctave = -7;
        private DoubleList amplitudes = new DoubleArrayList(Arrays.asList(1.0, 1.0));

        public NoiseBuilder firstOctave(int firstOctave) {
            this.firstOctave = firstOctave;
            return this;
        }

        public NoiseBuilder amplitudes(double first, double second) {
            return amplitudes(new DoubleArrayList(Arrays.asList(first, second)));
        }

        public NoiseBuilder amplitudes(DoubleList amplitudes) {
            this.amplitudes = amplitudes;
            return this;
        }

        private Noise build() {
            return new Noise(firstOctave, amplitudes);
        }
    }

    private static class ParameterizedBiome {
        private final double altitude;
        private final double weirdness;
        private final double offset;
        private final double temperature;
        private final double humidity;
        private final ResourceLocation biome;

        private ParameterizedBiome(double altitude, double weirdness, double offset, double temperature, double humidity, ResourceLocation biome) {
            this.altitude = altitude;
            this.weirdness = weirdness;
            this.offset = offset;
            this.temperature = temperature;
            this.humidity = humidity;
            this.biome = Objects.requireNonNull(biome);
        }

        private JsonObject parametersToJson() {
            JsonObject root = new JsonObject();
            root.addProperty("altitude", altitude);
            root.addProperty("weirdness", weirdness);
            root.addProperty("offset", offset);
            root.addProperty("temperature", temperature);
            root.addProperty("humidity", humidity);
            return root;
        }

        private JsonObject toJson() {
            JsonObject root = new JsonObject();
            root.add("parameters", parametersToJson());
            root.addProperty("biome", biome.toString());
            return root;
        }
    }

    public static class ParameterizedBiomeBuilder {
        private double altitude;
        private double weirdness;
        private double offset;
        private double temperature;
        private double humidity;
        private ResourceLocation biome;

        public ParameterizedBiomeBuilder altitude(double altitude) {
            this.altitude = altitude;
            return this;
        }

        public ParameterizedBiomeBuilder weirdness(double weirdness) {
            this.weirdness = weirdness;
            return this;
        }

        public ParameterizedBiomeBuilder offset(double offset) {
            this.offset = offset;
            return this;
        }

        public ParameterizedBiomeBuilder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public ParameterizedBiomeBuilder humidity(double humidity) {
            this.humidity = humidity;
            return this;
        }

        public ParameterizedBiomeBuilder biome(ResourceLocation biome) {
            Objects.requireNonNull(biome);
            this.biome = biome;
            return this;
        }

        private ParameterizedBiome build() {
            return new ParameterizedBiome(altitude, weirdness, offset, temperature, humidity, biome);
        }
    }
}
