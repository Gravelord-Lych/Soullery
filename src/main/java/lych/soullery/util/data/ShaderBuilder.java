package lych.soullery.util.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class ShaderBuilder implements IDataBuilder {
    private final String modid;
    private final String name;
    private final Set<String> targets = new LinkedHashSet<>(4);
    private final Set<PassNode> passNodes = new LinkedHashSet<>();

    public ShaderBuilder(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }

    @Override
    public String getNamespace() {
        return modid;
    }

    @Override
    public String getPath() {
        return name;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        JsonArray targetArray = new JsonArray();
        targets.forEach(targetArray::add);
        root.add("targets", targetArray);
        JsonArray passNodeArray = new JsonArray();
        passNodes.forEach(passNode -> passNodeArray.add(passNode.toJson()));
        root.add("passes", passNodeArray);
        return root;
    }

    public ShaderBuilder addTarget(String target) {
        targets.add(target);
        return this;
    }

    public ShaderBuilder addPassNode(PassNode passNode) {
        passNodes.add(passNode);
        return this;
    }

    public static PassNode createPassNode(String name, String inTarget, String outTarget) {
        return new PassNode(name, inTarget, outTarget);
    }

    public static Uniform createUniform(String name, double... values) {
        return new Uniform(name, Objects.requireNonNull(values));
    }

    public static class PassNode {
        private final String name;
        private final String inTarget;
        private final String outTarget;
        @Nullable
        private Set<Uniform> uniforms;

        private PassNode(String name, String inTarget, String outTarget) {
            this.name = name;
            this.inTarget = inTarget;
            this.outTarget = outTarget;
        }

        public PassNode addUniform(Uniform uniform) {
            if (uniforms == null) {
                uniforms = new LinkedHashSet<>();
            }
            uniforms.add(uniform);
            return this;
        }

        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            root.addProperty("name", name);
            root.addProperty("intarget", inTarget);
            root.addProperty("outtarget", outTarget);
            JsonArray uniformArray = new JsonArray();
            if (uniforms != null) {
                uniforms.forEach(uniform -> uniformArray.add(uniform.toJson()));
            }
            root.add("uniforms", uniformArray);
            return root;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PassNode passNode = (PassNode) o;
            return name.equals(passNode.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static class Uniform {
        private final String name;
        private final double[] values;

        private Uniform(String name, double... values) {
            this.name = name;
            this.values = values;
        }

        private JsonObject toJson() {
            JsonObject root = new JsonObject();
            root.addProperty("name", name);
            JsonArray valueArray = new JsonArray();
            Arrays.stream(values).forEach(valueArray::add);
            root.add("values", valueArray);
            return root;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Uniform uniform = (Uniform) o;
            return name.equals(uniform.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
