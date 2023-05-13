package lych.soullery.world.gen.config;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.util.WeightedRandom;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Random;

public class StreetlightConfig implements IFeatureConfig {
    public static final Codec<StreetlightConfig> CODEC = RecordCodecBuilder.create(StreetlightConfig::makeCodec);

    private final int minHeight;
    private final int maxHeight;
    private final Selection<Selection<BlockState>> columnBlock;

    public StreetlightConfig(int minHeight, int maxHeight) {
        this(minHeight, maxHeight, Selections.selection(WeightedRandom.makeItem(WorldUtils.SOUL_STONE_BRICK_WALL, 9), WeightedRandom.makeItem(WorldUtils.SMOOTH_SOUL_STONE_WALL, 1)));
    }

    public StreetlightConfig(int minHeight, int maxHeight, Selection<Selection<BlockState>> columnBlock) {
        this.minHeight = Math.min(minHeight, maxHeight);
        this.maxHeight = Math.max(minHeight, maxHeight);
        this.columnBlock = columnBlock;
    }

    public Selection<BlockState> getRandomColumnType(Random random) {
        return getColumnBlocks().getRandom(random);
    }

    public Selection<Selection<BlockState>> getColumnBlocks() {
        return columnBlock;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getRandomHeight(Random random) {
        return getMinHeight() + random.nextInt(getMaxHeight() - getMinHeight() + 1);
    }

    private static App<RecordCodecBuilder.Mu<StreetlightConfig>, StreetlightConfig> makeCodec(RecordCodecBuilder.Instance<StreetlightConfig> instance) {
        return instance.group(Codec.INT.fieldOf("min_height").forGetter(StreetlightConfig::getMinHeight),
                Codec.INT.fieldOf("max_height").forGetter(StreetlightConfig::getMaxHeight),
                WorldUtils.MULTI_BLOCK_SELECTION_CODEC.fieldOf("column_blocks").forGetter(StreetlightConfig::getColumnBlocks)).apply(instance, StreetlightConfig::new);
    }
}
