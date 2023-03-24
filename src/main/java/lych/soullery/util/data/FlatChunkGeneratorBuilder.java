package lych.soullery.util.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lych.soullery.util.ArrayUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FlatChunkGeneratorBuilder extends ChunkGeneratorBuilder {
    private static final ResourceLocation FLAT = new ResourceLocation("flat");
    private final List<Layer> layers = new ArrayList<>();
    private boolean lakes;
    private boolean features;
    private ResourceLocation biome;
    private Stronghold stronghold;
    private List<Structure> structures;

    FlatChunkGeneratorBuilder(String modid, String name) {
        super(modid, name);
    }

    public FlatChunkGeneratorBuilder addLayer(Layer layer) {
        this.layers.add(layer);
        return this;
    }

    public FlatChunkGeneratorBuilder withLakes() {
        this.lakes = true;
        return this;
    }

    public FlatChunkGeneratorBuilder withFeatures() {
        this.features = true;
        return this;
    }

    public FlatChunkGeneratorBuilder biome(ResourceLocation biome) {
        this.biome = biome;
        return this;
    }

    public FlatChunkGeneratorBuilder stronghold(Stronghold stronghold) {
        this.stronghold = stronghold;
        return this;
    }

    public FlatChunkGeneratorBuilder structures(Structure... structures) {
        return structures(new ArrayList<>(Arrays.asList(structures)));
    }

    public FlatChunkGeneratorBuilder structures(List<Structure> structures) {
        this.structures = structures;
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return FLAT;
    }

    @Override
    protected void addDetails(JsonObject root) {
        ArrayUtils.checkNonNull(layers, biome, stronghold, structures);
        JsonObject settings = new JsonObject();
        JsonArray layerArray = new JsonArray();
        layers.stream().filter(Objects::nonNull).forEach(layer -> layerArray.add(layer.toJson()));
        settings.addProperty("biome", biome.toString());
        settings.addProperty("lakes", lakes);
        settings.addProperty("features", features);
        JsonObject structures = new JsonObject();
        JsonObject stronghold = new JsonObject();
        JsonObject innerStructures = new JsonObject();
        stronghold.addProperty("count", this.stronghold.count);
        stronghold.addProperty("spread", this.stronghold.spread);
        stronghold.addProperty("distance", this.stronghold.distance);
        structures.add("stronghold", stronghold);
        this.structures.stream().filter(Objects::nonNull).forEach(structure -> innerStructures.add(structure.name.toString(), structure.toJson()));
        structures.add("structures", innerStructures);
        root.add("structures", structures);
    }

    public static class Layer {
        private final int height;
        private final ResourceLocation block;

        public Layer(int height, ResourceLocation block) {
            this.height = height;
            this.block = block;
        }

        private JsonObject toJson() {
            JsonObject root = new JsonObject();
            root.addProperty("height", height);
            root.addProperty("block", block.toString());
            return root;
        }
    }

    public static class Stronghold {
        private final int count;
        private final int spread;
        private final int distance;

        public Stronghold(int count, int spread, int distance) {
            this.count = count;
            this.spread = spread;
            this.distance = distance;
        }
    }

    public static class Structure {
        private final ResourceLocation name;
        private final int spacing;
        private final int separation;
        private final int salt;

        public Structure(ResourceLocation name, int spacing, int separation, int salt) {
            this.name = Objects.requireNonNull(name);
            this.spacing = spacing;
            this.separation = separation;
            this.salt = salt;
        }

        private JsonObject toJson() {
            JsonObject root = new JsonObject();
            root.addProperty("spacing", spacing);
            root.addProperty("separation", separation);
            root.addProperty("salt", salt);
            return root;
        }
    }
}
