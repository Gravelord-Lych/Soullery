package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.List;

public class SLSpikeConfig implements IFeatureConfig {
    public static final Codec<SLSpikeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BlockState.CODEC.fieldOf("to_place").forGetter(config -> config.toPlace),
                    BlockState.CODEC.listOf().fieldOf("place_on").forGetter(config -> config.placeOn),
                    Codec.DOUBLE.fieldOf("high_probability").forGetter(config -> config.highProbability))
            .apply(instance, SLSpikeConfig::new));
    public final BlockState toPlace;
    public final List<BlockState> placeOn;
    public final double highProbability;

    public SLSpikeConfig(BlockState toPlace, List<BlockState> placeOn, double highProbability) {
        this.toPlace = toPlace;
        this.placeOn = placeOn;
        this.highProbability = highProbability;
    }
}
