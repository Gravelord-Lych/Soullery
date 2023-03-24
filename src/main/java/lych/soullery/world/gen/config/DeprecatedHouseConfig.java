package lych.soullery.world.gen.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedHouseConfig implements IFeatureConfig {
    public static final Codec<DeprecatedHouseConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(BlockState.CODEC).fieldOf("fence_blocks")
                            .forGetter(DeprecatedHouseConfig::getFenceBlocks),
                    Codec.list(BlockState.CODEC).fieldOf("slab_blocks")
                            .forGetter(DeprecatedHouseConfig::getFenceBlocks),
                    Codec.list(BlockState.CODEC).fieldOf("stairs_blocks")
                            .forGetter(DeprecatedHouseConfig::getFenceBlocks),
                    Codec.list(BlockState.CODEC).fieldOf("wall_blocks")
                            .forGetter(DeprecatedHouseConfig::getFenceBlocks),
                    Codec.list(BlockState.CODEC).fieldOf("window_blocks")
                            .forGetter(DeprecatedHouseConfig::getFenceBlocks))
            .apply(instance, DeprecatedHouseConfig::new));

    private final List<BlockState> fenceBlocks;
    private final List<BlockState> slabBlocks;
    private final List<BlockState> stairsBlocks;
    private final List<BlockState> wallBlocks;
    private final List<BlockState> windowBlocks;

    public DeprecatedHouseConfig(List<BlockState> fenceBlocks, List<BlockState> slabBlocks, List<BlockState> stairsBlocks, List<BlockState> wallBlocks, List<BlockState> windowBlocks) {
        this.fenceBlocks = fenceBlocks;
        this.slabBlocks = slabBlocks;
        this.stairsBlocks = stairsBlocks;
        this.wallBlocks = wallBlocks;
        this.windowBlocks = windowBlocks;
    }

    public List<BlockState> getFenceBlocks() {
        return fenceBlocks;
    }

    public List<BlockState> getSlabBlocks() {
        return slabBlocks;
    }

    public List<BlockState> getStairsBlocks() {
        return stairsBlocks;
    }

    public List<BlockState> getWallBlocks() {
        return wallBlocks;
    }

    public List<BlockState> getWindowBlocks() {
        return windowBlocks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<BlockState> fenceBlocks = new ArrayList<>();
        private final List<BlockState> slabBlocks = new ArrayList<>();
        private final List<BlockState> stairsBlocks = new ArrayList<>();
        private final List<BlockState> wallBlocks = new ArrayList<>();
        private final List<BlockState> windowBlocks = new ArrayList<>();

        public Builder addFenceBlock(BlockState fenceBlock) {
            return addFenceBlock(fenceBlock, 1);
        }

        public Builder addFenceBlock(BlockState fenceBlock, int weight) {
            for (int i = 0; i < weight; i++) {
                fenceBlocks.add(fenceBlock);
            }
            return this;
        }

        public Builder addSlabBlock(BlockState slabBlock) {
            return addSlabBlock(slabBlock, 1);
        }

        public Builder addSlabBlock(BlockState slabBlock, int weight) {
            for (int i = 0; i < weight; i++) {
                slabBlocks.add(slabBlock);
            }
            return this;
        }

        public Builder addStairsBlock(BlockState stairsBlock) {
            return addStairsBlock(stairsBlock, 1);
        }

        public Builder addStairsBlock(BlockState stairsBlock, int weight) {
            for (int i = 0; i < weight; i++) {
                stairsBlocks.add(stairsBlock);
            }
            return this;
        }

        public Builder addWallBlock(BlockState wallBlock) {
            return addWallBlock(wallBlock, 1);
        }

        public Builder addWallBlock(BlockState wallBlock, int weight) {
            for (int i = 0; i < weight; i++) {
                wallBlocks.add(wallBlock);
            }
            return this;
        }

        public Builder addWindowBlock(BlockState windowBlock) {
            return addWindowBlock(windowBlock, 1);
        }

        public Builder addWindowBlock(BlockState windowBlock, int weight) {
            for (int i = 0; i < weight; i++) {
                windowBlocks.add(windowBlock);
            }
            return this;
        }

        public DeprecatedHouseConfig build() {
            return new DeprecatedHouseConfig(fenceBlocks, slabBlocks, stairsBlocks, wallBlocks, windowBlocks);
        }
    }
}
