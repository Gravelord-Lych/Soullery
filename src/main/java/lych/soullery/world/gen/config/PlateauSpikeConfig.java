package lych.soullery.world.gen.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.world.gen.feature.PlateauSpikeFeature.Spike;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.List;

public class PlateauSpikeConfig implements IFeatureConfig {
    public static final Codec<PlateauSpikeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("center").forGetter(PlateauSpikeConfig::getCenter),
            Spike.CODEC.listOf().fieldOf("spikes").forGetter(PlateauSpikeConfig::getSpikes)).apply(instance, PlateauSpikeConfig::new)
    );
    private final BlockPos center;
    private final List<Spike> spikes;

    public PlateauSpikeConfig(BlockPos center, List<Spike> spikes) {
        this.center = center;
        this.spikes = spikes;
    }

    public BlockPos getCenter() {
        return center;
    }

    public List<Spike> getSpikes() {
        return spikes;
    }
}
