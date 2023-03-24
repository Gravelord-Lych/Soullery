package lych.soullery.data;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lych.soullery.Soullery;
import lych.soullery.client.shader.ModShaderNames;
import lych.soullery.util.ModConstants;
import lych.soullery.util.data.IDataBuilder;
import lych.soullery.util.data.ShaderBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static lych.soullery.util.data.ShaderBuilder.createPassNode;
import static lych.soullery.util.data.ShaderBuilder.createUniform;

public class ShaderDataGen implements IDataProvider {
    public static final String MAIN = "minecraft:main";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Set<ShaderBuilder> shaderBuilders = new HashSet<>();
    private final DataGenerator generator;

    public ShaderDataGen(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DirectoryCache cache) {
        shaderBuilders.clear();
        registerShaders();
        generateShaders(cache);
    }

    protected void registerShaders() {
        reversion(ModShaderNames.REVERSION, ModConstants.REVERSION_INVERSE_AMOUNT);

        registerShader(getBuilder(ModShaderNames.SOUL_CONTROL)
                .addTarget(prefix("swap"))
                .addTarget(prefix("final"))
                .addPassNode(createPassNode("entity_outline", prefix("final"), prefix("swap"))
                        .addUniform(createUniform("RedMatrix", 0, 0, 0))
                        .addUniform(createUniform("GreenMatrix", 0.6, 0.6, 0.6))
                        .addUniform(createUniform("BlueMatrix", 0.7, 0.7, 0.7)))
                .addPassNode(createPassNode("color_convolve", MAIN, prefix("swap")))
                .addPassNode(createPassNode("blur", prefix("swap"), prefix("final"))
                        .addUniform(createUniform("BlurDir", 1, 0))
                        .addUniform(createUniform("Radius", 2)))
                .addPassNode(createPassNode("blur", prefix("final"), prefix("swap"))
                        .addUniform(createUniform("BlurDir", 0, 1))
                        .addUniform(createUniform("Radius", 2)))
                .addPassNode(createPassNode("blit", prefix("swap"), prefix("final"))));

        registerShader(getBuilder(ModShaderNames.SOUL_MOB).addTarget("swap")
                .addPassNode(createPassNode("color_convolve", MAIN, "swap")
                        .addUniform(createUniform("RedMatrix", 0, 0, 0))
                        .addUniform(createUniform("GreenMatrix", 0.27, 0.53, 0.1))
                        .addUniform(createUniform("BlueMatrix", 0.3, 0.59, 0.11)))
                .addPassNode(createPassNode("bits", "swap", MAIN)
                        .addUniform(createUniform("Resolution", 8))
                        .addUniform(createUniform("MosaicSize", 2))));
    }

    private void reversion(String name, double amount) {
        registerShader(getBuilder(name).addTarget("swap")
                .addPassNode(createPassNode("invert", MAIN, "swap")
                        .addUniform(createUniform("InverseAmount", amount)))
                .addPassNode(createPassNode("blit", "swap", MAIN)));
    }

    protected void generateShaders(DirectoryCache cache) {
        for (ShaderBuilder builder : shaderBuilders) {
            Path target = getPath(builder);
            try {
                IDataProvider.save(GSON, cache, builder.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void registerShader(ShaderBuilder shaderBuilder) {
        Preconditions.checkState(shaderBuilders.add(shaderBuilder), String.format("Duplicate shader: %s", shaderBuilder.getLocation()));
    }

    @Override
    public String getName() {
        return "Shaders: " + getModid();
    }

    protected String getModid() {
        return Soullery.MOD_ID;
    }

    protected ShaderBuilder getBuilder(String name) {
        return new ShaderBuilder(getModid(), name);
    }

    private Path getPath(IDataBuilder builder) {
        ResourceLocation location = builder.getLocation();
        return generator.getOutputFolder().resolve(String.format("assets/%s/shaders/post/%s.json", location.getNamespace(), location.getPath()));
    }

    private String prefix(String s) {
        return Soullery.MOD_ID + s;
    }
}
