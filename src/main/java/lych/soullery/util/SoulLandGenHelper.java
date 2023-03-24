package lych.soullery.util;

import com.google.common.collect.ImmutableSet;
import lych.soullery.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class SoulLandGenHelper {
    private static final Set<Block> REPLACEABLE_BLOCKS = new HashSet<>(Arrays.asList(ModBlocks.PARCHED_SOIL, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, ModBlocks.SOUL_STONE, ModBlocks.REFINED_SOUL_SAND, ModBlocks.REFINED_SOUL_SOIL, ModBlocks.CRIMSON_HYPHAL_SOIL, ModBlocks.WARPED_HYPHAL_SOIL));

    private SoulLandGenHelper() {}

    public static ImmutableSet<Block> getReplaceableBlocks() {
        return ImmutableSet.copyOf(REPLACEABLE_BLOCKS);
    }

    public static void registerReplaceableBlock(Block block) {
        REPLACEABLE_BLOCKS.add(block);
    }
}
