package lych.soullery.util;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import lych.soullery.util.selection.Selection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Supplier;

public class StairsIterator implements IterableIterator<Pair<BlockPos, BlockState>> {
    private final BlockPos start;
    private final Direction direction;
    private final Direction sideDirection;
    private final int width;
    private final int height;
    private final Supplier<StairsBlock> block;
    private int l;
    private int w;
    private int h;
    private boolean rv;
    private Pair<BlockPos, BlockState> next;

    public StairsIterator(BlockPos start, Direction direction, Direction sideDirection, int width, int height, Random random, Selection<Block> blockSelection) {
        this(start, direction, sideDirection, width, height, () -> {
            Block randomBlock = blockSelection.getRandom(random);
            Preconditions.checkState(randomBlock instanceof StairsBlock, "StairsBlockSelection provided an invalid block: " + randomBlock.getRegistryName());
            return (StairsBlock) randomBlock;
        });
    }

    public StairsIterator(BlockPos start, Direction direction, Direction sideDirection, int width, int height, Supplier<StairsBlock> block) {
        this.start = start;
        Preconditions.checkArgument(Direction.Plane.HORIZONTAL.test(direction) && Direction.Plane.HORIZONTAL.test(sideDirection), "Directions can only be horizontal");
        Preconditions.checkArgument(direction.getAxis() != sideDirection.getAxis(), "Direction must not be opposite to SideDirection");
        this.direction = direction;
        this.sideDirection = sideDirection;
        this.width = width;
        this.height = height;
        this.block = block;
    }

    public void iterateAndSetBlock(ISeedReader reader, MutableBoundingBox boundingBox, WorldUtils.StructureAccessors accessors) {
        iterateAndSetBlock(reader, boundingBox, accessors.getPlacer());
    }

    public void iterateAndSetBlock(ISeedReader reader, MutableBoundingBox boundingBox, WorldUtils.StructureBlockPlacer placer) {
        for (Pair<BlockPos, BlockState> pair : this) {
            BlockPos pos = pair.getFirst();
            placer.placeBlock(reader, pair.getSecond(), pos.getX(), pos.getY(), pos.getZ(), boundingBox);
        }
    }

    @Nullable
    private Pair<BlockPos, BlockState> computeNext() {
        BlockPos nextPos = start.offset(sideDirection.getStepX() * w, h, sideDirection.getStepZ() * w).offset(direction.getStepX() * l, 0, direction.getStepZ() * l);
        BlockState nextState = block.get().defaultBlockState();
        if (w >= width) {
            w = 0;
            if (rv) {
                h++;
            } else {
                l++;
            }
            rv = !rv;
            if (h >= height) {
                return null;
            }
            nextPos = start.offset(sideDirection.getStepX() * w, h, sideDirection.getStepZ() * w).offset(direction.getStepX() * l, 0, direction.getStepZ() * l);
        } else {
            w++;
        }
        if (direction.getAxis() == Direction.Axis.Z) {
            nextState = nextState.setValue(StairsBlock.FACING, rv ? direction : direction.getOpposite()).setValue(StairsBlock.HALF, rv ? Half.TOP : Half.BOTTOM);
        } else {
            nextState = nextState.setValue(StairsBlock.FACING, rv ? direction.getOpposite() : direction).setValue(StairsBlock.HALF, rv ? Half.TOP : Half.BOTTOM);
        }
        return Pair.of(nextPos, nextState);
    }

    @Override
    public boolean hasNext() {
        Pair<BlockPos, BlockState> next = computeNext();
        if (next == null) {
            return false;
        }
        this.next = next;
        return true;
    }

    public BlockPos end() {
        return start.offset(sideDirection.getStepX() * w, h, sideDirection.getStepZ() * w).offset(direction.getStepX() * l, 0, direction.getStepZ() * l);
    }

    public BlockPos newStart() {
        return end().below();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Pair<BlockPos, BlockState> next() {
        return next;
    }
}
