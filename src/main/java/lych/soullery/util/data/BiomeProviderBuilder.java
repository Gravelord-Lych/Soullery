package lych.soullery.util.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lych.soullery.world.gen.biome.provider.SoulLandBiomeProvider;
import lych.soullery.world.gen.biome.provider.SoulWastelandBiomeProvider;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class BiomeProviderBuilder implements IDataBuilder {
    private final String modid;
    private final String name;

    protected BiomeProviderBuilder(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        addDetails(root);
        root.addProperty("type", getType().toString());
        return root;
    }

    protected abstract void addDetails(JsonObject root);

    public abstract ResourceLocation getType();

    @Override
    public String getNamespace() {
        return modid;
    }

    @Override
    public String getPath() {
        return name;
    }

    public static Fixed fixed(ResourceLocation biome, String modid, String name) {
        return new Fixed(biome, modid, name);
    }

    public static Overworld overworld(boolean largeBiomes, String modid, String name, long seed) {
        return new Overworld(largeBiomes, modid, name, seed);
    }

    public static End end(String modid, String name, long seed) {
        return new End(modid, name, seed);
    }

    public static Checkerboard checkerboard(String modid, String name) {
        return new Checkerboard(modid, name);
    }

    public static Checkerboard checkerboard(String modid, String name, int scale, ResourceLocation... biomes) {
        Checkerboard checkerboard = new Checkerboard(modid, name).scale(scale);
        Arrays.stream(biomes).forEach(checkerboard::addBiome);
        return checkerboard;
    }

    public static MultiNoiseBiomeProviderBuilder multiNoise(String modid, String name) {
        return new MultiNoiseBiomeProviderBuilder(modid, name);
    }

    public static SoulLand soulLand(String modid, String name) {
        return new SoulLand(modid, name, null);
    }

    public static SoulLand soulLand(String modid, String name, long seed) {
        return new SoulLand(modid, name, seed);
    }

    public static SoulWasteland soulWasteland(String modid, String name) {
        return new SoulWasteland(modid, name, null);
    }

    public static SoulWasteland soulWasteland(String modid, String name, long seed) {
        return new SoulWasteland(modid, name, seed);
    }

    public static class Fixed extends BiomeProviderBuilder {
        private static final ResourceLocation FIXED = new ResourceLocation("fixed");
        private final ResourceLocation biome;

        private Fixed(ResourceLocation biome, String modid, String name) {
            super(modid, name);
            this.biome = biome;
        }

        @Override
        protected void addDetails(JsonObject root) {
            Objects.requireNonNull(biome);
            root.addProperty("biome", biome.toString());
        }

        @Override
        public ResourceLocation getType() {
            return FIXED;
        }
    }

    public static class Overworld extends BiomeProviderBuilder {
        private static final ResourceLocation OVERWORLD = new ResourceLocation("vanilla_layered");
        private final boolean largeBiomes;
        private final long seed;

        private Overworld(boolean largeBiomes, String modid, String name, long seed) {
            super(modid, name);
            this.largeBiomes = largeBiomes;
            this.seed = seed;
        }

        @Override
        protected void addDetails(JsonObject root) {
            root.addProperty("large_biomes", largeBiomes);
            root.addProperty("seed", seed);
        }

        @Override
        public ResourceLocation getType() {
            return OVERWORLD;
        }
    }

    public static class End extends BiomeProviderBuilder {
        private static final ResourceLocation END = new ResourceLocation("the_end");
        private final long seed;

        private End(String modid, String name, long seed) {
            super(modid, name);
            this.seed = seed;
        }

        @Override
        protected void addDetails(JsonObject root) {
            root.addProperty("seed", seed);
        }

        @Override
        public ResourceLocation getType() {
            return END;
        }
    }

    public static class Checkerboard extends BiomeProviderBuilder {
        private static final ResourceLocation CHECKERBOARD = new ResourceLocation("checkerboard");
        private final List<ResourceLocation> biomes = new ArrayList<>();
        private int scale = 2;

        private Checkerboard(String modid, String name) {
            super(modid, name);
        }

        public Checkerboard addBiome(ResourceLocation biome) {
            biomes.add(biome);
            return this;
        }

        public Checkerboard scale(int scale) {
            Preconditions.checkArgument(scale > 0, "Scale must be positive");
            this.scale = scale;
            return this;
        }

        @Override
        protected void addDetails(JsonObject root) {
            Objects.requireNonNull(biomes);
            root.addProperty("scale", scale);
            JsonArray biomeArray = new JsonArray();
            biomes.stream().filter(Objects::nonNull).map(ResourceLocation::toString).forEach(biomeArray::add);
            root.add("biomes", biomeArray);
        }

        @Override
        public ResourceLocation getType() {
            return CHECKERBOARD;
        }
    }

    public static class SoulLand extends BiomeProviderBuilder {
        private final long seed;
        private final boolean noSeed;

        private SoulLand(String modid, String name, @Nullable Long seed) {
            super(modid, name);
            this.noSeed = seed == null;
            if (noSeed) {
                this.seed = 0;
            } else {
                this.seed = seed;
            }
        }

        @Override
        protected void addDetails(JsonObject root) {
            if (!noSeed) {
                root.addProperty("seed", seed);
            }
        }

        @Override
        public ResourceLocation getType() {
            return SoulLandBiomeProvider.SOUL_LAND;
        }
    }

    public static class SoulWasteland extends SoulLand {
        private SoulWasteland(String modid, String name, @Nullable Long seed) {
            super(modid, name, seed);
        }

        @Override
        public ResourceLocation getType() {
            return SoulWastelandBiomeProvider.SOUL_WASTELAND;
        }
    }
}
