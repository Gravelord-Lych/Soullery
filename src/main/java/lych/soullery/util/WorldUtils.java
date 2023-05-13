package lych.soullery.util;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.block.ModBlocks;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WallHeight;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import static lych.soullery.util.WeightedRandom.makeItem;

public final class WorldUtils {
    public static final Codec<WeightedRandom.ItemImpl<BlockState>> WEIGHTED_BLOCK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("block").forGetter(WeightedRandom.ItemImpl::get),
            Codec.INT.fieldOf("weight").forGetter(WeightedRandom.ItemImpl::getWeight)).apply(instance, WeightedRandom::makeItem));
    public static final Codec<Selection<BlockState>> BLOCK_SELECTION_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.list(WEIGHTED_BLOCK_CODEC).fieldOf("contents").forGetter(Selection::getAllItems)).apply(instance, Selections::selection));
    public static final Codec<WeightedRandom.ItemImpl<Selection<BlockState>>> WEIGHTED_BLOCK_SELECTION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BLOCK_SELECTION_CODEC.fieldOf("sub_selection").forGetter(WeightedRandom.ItemImpl::get),
            Codec.INT.fieldOf("weight").forGetter(WeightedRandom.ItemImpl::getWeight)).apply(instance, WeightedRandom::makeItem));
    public static final Codec<Selection<Selection<BlockState>>> MULTI_BLOCK_SELECTION_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.list(WEIGHTED_BLOCK_SELECTION_CODEC).fieldOf("sub_selections").forGetter(Selection::getAllItems)).apply(instance, Selections::selection));
    public static final Selection<BlockState> SMOOTH_SOUL_STONE = Selections.selection(makeItem(ModBlocks.SMOOTH_SOUL_STONE.defaultBlockState(), 1));
    public static final Selection<BlockState> SMOOTH_SOUL_STONE_SLAB = Selections.selection(makeItem(ModBlocks.SMOOTH_SOUL_STONE_SLAB.defaultBlockState(), 1));
    public static final Selection<BlockState> SMOOTH_SOUL_STONE_STAIRS = Selections.selection(makeItem(ModBlocks.SMOOTH_SOUL_STONE_STAIRS.defaultBlockState(), 1));
    public static final Selection<BlockState> SMOOTH_SOUL_STONE_WALL = Selections.selection(makeItem(ModBlocks.SMOOTH_SOUL_STONE_WALL.defaultBlockState(), 1));

    public static final Selection<BlockState> SOUL_STONE_BRICKS = Selections.selection(makeItem(ModBlocks.SOUL_STONE_BRICKS.defaultBlockState(), 4), makeItem(ModBlocks.CRACKED_SOUL_STONE_BRICKS.defaultBlockState(), 1));
    public static final Selection<BlockState> SOUL_STONE_BRICK_SLAB = Selections.selection(makeItem(ModBlocks.SOUL_STONE_BRICK_SLAB.defaultBlockState(), 4), makeItem(ModBlocks.CRACKED_SOUL_STONE_BRICK_SLAB.defaultBlockState(), 1));
    public static final Selection<BlockState> SOUL_STONE_BRICK_STAIRS = Selections.selection(makeItem(ModBlocks.SOUL_STONE_BRICK_STAIRS.defaultBlockState(), 4), makeItem(ModBlocks.CRACKED_SOUL_STONE_BRICK_STAIRS.defaultBlockState(), 1));
    public static final Selection<BlockState> SOUL_STONE_BRICK_WALL = Selections.selection(makeItem(ModBlocks.SOUL_STONE_BRICK_WALL.defaultBlockState(), 4), makeItem(ModBlocks.CRACKED_SOUL_STONE_BRICK_WALL.defaultBlockState(), 1));
    public static final Selection<BlockState> SOUL_METAL_BARS = Selections.selection(makeItem(ModBlocks.SOUL_METAL_BARS.defaultBlockState(), 1));

    private WorldUtils() {}

    public static List<BlockPos> getNearbyBlocks(BlockPos pos) {
        return getNearbyBlocks(pos, 1);
    }

    public static List<BlockPos> getNearbyBlocks(BlockPos pos, int radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be positive");
        List<BlockPos> blocks = new ArrayList<>((radius + 2) * (radius + 2) * (radius + 2));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
//                  Exclude self
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    blocks.add(pos.offset(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static BlockPos calculateSummonPosition2(int x, int z, World world, PositionCalculators.IYCalculator2 calculator) {
        return new BlockPos(x, calculator.calculate(x, z, world), z);
    }

    public static BlockPos calculateSummonPosition2(double x, double z, World world, PositionCalculators.IYCalculator2 calculator) {
        return calculateSummonPosition2((int) x, (int) z, world, calculator);
    }

    public static BlockPos calculateSummonPosition3(int x, int y, int z, World world, PositionCalculators.IYCalculator3 calculator) {
        return calculateSummonPosition3(new BlockPos(x, y, z), world, calculator);
    }

    public static BlockPos calculateSummonPosition3(double x, double y, double z, World world, PositionCalculators.IYCalculator3 calculator) {
        return calculateSummonPosition3(new BlockPos(x, y, z), world, calculator);
    }

    public static BlockPos calculateSummonPosition3(BlockPos pos, World world, PositionCalculators.IYCalculator3 calculator) {
        return new BlockPos(pos.getX(), calculator.calculate(pos, world), pos.getZ());
    }

    public static Vector3d calculateSummonPosition3(Vector3d pos, World world, PositionCalculators.IYCalculator3 calculator) {
        int y = calculator.calculate(new BlockPos(pos), world);
        return new Vector3d(pos.x, y, pos.z);
    }

    public static EnumProperty<WallHeight> wallHeightByDirection(Direction direction) {
        Objects.requireNonNull(direction);
        switch (direction) {
            case NORTH:
                return WallBlock.NORTH_WALL;
            case SOUTH:
                return WallBlock.SOUTH_WALL;
            case EAST:
                return WallBlock.EAST_WALL;
            case WEST:
                return WallBlock.WEST_WALL;
            default:
                throw new IllegalArgumentException("Only horizontal directions are allowed");
        }
    }

    public static void placeUp(IWorldWriter writer, Random random, BlockPos start, int height, BlockState state, TriConsumer<? super IWorldWriter, ? super BlockPos, ? super BlockState> placer) {
        placeUp(writer, random, start, start.getY() + height, r -> state, placer);
    }

    public static void placeUp(IWorldWriter writer, Random random, BlockPos start, int height, Function<? super Random, ? extends BlockState> state, TriConsumer<? super IWorldWriter, ? super BlockPos, ? super BlockState> placer) {
        placeUp(writer, random, start.getX(), start.getZ(), start.getY(), start.getY() + height, state, placer);
    }

    public static void placeUp(IWorldWriter writer, Random random, int x, int z, int startY, int toY, BlockState state, TriConsumer<? super IWorldWriter, ? super BlockPos, ? super BlockState> placer) {
        placeUp(writer, random, x, z, startY, toY, r -> state, placer);
    }

    public static void placeUp(IWorldWriter writer, Random random, int x, int z, int startY, int toY, Function<? super Random, ? extends BlockState> state, TriConsumer<? super IWorldWriter, ? super BlockPos, ? super BlockState> placer) {
        for (int y = startY; y <= toY; y++) {
            placer.accept(writer, new BlockPos(x, y, z), state.apply(random));
        }
    }
}
