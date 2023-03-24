package lych.soullery.block;

import lych.soullery.world.gen.feature.SLTwistingVineFeature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.NetherVegetationFeature;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class HyphalSoulSoilBlock extends Block implements IGrowable {
    public HyphalSoulSoilBlock(Properties properties) {
        super(properties);
    }

    private static boolean canBeHyphal(BlockState state, IWorldReader reader, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = reader.getBlockState(above);
        int light = LightEngine.getLightBlockInto(reader, state, pos, aboveState, above, Direction.UP, aboveState.getLightBlock(reader, above));
        return light < reader.getMaxLightLevel();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!canBeHyphal(state, world, pos)) {
            world.setBlockAndUpdate(pos, Blocks.SOUL_SOIL.defaultBlockState());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValidBonemealTarget(IBlockReader reader, BlockPos pos, BlockState state, boolean clientside) {
        return reader.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState belowState = world.getBlockState(pos);
        BlockPos above = pos.above();
        if (belowState.is(ModBlocks.CRIMSON_HYPHAL_SOIL)) {
            NetherVegetationFeature.place(world, random, above, Features.Configs.CRIMSON_FOREST_CONFIG, 3, 1);
        } else if (belowState.is(ModBlocks.WARPED_HYPHAL_SOIL)) {
            NetherVegetationFeature.place(world, random, above, Features.Configs.WARPED_FOREST_CONFIG, 3, 1);
            NetherVegetationFeature.place(world, random, above, Features.Configs.NETHER_SPROUTS_CONFIG, 3, 1);
            if (random.nextInt(8) == 0) {
                SLTwistingVineFeature.place(world, random, above, 3, 1, 2);
            }
        }
    }
}
