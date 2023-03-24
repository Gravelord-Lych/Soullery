package lych.soullery.util.data;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class DimensionBuilder implements IDataBuilder {
    private ResourceLocation type;
    private JsonObject chunkGenerator;
    private final String modid;
    private final String name;

    public DimensionBuilder(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }

    public DimensionBuilder type(ResourceLocation type) {
        this.type = type;
        return this;
    }

    public DimensionBuilder generator(ChunkGeneratorBuilder builder) {
        return generator(builder.toJson());
    }

    public DimensionBuilder generator(JsonObject generator) {
        this.chunkGenerator = generator;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimensionBuilder that = (DimensionBuilder) o;
        return Objects.equals(modid, that.modid) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modid, name);
    }

    @Override
    public JsonObject toJson() {
        Objects.requireNonNull(type);
        Objects.requireNonNull(chunkGenerator);
        JsonObject root = new JsonObject();
        root.addProperty("type", type.toString());
        root.add("generator", chunkGenerator);
        return root;
    }

    @Override
    public String getNamespace() {
        return modid;
    }

    @Override
    public String getPath() {
        return name;
    }

    public ResourceLocation getType() {
        return type;
    }
}
