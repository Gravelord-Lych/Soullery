package lych.soullery.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import lych.soullery.util.SoulLandGenHelper;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class SoulCanyonCarver extends CanyonWorldCarver {
    public SoulCanyonCarver(Codec<ProbabilityConfig> codec) {
        super(codec);
        replaceableBlocks = ImmutableSet.<Block>builder().addAll(replaceableBlocks).addAll(SoulLandGenHelper.getReplaceableBlocks()).build();
    }

    @Override
    protected boolean carveBlock(IChunk chunk, Function<BlockPos, Biome> biomeGetter, BitSet carvingMask, Random random, BlockPos.Mutable carvePosition, BlockPos.Mutable carvePosAbove, BlockPos.Mutable carvePosBelow, int seaLevel, int chunkX, int chunkZ, int realX, int realZ, int stepX, int realY, int stepZ, MutableBoolean surface) {
        return SoulCaveCarver.carveInSoulLand(this::canReplaceBlock, chunk, biomeGetter, carvingMask, carvePosition, carvePosAbove, carvePosBelow, realX, realZ, stepX, realY, stepZ, surface);
    }
}
