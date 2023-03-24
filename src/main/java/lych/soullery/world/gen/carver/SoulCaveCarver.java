package lych.soullery.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import lych.soullery.fluid.ModFluids;
import lych.soullery.util.SoulLandGenHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class SoulCaveCarver extends CaveWorldCarver {
    protected static final FluidState SOUL_LAVA = ModFluids.SOUL_LAVA.defaultFluidState();
    protected static final int SOUL_LAVA_HEIGHT = 11;

    public SoulCaveCarver(Codec<ProbabilityConfig> codec, int genHeight) {
        super(codec, genHeight);
        replaceableBlocks = ImmutableSet.<Block>builder().addAll(replaceableBlocks).addAll(SoulLandGenHelper.getReplaceableBlocks()).build();
    }

    @Override
    protected boolean carveBlock(IChunk chunk, Function<BlockPos, Biome> biomeGetter, BitSet carvingMask, Random random, BlockPos.Mutable carvePosition, BlockPos.Mutable carvePosAbove, BlockPos.Mutable carvePosBelow, int seaLevel, int chunkX, int chunkZ, int realX, int realZ, int stepX, int realY, int stepZ, MutableBoolean surface) {
        return carveInSoulLand(this::canReplaceBlock, chunk, biomeGetter, carvingMask, carvePosition, carvePosAbove, carvePosBelow, realX, realZ, stepX, realY, stepZ, surface);
    }

    @SuppressWarnings("deprecation")
    protected static boolean carveInSoulLand(BiPredicate<? super BlockState, ? super BlockState> canReplacePredicate, IChunk chunk, Function<BlockPos, Biome> biomeGetter, BitSet carvingMask, BlockPos.Mutable carvePosition, BlockPos.Mutable carvePosAbove, BlockPos.Mutable carvePosBelow, int realX, int realZ, int stepX, int realY, int stepZ, MutableBoolean surface) {
        int bitIndex = stepX | stepZ << 4 | realY << 8;
        if (!carvingMask.get(bitIndex)) {
            carvingMask.set(bitIndex);
            carvePosition.set(realX, realY, realZ);
            BlockState carvePosState = chunk.getBlockState(carvePosition);
            BlockState carvePosAboveState = chunk.getBlockState(carvePosAbove.setWithOffset(carvePosition, Direction.UP));
            BlockState topMaterial = biomeGetter.apply(carvePosition).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
            if ((carvePosState.is(Blocks.SOUL_SOIL) || carvePosState.is(Blocks.SOUL_SAND) || carvePosState.is(topMaterial.getBlock())) && carvePosAboveState.isAir()) {
                surface.setTrue();
            }
            if (canReplacePredicate.test(carvePosState, carvePosAboveState)) {
                if (realY < SOUL_LAVA_HEIGHT) {
                    chunk.setBlockState(carvePosition, SOUL_LAVA.createLegacyBlock(), false);
                } else {
                    chunk.setBlockState(carvePosition, CAVE_AIR, false);
                    if (surface.isTrue()) {
                        carvePosBelow.setWithOffset(carvePosition, Direction.DOWN);
                        if (chunk.getBlockState(carvePosBelow).is(Blocks.SOUL_SAND)) {
                            chunk.setBlockState(carvePosBelow, topMaterial, false);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}
