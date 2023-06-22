package lych.soullery.world.gen.config;

import lych.soullery.block.ModBlocks;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import static lych.soullery.util.WeightedRandom.makeItem;

public final class SoulTowerConfigs {
    public static final Selection<BlockState> OUTER_SOUL_STONE_BRICKS = Selections.selection(
            makeItem(ModBlocks.SOUL_STONE_BRICKS.defaultBlockState(), 30),
            makeItem(ModBlocks.CRACKED_SOUL_STONE_BRICKS.defaultBlockState(), 10),
            makeItem(ModBlocks.SOUL_STONE.defaultBlockState(), 2),
            makeItem(ModBlocks.SMOOTH_SOUL_STONE.defaultBlockState(), 1),
            makeItem(Blocks.AIR.defaultBlockState(), 1),
            makeItem(Blocks.COBWEB.defaultBlockState(), 1));
    public static final Selection<BlockState> INNER_SOUL_STONE_BRICKS = Selections.selection(
            makeItem(ModBlocks.SOUL_STONE_BRICKS.defaultBlockState(), 3),
            makeItem(ModBlocks.CRACKED_SOUL_STONE_BRICKS.defaultBlockState(), 1));
    public static final Selection<BlockState> INNER_SOUL_STONE_BRICK_SLAB = Selections.selection(
            makeItem(ModBlocks.SOUL_STONE_BRICK_SLAB.defaultBlockState(), 3),
            makeItem(ModBlocks.CRACKED_SOUL_STONE_BRICK_SLAB.defaultBlockState(), 1));
    public static final Selection<BlockState> GLOWSTONE = Selections.selection(
            makeItem(ModBlocks.GLOWSTONE_BRICKS.core().defaultBlockState(), 100),
            makeItem(Blocks.GLOWSTONE.defaultBlockState(), 20),
            makeItem(Blocks.AIR.defaultBlockState(), 7));
    public static final Selection<BlockState> PROFOUND_STONE = Selections.selection(
            makeItem(ModBlocks.PROFOUND_STONE_BRICKS.core().defaultBlockState(), 100),
            makeItem(ModBlocks.CRACKED_PROFOUND_STONE_BRICKS.core().defaultBlockState(), 20),
            makeItem(ModBlocks.PROFOUND_STONE.core().defaultBlockState(), 10),
            makeItem(Blocks.AIR.defaultBlockState(), 7));
    public static final Selection<BlockState> OUTER_NETHER_BRICKS = Selections.selection(
            makeItem(Blocks.NETHER_BRICKS.defaultBlockState(), 36),
            makeItem(Blocks.CRACKED_NETHER_BRICKS.defaultBlockState(), 12),
            makeItem(Blocks.CHISELED_NETHER_BRICKS.defaultBlockState(), 3),
            makeItem(Blocks.AIR.defaultBlockState(), 1));
    public static final Selection<BlockState> INNER_NETHER_BRICKS = Selections.selection(
            makeItem(Blocks.NETHER_BRICKS.defaultBlockState(), 3),
            makeItem(Blocks.CRACKED_NETHER_BRICKS.defaultBlockState(), 1));
    public static final Selection<BlockState> INNER_NETHER_BRICK_SLAB = Selections.selection(makeItem(Blocks.NETHER_BRICK_SLAB.defaultBlockState(), 1));
    public static final Selection<BlockState> OUTER_RED_NETHER_BRICKS = Selections.selection(
            makeItem(Blocks.RED_NETHER_BRICKS.defaultBlockState(), 60),
            makeItem(Blocks.NETHER_BRICKS.defaultBlockState(), 6),
            makeItem(Blocks.NETHER_WART_BLOCK.defaultBlockState(), 8),
            makeItem(Blocks.NETHERRACK.defaultBlockState(), 6),
            makeItem(Blocks.AIR.defaultBlockState(), 2),
            makeItem(Blocks.CRACKED_NETHER_BRICKS.defaultBlockState(), 2),
            makeItem(Blocks.CHISELED_NETHER_BRICKS.defaultBlockState(), 1));
    public static final Selection<BlockState> INNER_RED_NETHER_BRICKS = Selections.selection(
            makeItem(Blocks.RED_NETHER_BRICKS.defaultBlockState(), 54),
            makeItem(Blocks.NETHER_BRICKS.defaultBlockState(), 3),
            makeItem(Blocks.CRACKED_NETHER_BRICKS.defaultBlockState(), 1));
    public static final Selection<BlockState> INNER_RED_NETHER_BRICK_SLAB = Selections.selection(
            makeItem(Blocks.RED_NETHER_BRICK_SLAB.defaultBlockState(), 18),
            makeItem(Blocks.NETHER_BRICK_SLAB.defaultBlockState(), 1));
    public static final Selection<BlockState> CRIMSON_PLANKS = Selections.selection(
            makeItem(Blocks.CRIMSON_PLANKS.defaultBlockState(), 100),
            makeItem(Blocks.NETHER_WART_BLOCK.defaultBlockState(), 15),
            makeItem(Blocks.AIR.defaultBlockState(), 7),
            makeItem(Blocks.NETHERRACK.defaultBlockState(), 2)
    );
    public static final Selection<BlockState> WARPED_PLANKS = Selections.selection(
            makeItem(Blocks.WARPED_PLANKS.defaultBlockState(), 100),
            makeItem(Blocks.WARPED_WART_BLOCK.defaultBlockState(), 20),
            makeItem(Blocks.AIR.defaultBlockState(), 7));
    public static final Selection<BlockState> LIGHT_BLUE_STAINED_GLASS = Selections.selection(makeItem(Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(), 1));
    public static final Selection<BlockState> ORANGE_STAINED_GLASS = Selections.selection(makeItem(Blocks.ORANGE_STAINED_GLASS.defaultBlockState(), 1));
    public static final Selection<BlockState> RED_STAINED_GLASS = Selections.selection(makeItem(Blocks.RED_STAINED_GLASS.defaultBlockState(), 1));
    public static final Selection<BlockState> GREEN_STAINED_GLASS = Selections.selection(makeItem(Blocks.GREEN_STAINED_GLASS.defaultBlockState(), 1));

    private SoulTowerConfigs() {}
}
