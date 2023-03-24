package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import lych.soullery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.WeepingVineFeature;

import java.util.Random;

public class SLWeepingVineFeature extends WeepingVineFeature {
    public SLWeepingVineFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig config) {
        if (reader.isEmptyBlock(pos)) {
            BlockState state = reader.getBlockState(pos.above());
            if (!state.is(Blocks.SOUL_SOIL) && !state.is(ModBlocks.CRIMSON_HYPHAL_SOIL) && !state.is(Blocks.NETHER_WART_BLOCK)) {
                return false;
            } else {
                placeRoofNetherWart(reader, random, pos);
                placeRoofWeepingVines(reader, random, pos);
                return true;
            }
        }
        return false;
    }

    private void placeRoofNetherWart(IWorld world, Random random, BlockPos pos) {
        world.setBlock(pos, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
        BlockPos.Mutable mutable1 = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();

        for (int i = 0; i < 200; i++) {
            mutable1.setWithOffset(pos, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
            if (world.isEmptyBlock(mutable1)) {
                int count = 0;
                for (Direction direction : Direction.values()) {
                    BlockState state = world.getBlockState(mutable2.setWithOffset(mutable1, direction));
                    if (state.is(Blocks.SOUL_SOIL) || state.is(ModBlocks.CRIMSON_HYPHAL_SOIL) || state.is(Blocks.NETHER_WART_BLOCK)) {
                        count++;
                    }
                    if (count > 1) {
                        break;
                    }
                }
                if (count == 1) {
                    world.setBlock(mutable1, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
                }
            }
        }

    }

    private void placeRoofWeepingVines(IWorld wold, Random random, BlockPos pos) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int i = 0; i < 100; i++) {
            mutablePos.setWithOffset(pos, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
            if (wold.isEmptyBlock(mutablePos)) {
                BlockState state = wold.getBlockState(mutablePos.above());
                if (state.is(Blocks.SOUL_SOIL) || state.is(ModBlocks.WARPED_HYPHAL_SOIL) || state.is(Blocks.NETHER_WART_BLOCK)) {
                    int count = MathHelper.nextInt(random, 1, 8);
                    if (random.nextInt(6) == 0) {
                        count *= 2;
                    }
                    if (random.nextInt(5) == 0) {
                        count = 1;
                    }
                    int minAge = 17;
                    int maxAge = 25;
                    placeWeepingVinesColumn(wold, random, mutablePos, count, minAge, maxAge);
                }
            }
        }
    }
}
