package lych.soullery.util.data;

import com.google.gson.JsonObject;
import lych.soullery.Soullery;
import lych.soullery.util.ArrayUtils;
import lych.soullery.world.gen.chunkgen.ModChunkGenerators;
import lych.soullery.world.gen.dimension.ModDimensionNames;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public abstract class ChunkGeneratorBuilder implements IDataBuilder {
    private static final ResourceLocation ESV = Soullery.prefix(ModDimensionNames.ESV);
    private static final ResourceLocation ETHEREAL = Soullery.prefix(ModDimensionNames.ETHEREAL);

    private final String modid;
    private final String name;

    protected ChunkGeneratorBuilder(String modid, String name) {
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

    protected abstract ResourceLocation getType();

    @Override
    public String getNamespace() {
        return modid;
    }

    @Override
    public String getPath() {
        return name;
    }

    public static Noise noise(String modid, String name) {
        return new Noise(modid, name);
    }

    public static Debug debug(String modid, String name) {
        return new Debug(modid, name);
    }

    public static Subworld subworld(String modid, String name) {
        return new Subworld(modid, name);
    }

    public static ESV esv(String modid, String name) {
        return new ESV(modid, name);
    }

    public static Common ethe(String modid, String name) {
        return common(modid, name, ETHEREAL);
    }

    public static Common common(String modid, String name, ResourceLocation type) {
        return new Common(modid, name, type);
    }

    public static FlatChunkGeneratorBuilder flat(String modid, String name) {
        return new FlatChunkGeneratorBuilder(modid, name);
    }

    public static class Noise extends ChunkGeneratorBuilder {
        private static final ResourceLocation NOISE = new ResourceLocation("noise");
        private JsonObject biomeSource;
        private long seed;
        private boolean noSeed;
        private ResourceLocation settings;
        private ResourceLocation type = NOISE;

        private Noise(String modid, String name) {
            super(modid, name);
        }

        public Noise biomeSource(BiomeProviderBuilder biomeSource) {
            this.biomeSource = biomeSource.toJson();
            return this;
        }

        public Noise biomeSource(JsonObject biomeSource) {
            this.biomeSource = biomeSource;
            return this;
        }

        public Noise seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Noise noSeed() {
            this.noSeed = true;
            this.seed = 0;
            this.type = ModChunkGenerators.DYNAMIC_SEED_CHUNKGEN;
            return this;
        }

        public Noise settings(ResourceLocation settings) {
            this.settings = settings;
            return this;
        }

        @Override
        protected void addDetails(JsonObject root) {
            ArrayUtils.checkNonNull(biomeSource, seed, settings);
            root.add("biome_source", biomeSource);
            if (!noSeed) {
                root.addProperty("seed", seed);
            }
            root.addProperty("settings", settings.toString());
        }

        @Override
        public ResourceLocation getType() {
            return type;
        }
    }

    public static class Debug extends ChunkGeneratorBuilder {
        private static final ResourceLocation DEBUG = new ResourceLocation("debug");

        public Debug(String modid, String name) {
            super(modid, name);
        }

        @Override
        protected void addDetails(JsonObject root) {}

        @Override
        public ResourceLocation getType() {
            return DEBUG;
        }
    }

    public static class Subworld extends ChunkGeneratorBuilder {
        private static final ResourceLocation SUBWORLD = Soullery.prefix(ModDimensionNames.SUBWORLD);
        private JsonObject biomeSource;
        private long seed;
        private boolean noSeed;
        private ResourceLocation settings;

        private Subworld(String modid, String name) {
            super(modid, name);
        }

        public Subworld biomeSource(BiomeProviderBuilder biomeSource) {
            this.biomeSource = biomeSource.toJson();
            return this;
        }

        public Subworld biomeSource(JsonObject biomeSource) {
            this.biomeSource = biomeSource;
            return this;
        }

        public Subworld seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Subworld noSeed() {
            this.noSeed = true;
            this.seed = 0;
            return this;
        }

        public Subworld settings(ResourceLocation settings) {
            this.settings = settings;
            return this;
        }

        @Override
        protected void addDetails(JsonObject root) {
            ArrayUtils.checkNonNull(biomeSource, seed, settings);
            root.add("biome_source", biomeSource);
            if (!noSeed) {
                root.addProperty("seed", seed);
            }
            root.addProperty("settings", settings.toString());
        }

        @Override
        public ResourceLocation getType() {
            return SUBWORLD;
        }
    }

    public static class ESV extends ChunkGeneratorBuilder {
        private static final ResourceLocation ESV = Soullery.prefix(ModDimensionNames.ESV);
        private JsonObject biomeSource;

        private ESV(String modid, String name) {
            super(modid, name);
        }

        public ESV biomeSource(BiomeProviderBuilder biomeSource) {
            this.biomeSource = biomeSource.toJson();
            return this;
        }

        @Override
        protected void addDetails(JsonObject root) {
            Objects.requireNonNull(biomeSource);
            root.add("biome_source", biomeSource);
        }

        @Override
        public ResourceLocation getType() {
            return ESV;
        }
    }

    public static class Common extends ChunkGeneratorBuilder {
        private final ResourceLocation type;
        private JsonObject biomeSource;
        private long seed;
        private boolean noSeed;
        private ResourceLocation settings;

        private Common(String modid, String name, ResourceLocation type) {
            super(modid, name);
            this.type = type;
        }

        public Common biomeSource(BiomeProviderBuilder biomeSource) {
            this.biomeSource = biomeSource.toJson();
            return this;
        }

        public Common biomeSource(JsonObject biomeSource) {
            this.biomeSource = biomeSource;
            return this;
        }

        public Common seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Common noSeed() {
            this.noSeed = true;
            this.seed = 0;
            return this;
        }

        public Common settings(ResourceLocation settings) {
            this.settings = settings;
            return this;
        }

        @Override
        protected void addDetails(JsonObject root) {
            ArrayUtils.checkNonNull(biomeSource, seed, settings);
            root.add("biome_source", biomeSource);
            if (!noSeed) {
                root.addProperty("seed", seed);
            }
            root.addProperty("settings", settings.toString());
        }

        @Override
        public ResourceLocation getType() {
            return type;
        }
    }
}
