package lych.soullery.world.gen.config;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Random;

public class WatchtowerConfig implements IFeatureConfig {
    public static final Codec<WatchtowerConfig> CODEC = RecordCodecBuilder.create(WatchtowerConfig::makeCodec);
    private final int minHeight;
    private final int maxHeight;
    private final Selection<BlockState> columnBlocks;
    private final Selection<BlockState> floorBlocks;
    private final Selection<BlockState> barBlocks;
    private final Selection<BlockState> topBlocks;

    public WatchtowerConfig(int minHeight, int maxHeight, Selection<BlockState> columnBlocks, Selection<BlockState> floorBlocks, Selection<BlockState> barBlocks, Selection<BlockState> topBlocks) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.columnBlocks = columnBlocks;
        this.floorBlocks = floorBlocks;
        this.barBlocks = barBlocks;
        this.topBlocks = topBlocks;
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

    public Selection<BlockState> getColumnBlocks() {
        return columnBlocks;
    }

    public Selection<BlockState> getFloorBlocks() {
        return floorBlocks;
    }

    public Selection<BlockState> getBarBlocks() {
        return barBlocks;
    }

    public Selection<BlockState> getTopBlocks() {
        return topBlocks;
    }

    private static App<RecordCodecBuilder.Mu<WatchtowerConfig>, WatchtowerConfig> makeCodec(RecordCodecBuilder.Instance<WatchtowerConfig> instance) {
        return instance.group(Codec.INT.fieldOf("min_height").forGetter(WatchtowerConfig::getMinHeight),
                Codec.INT.fieldOf("max_height").forGetter(WatchtowerConfig::getMaxHeight),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("column_blocks").forGetter(WatchtowerConfig::getColumnBlocks),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("floor_blocks").forGetter(WatchtowerConfig::getFloorBlocks),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("bar_blocks").forGetter(WatchtowerConfig::getBarBlocks),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("top_blocks").forGetter(WatchtowerConfig::getTopBlocks)).apply(instance, WatchtowerConfig::new);
    }
}
