package lych.soullery.util.data;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public interface IDataBuilder {
    String getNamespace();

    String getPath();

    JsonObject toJson();

    default ResourceLocation getLocation() {
        return new ResourceLocation(getNamespace(), getPath());
    }
}
