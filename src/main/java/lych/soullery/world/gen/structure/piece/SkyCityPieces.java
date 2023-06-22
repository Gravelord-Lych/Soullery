package lych.soullery.world.gen.structure.piece;

import com.google.common.collect.Lists;
import lych.soullery.block.ModBlocks;
import lych.soullery.util.StairsIterator;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static lych.soullery.util.WeightedRandom.makeItem;

public final class SkyCityPieces {
    private static final Selection<Block> STAIRS = Selections.selection(makeItem(ModBlocks.PROFOUND_STONE_BRICKS.stairs(), 4), makeItem(ModBlocks.CRACKED_PROFOUND_STONE_BRICKS.stairs(), 1));
    private static final Selection<BlockState> BRICKS = Selections.selection(makeItem(ModBlocks.PROFOUND_STONE_BRICKS.core().defaultBlockState(), 4), makeItem(ModBlocks.CRACKED_PROFOUND_STONE_BRICKS.core().defaultBlockState(), 1));

    private SkyCityPieces() {}

    public static class MainBuilding extends BasePiece {
        private static final int SIZE = 11;
        private static final int HEIGHT = 70;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public MainBuilding(Random random, int x, int z) {
            super(ModStructurePieces.SC_MAIN_BUILDING, 1);
            boundingBox = new MutableBoundingBox(x, 64, z, x + SIZE, 64 + HEIGHT, z + SIZE);
            setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
        }

        public MainBuilding(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_MAIN_BUILDING, compoundNBT);
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, boundingBox, 0, 0, 0, SIZE - 1, HEIGHT - 1, SIZE - 1, false, random, WorldUtils.hollowSelectorFrom(BRICKS));
            return true;
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

        @Override
        public void addChildren(StructurePiece parent, List<StructurePiece> pieces, Random random) {
            generateChildForward(this, pieces, random, SIZE, HEIGHT);
        }
    }

    public static class Walkway extends BasePiece {
        protected Walkway(int genDepth, MutableBoundingBox bb, Direction direction) {
            super(ModStructurePieces.SC_WALKWAY, genDepth);
            this.boundingBox = bb;
            setOrientation(direction);
        }

        public Walkway(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_WALKWAY, compoundNBT);
        }

        @Nullable
        public static Walkway createPiece(List<StructurePiece> pieces, Random random, int depth, int height, int width, Direction direction, int genDepth) {
            MutableBoundingBox bb = MutableBoundingBox.orientBox(depth, height, width, 0, 0, 0, 4, 1, 10, direction);
            return isOkBox(bb) && StructurePiece.findCollisionPiece(pieces, bb) == null ? new Walkway(genDepth, bb, direction) : null;
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            placeWalkway(blockPos, reader, random, getOrientation(), getOrientation().getCounterClockWise(), 10, 4, boundingBox);
            return true;
        }
    }

    public static abstract class BasePiece extends StructurePiece {
        protected BasePiece(IStructurePieceType type, int genDepth) {
            super(type, genDepth);
        }

        public BasePiece(IStructurePieceType type, CompoundNBT compoundNBT) {
            super(type, compoundNBT);
        }

        @Override
        public abstract boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox p_230383_5_, ChunkPos chunkPos, BlockPos blockPos);

        @Override
        protected abstract void addAdditionalSaveData(CompoundNBT compoundNBT);

        @NotNull
        protected BlockPos placeWalkway(BlockPos start, ISeedReader reader, Random random, Direction direction, Direction sideDirection, int len, int width, MutableBoundingBox boundingBox) {
            BlockPos to = start.relative(direction, len).relative(sideDirection, width);
            generateBox(reader, boundingBox, start.getX(), start.getY(), start.getZ(), to.getX(), to.getY(), to.getZ(), false, random, WorldUtils.selectorFrom(BRICKS));
            return start.relative(direction, len);
        }

        @NotNull
        protected BlockPos placeStairs(BlockPos start, ISeedReader reader, Random random, Direction direction, Direction sideDirection, int len, int width, MutableBoundingBox boundingBox) {
            StairsIterator iterator = new StairsIterator(start, direction, sideDirection, width, len, random, STAIRS);
            iterator.iterateAndSetBlock(reader, boundingBox, this::placeBlock);
            return iterator.newStart();
        }

        protected static boolean isOkBox(@Nullable MutableBoundingBox bb) {
            return bb != null && bb.y0 > 100;
        }

        @Nullable
        protected StructurePiece generateChildForward(MainBuilding parent, List<StructurePiece> pieces, Random random, int depth, int height) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch(direction) {
                    case NORTH:
                        return generateAndAddPiece(parent, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z0 - 1, direction, getGenDepth());
                    case SOUTH:
                        return generateAndAddPiece(parent, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z1 + 1, direction, getGenDepth());
                    case WEST:
                        return generateAndAddPiece(parent, pieces, random, boundingBox.x0 - 1, boundingBox.y0 + height, boundingBox.z0 + depth, direction, getGenDepth());
                    case EAST:
                        return generateAndAddPiece(parent, pieces, random, boundingBox.x1 + 1, boundingBox.y0 + height, boundingBox.z0 + depth, direction, getGenDepth());
                }
            }
            return null;
        }

        private StructurePiece generateAndAddPiece(MainBuilding parent, List<StructurePiece> pieces, Random random, int x0, int y0, int z0, @javax.annotation.Nullable Direction direction, int genDepth) {
            if (Math.abs(x0 - parent.getBoundingBox().x0) <= 112 && Math.abs(z0 - parent.getBoundingBox().z0) <= 112) {
                StructurePiece piece = generatePiece(parent, pieces, random, x0, y0, z0, direction, genDepth + 1);
                if (piece != null) {
                    pieces.add(piece);
                    parent.pendingChildren.add(piece);
                }
                return piece;
            } else {
                return FortressPieces.End.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
            }
        }

        @Nullable
        private StructurePiece generatePiece(MainBuilding parent, List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            return Walkway.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }
    }
}
