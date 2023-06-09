package lych.soullery.data;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lych.soullery.Soullery;
import lych.soullery.client.particle.ModParticleNames;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticleDataGen implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final Set<TextureArray> textureArrays = new HashSet<>();

    public ParticleDataGen(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DirectoryCache cache) {
        textureArrays.clear();
        registerParticles();
        generateParticles(cache);
    }

    protected void registerParticles() {
        withName(ModParticleNames.DRIPPING_SOUL_LAVA).add(new ResourceLocation("drip_land")).save();
        withName(ModParticleNames.FALLING_SOUL_LAVA).add(new ResourceLocation("drip_fall")).save();
        withName(ModParticleNames.LANDING_SOUL_LAVA).add(new ResourceLocation("drip_land")).save();
        iterateDown(withName(ModParticleNames.PURSUER_RAIL), "generic_", 5);
        iterateDown(withName(ModParticleNames.PURSUER_RAIL_TYPE_2), "generic_", 5);
        withName(ModParticleNames.SOUL_LAVA).add(Soullery.prefix("soul_lava")).save();
        iterateUp(withName(ModParticleNames.SOUL_DRAGON_BREATH), "generic_", 5, 7);
        iterateUp(withName(ModParticleNames.SOUL_DRAGON_BREATH_PURE), "generic_", 5, 7);
    }

    public static void iterateUp(TextureArray array, String name, int max) {
        iterateUp(array, name, 0, max);
    }

    public static void iterateUp(TextureArray array, String name, int from, int to) {
        Preconditions.checkArgument(from <= to);
        for (int i = from; i <= to; i++){
            array.add(new ResourceLocation(name + i));
        }
        array.save();
    }

    public static void iterateDown(TextureArray array, String name, int max) {
        iterateDown(array, name, max, 0);
    }

    public static void iterateDown(TextureArray array, String name, int from, int to) {
        Preconditions.checkArgument(from >= to);
        for (int i = from; i >= to; i--){
            array.add(new ResourceLocation(name + i));
        }
        array.save();
    }

    protected void generateParticles(DirectoryCache cache) {
        for (TextureArray array : textureArrays) {
            Path target = getPath(array);
            try {
                IDataProvider.save(GSON, cache, array.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public TextureArray withName(String particleName) {
        return new TextureArray(particleName);
    }

    @Override
    public String getName() {
        return String.format("Particles: %s", getModid());
    }

    protected String getModid() {
        return Soullery.MOD_ID;
    }

    private Path getPath(TextureArray array) {
        return generator.getOutputFolder().resolve("assets/" + getModid() + "/" + "particles" + "/" + array.particleName + ".json");
    }

    public class TextureArray {
        private final List<ResourceLocation> textures = new ArrayList<>();
        private final String particleName;

        private TextureArray(String particleName) {
            this.particleName = particleName;
        }

        public TextureArray add(ResourceLocation texture) {
            textures.add(texture);
            return this;
        }

        public void save() {
            Preconditions.checkState(textureArrays.add(this), String.format("Duplicate particle: %s", particleName));
        }

        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonArray array = new JsonArray();
            textures.stream().map(ResourceLocation::toString).forEach(array::add);
            root.add("textures", array);
            return root;
        }
    }
}
