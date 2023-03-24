package lych.soullery.util.data;

import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;

public class NoiseSettingsBuilder {
    private int height;
    private ScalingSettings noiseSamplingSettings;
    private SlideSettings topSlideSettings;
    private SlideSettings bottomSlideSettings;
    private int noiseSizeHorizontal;
    private int noiseSizeVertical;
    private double densityFactor;
    private double densityOffset;
    private boolean useSimplexSurfaceNoise;
    private boolean randomDensityOffset;
    private boolean islandNoiseOverride;
    private boolean isAmplified;

    public NoiseSettingsBuilder height(int height) {
        this.height = height;
        return this;
    }

    public NoiseSettingsBuilder noiseSamplingSettings(ScalingSettings noiseSamplingSettings) {
        this.noiseSamplingSettings = noiseSamplingSettings;
        return this;
    }

    public NoiseSettingsBuilder topSlideSettings(SlideSettings topSlideSettings) {
        this.topSlideSettings = topSlideSettings;
        return this;
    }

    public NoiseSettingsBuilder bottomSlideSettings(SlideSettings bottomSlideSettings) {
        this.bottomSlideSettings = bottomSlideSettings;
        return this;
    }

    public NoiseSettingsBuilder noiseSizeHorizontal(int noiseSizeHorizontal) {
        this.noiseSizeHorizontal = noiseSizeHorizontal;
        return this;
    }

    public NoiseSettingsBuilder noiseSizeVertical(int noiseSizeVertical) {
        this.noiseSizeVertical = noiseSizeVertical;
        return this;
    }

    public NoiseSettingsBuilder densityFactor(double densityFactor) {
        this.densityFactor = densityFactor;
        return this;
    }

    public NoiseSettingsBuilder densityOffset(double densityOffset) {
        this.densityOffset = densityOffset;
        return this;
    }

    public NoiseSettingsBuilder useSimplexSurfaceNoise() {
        this.useSimplexSurfaceNoise = true;
        return this;
    }

    public NoiseSettingsBuilder randomDensityOffset() {
        this.randomDensityOffset = true;
        return this;
    }

    public NoiseSettingsBuilder islandNoiseOverride() {
        this.islandNoiseOverride = true;
        return this;
    }

    public NoiseSettingsBuilder setAmplified() {
        this.isAmplified = true;
        return this;
    }

    public NoiseSettings build() {
        return new NoiseSettings(height, noiseSamplingSettings, topSlideSettings, bottomSlideSettings, noiseSizeHorizontal, noiseSizeVertical, densityFactor, densityOffset, useSimplexSurfaceNoise, randomDensityOffset, islandNoiseOverride, isAmplified);
    }
}