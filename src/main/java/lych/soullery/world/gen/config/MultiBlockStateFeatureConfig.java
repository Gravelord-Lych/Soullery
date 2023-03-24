package lych.soullery.world.gen.config;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.util.CollectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.*;

public class MultiBlockStateFeatureConfig implements IFeatureConfig {
    public static final Codec<MultiBlockStateFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(BlockState.CODEC)
                    .fieldOf("possible_states")
                    .forGetter(MultiBlockStateFeatureConfig::getPossibleStates))
            .apply(instance, MultiBlockStateFeatureConfig::new));

    private final List<BlockState> possibleStates;

    @SafeVarargs
    public MultiBlockStateFeatureConfig(Pair<BlockState, Integer>... pairs) {
        List<BlockState> possibleStates = new ArrayList<>(pairs.length * 3);
        for (Pair<BlockState, Integer> pair : pairs) {
            int weight = pair.getSecond();
            for (int i = 0; i < weight; i++) {
                possibleStates.add(pair.getFirst());
            }
        }
        this.possibleStates = possibleStates;
    }

    public MultiBlockStateFeatureConfig(BlockState... possibleStates) {
        this(new ArrayList<>(Arrays.asList(possibleStates)));
    }

    public MultiBlockStateFeatureConfig(List<BlockState> possibleStates) {
        this.possibleStates = possibleStates;
    }

    public List<BlockState> getPossibleStates() {
        return possibleStates;
    }

    public BlockState randomState(Random random) {
        Objects.requireNonNull(possibleStates, "PossibleStates should be non-null");
        Preconditions.checkState(!possibleStates.isEmpty(), "PossibleStates should not be empty");
        return CollectionUtils.getNonnullRandom(possibleStates, random);
    }
}
