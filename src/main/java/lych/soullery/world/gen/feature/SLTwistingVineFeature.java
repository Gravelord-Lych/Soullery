package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import lych.soullery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TwistingVineFeature;

import java.util.Random;

public class SLTwistingVineFeature extends TwistingVineFeature {
    public SLTwistingVineFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig p_241855_5_) {
        return SLTwistingVineFeature.place(reader, random, pos, 8, 4, 8);
    }

    public static boolean place(IWorld world, Random random, BlockPos pos, int horizontalRadius, int verticalRadius, int maxCount) {
        if (isInvalidPlacementLocation(world, pos)) {
            return false;
        } else {
            placeTwistingVines(world, random, pos, horizontalRadius, verticalRadius, maxCount);
            return true;
        }
    }

    private static void placeTwistingVines(IWorld world, Random random, BlockPos pos, int horizontalRadius, int verticalRadius, int maxCount) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for(int i = 0; i < horizontalRadius * horizontalRadius; i++) {
            mutablePos.set(pos).move(MathHelper.nextInt(random, -horizontalRadius, horizontalRadius), MathHelper.nextInt(random, -verticalRadius, verticalRadius), MathHelper.nextInt(random, -horizontalRadius, horizontalRadius));
            if (findFirstAirBlockAboveGround(world, mutablePos) && !isInvalidPlacementLocation(world, mutablePos)) {
                int count = MathHelper.nextInt(random, 1, maxCount);
                if (random.nextInt(6) == 0) {
                    count *= 2;
                }
                if (random.nextInt(5) == 0) {
                    count = 1;
                }
                int minAge = 17;
                int maxAge = 25;
                placeWeepingVinesColumn(world, random, mutablePos, count, minAge, maxAge);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean findFirstAirBlockAboveGround(IWorld world, BlockPos.Mutable mutablePos) {
        do {
            mutablePos.move(0, -1, 0);
            if (World.isOutsideBuildHeight(mutablePos)) {
                return false;
            }
        } while (world.getBlockState(mutablePos).isAir());

        mutablePos.move(0, 1, 0);
        return true;
    }

    private static boolean isInvalidPlacementLocation(IWorld world, BlockPos pos) {
        if (!world.isEmptyBlock(pos)) {
            return true;
        } else {
            BlockState state = world.getBlockState(pos.below());
            return !state.is(ModBlocks.WARPED_HYPHAL_SOIL) && !state.is(Blocks.SOUL_SOIL) && !state.is(Blocks.SOUL_SAND) && !state.is(Blocks.WARPED_WART_BLOCK);
        }
    }
}
