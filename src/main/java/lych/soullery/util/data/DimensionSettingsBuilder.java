package lych.soullery.util.data;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;

import static java.util.Objects.requireNonNull;

public class DimensionSettingsBuilder {
    private DimensionStructuresSettings structureSettings;
    private NoiseSettings noiseSettings;
    private BlockState defaultBlock;
    private BlockState defaultFluid;
    private int bedrockRoofPosition;
    private int bedrockFloorPosition;
    private int seaLevel;
    private boolean disableMobGeneration;

    public DimensionSettingsBuilder structureSettings(DimensionStructuresSettings structureSettings) {
        this.structureSettings = structureSettings;
        return this;
    }

    public DimensionSettingsBuilder noiseSettings(NoiseSettings noiseSettings) {
        this.noiseSettings = noiseSettings;
        return this;
    }

    public DimensionSettingsBuilder defaultBlock(BlockState defaultBlock) {
        this.defaultBlock = defaultBlock;
        return this;
    }

    public DimensionSettingsBuilder defaultFluid(BlockState defaultFluid) {
        this.defaultFluid = defaultFluid;
        return this;
    }

    public DimensionSettingsBuilder bedrockRoofPosition(int bedrockRoofPosition) {
        this.bedrockRoofPosition = bedrockRoofPosition;
        return this;
    }

    public DimensionSettingsBuilder bedrockFloorPosition(int bedrockFloorPosition) {
        this.bedrockFloorPosition = bedrockFloorPosition;
        return this;
    }

    public DimensionSettingsBuilder seaLevel(int seaLevel) {
        this.seaLevel = seaLevel;
        return this;
    }

    public DimensionSettingsBuilder disableMobGeneration(boolean disableMobGeneration) {
        this.disableMobGeneration = disableMobGeneration;
        return this;
    }

    public DimensionSettings build() {
        return new DimensionSettings(requireNonNull(structureSettings), requireNonNull(noiseSettings), requireNonNull(defaultBlock), requireNonNull(defaultFluid), bedrockRoofPosition, bedrockFloorPosition, seaLevel, disableMobGeneration);
    }
}