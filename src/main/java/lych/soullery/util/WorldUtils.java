package lych.soullery.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.block.ModBlocks;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.*;

import static java.lang.Math.abs;
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

    public static StructurePiece.BlockSelector selectorFrom(Selection<BlockState> selection) {
        return selectorFrom(selection, selection);
    }

    public static StructurePiece.BlockSelector hollowSelectorFrom(Selection<BlockState> selection) {
        return selectorFrom(selection, Blocks.AIR.defaultBlockState());
    }

    public static StructurePiece.BlockSelector caveSelectorFrom(Selection<BlockState> selection) {
        return selectorFrom(selection, Blocks.CAVE_AIR.defaultBlockState());
    }

    public static StructurePiece.BlockSelector selectorFrom(Selection<BlockState> selection, BlockState air) {
        return selectorFrom(selection, Selections.selection(WeightedRandom.makeItem(air, 1)));
    }

    public static StructurePiece.BlockSelector selectorFrom(Selection<BlockState> blockSelection, Selection<BlockState> airSelection) {
        return new StructurePiece.BlockSelector() {
            @Override
            public void next(Random random, int x, int y, int z, boolean edge) {
                next = edge ? blockSelection.getRandom(random) : airSelection.getRandom(random);
            }
        };
    }

    public static ListNBT save(Selection<BlockState> selection) {
        ListNBT listNBT = new ListNBT();
        for (WeightedRandom.ItemImpl<BlockState> item : selection.getAllItems()) {
            CompoundNBT compoundNBT = NBTUtil.writeBlockState(item.get());
            compoundNBT.putInt("BlockWeight", item.getWeight());
            listNBT.add(compoundNBT);
        }
        return listNBT;
    }

    public static Selection<BlockState> load(ListNBT nbt) {
        ImmutableList.Builder<WeightedRandom.ItemImpl<BlockState>> builder = new ImmutableList.Builder<>();
        for (int i = 0; i < nbt.size(); i++) {
            CompoundNBT compoundNBT = nbt.getCompound(i);
            BlockState state = NBTUtil.readBlockState(compoundNBT);
            int weight = compoundNBT.getInt("BlockWeight");
            builder.add(WeightedRandom.makeItem(state, weight));
        }
        return Selections.selection(builder.build());
    }

    public static StructureAccessors group(StructureBlockPlacer placer, IntBinaryOperator worldXGetter, IntUnaryOperator worldYGetter, IntBinaryOperator worldZGetter) {
        return new StructureAccessors(placer, worldXGetter, worldYGetter, worldZGetter);
    }

    public static <T extends TileEntity> boolean placeBlockEntity(StructureAccessors accessors, ISeedReader reader, BlockState state, Class<T> type, int x, int y, int z, MutableBoundingBox boundingBox, Consumer<? super T> op) {
        return placeBlockEntity(accessors.getPlacer(), accessors.getWorldXGetter(), accessors.getWorldYGetter(), accessors.getWorldZGetter(), reader, state, type, x, y, z, boundingBox, op);
    }

    public static <T extends TileEntity> boolean placeBlockEntity(StructureBlockPlacer placer, IntBinaryOperator worldXGetter, IntUnaryOperator worldYGetter, IntBinaryOperator worldZGetter, ISeedReader reader, BlockState state, Class<T> type, int x, int y, int z, MutableBoundingBox boundingBox, Consumer<? super T> op) {
        BlockPos worldPos = new BlockPos(worldXGetter.applyAsInt(x, z), worldYGetter.applyAsInt(y), worldZGetter.applyAsInt(x, z));
        if (!boundingBox.isInside(worldPos)) {
            return false;
        }
        placer.placeBlock(reader, state, x, y, z, boundingBox);
        TileEntity blockEntity = reader.getBlockEntity(worldPos);
        if (type.isInstance(blockEntity)) {
            op.accept(type.cast(blockEntity));
            return true;
        }
        return false;
    }

    public static void fillCircle(StructureAccessors accessors, ISeedReader reader, Selection<BlockState> selection, Random random, int cx, int y, int cz, int r,  MutableBoundingBox boundingBox) {
        fillCircle(accessors, reader, selection, random, cx, y, cz, r, true, boundingBox);
    }

    public static void fillCircle(StructureAccessors accessors, ISeedReader reader, Selection<BlockState> selection, Random random, int cx, int y, int cz, int r, boolean smooth, MutableBoundingBox boundingBox) {
        fillCircle(accessors.getPlacer(), reader, selection, random, cx, y, cz, r, smooth, boundingBox);
    }

    public static void fillCircle(StructureBlockPlacer placer, ISeedReader reader, Selection<BlockState> selection, Random random, int cx, int y, int cz, int r, boolean smooth, MutableBoundingBox boundingBox) {
        fillCircle(placer, reader, () -> selection.getRandom(random), cx, y, cz, r, smooth, boundingBox);
    }

    public static void fillCircle(StructureAccessors accessors, ISeedReader reader, Supplier<BlockState> stateSup, int cx, int y, int cz, int r,  MutableBoundingBox boundingBox) {
        fillCircle(accessors, reader, stateSup, cx, y, cz, r, true, boundingBox);
    }

    public static void fillCircle(StructureAccessors accessors, ISeedReader reader, Supplier<BlockState> stateSup, int cx, int y, int cz, int r, boolean smooth, MutableBoundingBox boundingBox) {
        fillCircle(accessors.getPlacer(), reader, stateSup, cx, y, cz, r, smooth, boundingBox);
    }

    public static void fillCircle(StructureBlockPlacer placer, ISeedReader reader, Supplier<BlockState> stateSup, int cx, int y, int cz, int r, boolean smooth, MutableBoundingBox boundingBox) {
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z <= r * r) {
//                  Avoid placing "spikes"
                    if (smooth && (x == 0 && Math.abs(z) == r || z == 0 && Math.abs(x) == r)) {
                        continue;
                    }
                    BlockState state = stateSup.get();
                    if (state == null) {
                        throw new NullPointerException("BlockState is null!");
                    }
                    placer.placeBlock(reader, state, cx + x, y, cz + z, boundingBox);
                }
            }
        }
    }

    public static List<BlockPos> getCircleEdges(BlockPos pos, int radius, Direction.Axis normal) {
        return new SortedBresenhamCirclePositionIterator(pos, radius, normal).get();
    }

    public static List<BlockPos> getCircleEdges(int cx, int cy, int cz, int radius, Direction.Axis normal) {
        return new SortedBresenhamCirclePositionIterator(cx, cy, cz, radius, normal).get();
    }

    public static BlockState discussCornerPosition(BlockState edge, int x, int z, int r) {
        if (edge.getBlock() instanceof WallBlock) {
            return discussWallCornerPosition(edge, x, z, r);
        }
        if (edge.getBlock() instanceof FourWayBlock) {
            return discussPaneCornerPosition(edge, x, z, r);
        }
        throw new IllegalArgumentException("Invalid corner: " + edge.getBlock().getRegistryName());
    }

    private static BlockState discussWallCornerPosition(BlockState wallColumn, int x, int z, int r) {
        Preconditions.checkArgument(r > 0, "Radius must be positive");
        if (x == r && z == r) {
            return wallColumn.setValue(WallBlock.NORTH_WALL, WallHeight.LOW).setValue(WallBlock.WEST_WALL, WallHeight.LOW);
        }
        if (x == r && z == -r) {
            return wallColumn.setValue(WallBlock.SOUTH_WALL, WallHeight.LOW).setValue(WallBlock.WEST_WALL, WallHeight.LOW);
        }
        if (x == -r && z == r) {
            return wallColumn.setValue(WallBlock.NORTH_WALL, WallHeight.LOW).setValue(WallBlock.EAST_WALL, WallHeight.LOW);
        }
        if (x == -r && z == -r) {
            return wallColumn.setValue(WallBlock.SOUTH_WALL, WallHeight.LOW).setValue(WallBlock.EAST_WALL, WallHeight.LOW);
        }
        throw new IllegalArgumentException(String.format("Both abs(x)(Provided: %d) and abs(z)(Provided: %d) != r(Provided: %d)", abs(x), abs(z), r));
    }

    private static BlockState discussPaneCornerPosition(BlockState paneColumn, int x, int z, int r) {
        Preconditions.checkArgument(r > 0, "Radius must be positive");
        if (x == r && z == r) {
            return paneColumn.setValue(FourWayBlock.NORTH, true).setValue(FourWayBlock.WEST, true);
        }
        if (x == r && z == -r) {
            return paneColumn.setValue(FourWayBlock.SOUTH, true).setValue(FourWayBlock.WEST, true);
        }
        if (x == -r && z == r) {
            return paneColumn.setValue(FourWayBlock.NORTH, true).setValue(FourWayBlock.EAST, true);
        }
        if (x == -r && z == -r) {
            return paneColumn.setValue(FourWayBlock.SOUTH, true).setValue(FourWayBlock.EAST, true);
        }
        throw new IllegalArgumentException(String.format("Both abs(x)(Provided: %d) and abs(z)(Provided: %d) != r(Provided: %d)", abs(x), abs(z), r));
    }

    public static BlockState discussEdgePosition(BlockState edge, int x, int z, int r) {
        if (edge.getBlock() instanceof WallBlock) {
            return discussWallEdgePosition(edge, x, z, r);
        }
        if (edge.getBlock() instanceof FourWayBlock) {
            return discussPaneEdgePosition(edge, x, z, r);
        }
        throw new IllegalArgumentException("Invalid edge: " + edge.getBlock().getRegistryName());
    }

    private static BlockState discussWallEdgePosition(BlockState wallEdge, int x, int z, int r) {
        Preconditions.checkArgument(r > 0, "Radius must be positive");
        if (abs(x) == r) {
            return wallEdge.setValue(WallBlock.SOUTH_WALL, WallHeight.LOW).setValue(WallBlock.NORTH_WALL, WallHeight.LOW);
        }
        if (abs(z) == r) {
            return wallEdge.setValue(WallBlock.EAST_WALL, WallHeight.LOW).setValue(WallBlock.WEST_WALL, WallHeight.LOW);
        }
        throw new IllegalArgumentException(String.format("Both abs(x)(Provided: %d) and abs(z)(Provided: %d) != r(Provided: %d)", abs(x), abs(z), r));
    }

    private static BlockState discussPaneEdgePosition(BlockState paneEdge, int x, int z, int r) {
        Preconditions.checkArgument(r > 0, "Radius must be positive");
        if (abs(x) == r) {
            return paneEdge.setValue(FourWayBlock.SOUTH, true).setValue(FourWayBlock.NORTH, true);
        }
        if (abs(z) == r) {
            return paneEdge.setValue(FourWayBlock.EAST, true).setValue(FourWayBlock.WEST, true);
        }
        throw new IllegalArgumentException(String.format("Both abs(x)(Provided: %d) and abs(z)(Provided: %d) != r(Provided: %d)", abs(x), abs(z), r));
    }

    @FunctionalInterface
    public interface StructureBlockPlacer {
        void placeBlock(ISeedReader reader, BlockState state, int x, int y, int z, MutableBoundingBox boundingBox);
    }

    public static final class StructureAccessors {
        private final StructureBlockPlacer placer;
        private final IntBinaryOperator worldXGetter;
        private final IntUnaryOperator worldYGetter;
        private final IntBinaryOperator worldZGetter;

        StructureAccessors(StructureBlockPlacer placer, IntBinaryOperator worldXGetter, IntUnaryOperator worldYGetter, IntBinaryOperator worldZGetter) {
            this.placer = placer;
            this.worldXGetter = worldXGetter;
            this.worldYGetter = worldYGetter;
            this.worldZGetter = worldZGetter;
        }

        public StructureBlockPlacer getPlacer() {
            return placer;
        }

        public IntBinaryOperator getWorldXGetter() {
            return worldXGetter;
        }

        public IntUnaryOperator getWorldYGetter() {
            return worldYGetter;
        }

        public IntBinaryOperator getWorldZGetter() {
            return worldZGetter;
        }
    }
}
