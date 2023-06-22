package lych.soullery.world.gen.structure.piece;

import com.google.common.collect.Lists;
import lych.soullery.block.ModBlocks;
import lych.soullery.data.loot.ModChestLootTables;
import lych.soullery.entity.ModEntities;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static lych.soullery.util.WeightedRandom.makeItem;

public class SCP0 {
    private static final PieceWeight[] BRIDGE_PIECE_WEIGHTS = new PieceWeight[]{new PieceWeight(Straight.class, 30, 0, true),
            new PieceWeight(Crossing3.class, 10, 10),
            new PieceWeight(Crossing.class, 10, 10),
            new PieceWeight(Stairs.class, 10, 6),
            new PieceWeight(Throne.class, 5, 4),
            new PieceWeight(Entrance.class, 5, 2),
            new PieceWeight(BossRoom.class, 10, 1)};
    private static final PieceWeight[] CASTLE_PIECE_WEIGHTS = new PieceWeight[]{new PieceWeight(Corridor5.class, 25, 0, true),
            new PieceWeight(Crossing2.class, 15, 5),
            new PieceWeight(Corridor2.class, 5, 10),
            new PieceWeight(Corridor.class, 5, 10),
            new PieceWeight(Corridor3.class, 10, 3, true),
            new PieceWeight(Corridor4.class, 7, 2),
            new PieceWeight(SoulStalkRoom.class, 5, 2)};
    private static final int MAX_GEN_DISTANCE = 250;
    private static final int MAX_GEN_DEPTH = 60;
    private static final Selection<BlockState> BRICKS_SELECTION = Selections.selection(makeItem(ModBlocks.PROFOUND_STONE_BRICKS.core().defaultBlockState(), 4), makeItem(ModBlocks.CRACKED_PROFOUND_STONE_BRICKS.core().defaultBlockState(), 1));
    private static final StructurePiece.BlockSelector BRICKS = WorldUtils.selectorFrom(BRICKS_SELECTION);
    private static final Selection<BlockState> STAIRS = Selections.selection(makeItem(ModBlocks.PROFOUND_STONE_BRICKS.stairs().defaultBlockState(), 4), makeItem(ModBlocks.CRACKED_PROFOUND_STONE_BRICKS.stairs().defaultBlockState(), 1));
    private static final BlockState FENCE = Blocks.WARPED_FENCE.defaultBlockState();
    public static final int BOSS_ROOM_SIZE = 39;
    public static final int BOSS_ROOM_HEIGHT = 16;
    public static final int INNER_BOSS_ROOM_SIZE = BOSS_ROOM_SIZE - 4;
    public static final int INNER_BOSS_ROOM_HEIGHT = BOSS_ROOM_HEIGHT - 8;

    @Nullable
    private static BasePiece findAndCreateBridgePieceFactory(PieceWeight weight, List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
        Class<? extends BasePiece> cls = weight.pieceClass;

        if (cls == Straight.class) {
            return Straight.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }
        if (cls == Crossing3.class) {
            return Crossing3.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == Crossing.class) {
            return Crossing.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == Stairs.class) {
            return Stairs.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == Throne.class) {
            return Throne.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == Entrance.class) {
            return Entrance.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }
        if (cls == BossRoom.class) {
            return BossRoom.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }
        if (cls == Corridor5.class) {
            return Corridor5.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == Corridor2.class) {
            return Corridor2.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }
        if (cls == Corridor.class) {
            return Corridor.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }
        if (cls == Corridor3.class) {
            return Corridor3.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == Corridor4.class) {
            return Corridor4.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == Crossing2.class) {
            return Crossing2.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }
        if (cls == SoulStalkRoom.class) {
            return SoulStalkRoom.createPiece(pieces, x0, y0, z0, direction, genDepth);
        }

        return null;
    }

    public static class Corridor extends BasePiece {
        private boolean isNeedingChest;

        public Corridor(int genDepth, Random random, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR_LEFT_TURN, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
            isNeedingChest = random.nextInt(3) == 0;
        }

        public Corridor(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR_LEFT_TURN, compoundNBT);
            isNeedingChest = compoundNBT.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
            super.addAdditionalSaveData(compoundNBT);
            compoundNBT.putBoolean("Chest", isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildLeft((Start) start, pieces, random, 0, 1, true);
        }

        @Nullable
        public static Corridor createPiece(List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -1, 0, 0, 5, 7, 5, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Corridor(genDepth, random, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 0, 0, 4, 1, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);

            generateBox(reader, bb, 4, 2, 0, 4, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 4, 3, 1, 4, 4, 1, fenceNS, fenceNS, false);
            generateBox(reader, bb, 4, 3, 3, 4, 4, 3, fenceNS, fenceNS, false);
            generateBox(reader, bb, 0, 2, 0, 0, 5, 0, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 4, 3, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 1, 3, 4, 1, 4, 4, fenceWE, fenceWE, false);
            generateBox(reader, bb, 3, 3, 4, 3, 4, 4, fenceWE, fenceWE, false);

            if (isNeedingChest && bb.isInside(new BlockPos(getWorldX(3, 3), getWorldY(2), getWorldZ(3, 3)))) {
                isNeedingChest = false;
                createChest(reader, bb, random, 3, 2, 3, ModChestLootTables.SKY_CITY);
            }

            generateBox(reader, bb, 0, 6, 0, 4, 6, 4, false, random, BRICKS);

            return true;
        }
    }

    public static class Corridor2 extends BasePiece {
        private boolean isNeedingChest;

        public Corridor2(int genDepth, Random random, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
            isNeedingChest = random.nextInt(3) == 0;
        }

        public Corridor2(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, compoundNBT);
            isNeedingChest = compoundNBT.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
            super.addAdditionalSaveData(compoundNBT);
            compoundNBT.putBoolean("Chest", isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildRight((Start) start, pieces, random, 0, 1, true);
        }

        @Nullable
        public static Corridor2 createPiece(List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -1, 0, 0, 5, 7, 5, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Corridor2(genDepth, random, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 0, 0, 4, 1, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);

            generateBox(reader, bb, 0, 2, 0, 0, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 3, 1, 0, 4, 1, fenceNS, fenceNS, false);
            generateBox(reader, bb, 0, 3, 3, 0, 4, 3, fenceNS, fenceNS, false);
            generateBox(reader, bb, 4, 2, 0, 4, 5, 0, false, random, BRICKS);
            generateBox(reader, bb, 1, 2, 4, 4, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 1, 3, 4, 1, 4, 4, fenceWE, fenceWE, false);
            generateBox(reader, bb, 3, 3, 4, 3, 4, 4, fenceWE, fenceWE, false);

            if (isNeedingChest && bb.isInside(new BlockPos(getWorldX(1, 3), getWorldY(2), getWorldZ(1, 3)))) {
                isNeedingChest = false;
                createChest(reader, bb, random, 1, 2, 3, ModChestLootTables.SKY_CITY);
            }

            generateBox(reader, bb, 0, 6, 0, 4, 6, 4, false, random, BRICKS);

            return true;
        }
    }

    public static class Corridor3 extends BasePiece {
        public Corridor3(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_CORRIDOR_STAIRS, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Corridor3(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_CORRIDOR_STAIRS, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) start, pieces, random, 1, 0, true);
        }

        @Nullable
        public static Corridor3 createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -1, -7, 0, 5, 14, 10, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Corridor3(genDepth, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            BlockState stairsS = STAIRS.getRandom(random).setValue(StairsBlock.FACING, Direction.SOUTH);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);

            for (int z = 0; z <= 9; ++z) {
                int y0 = Math.max(1, 7 - z);
                int y1 = Math.min(Math.max(y0 + 5, 14 - z), 13);

                generateBox(reader, bb, 0, 0, z, 4, y0, z, false, random, BRICKS);
                generateBox(reader, bb, 1, y0 + 1, z, 3, y1 - 1, z, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

                if (z <= 6) {
                    placeBlock(reader, stairsS, 1, y0 + 1, z, bb);
                    placeBlock(reader, stairsS, 2, y0 + 1, z, bb);
                    placeBlock(reader, stairsS, 3, y0 + 1, z, bb);
                }

                generateBox(reader, bb, 0, y1, z, 4, y1, z, false, random, BRICKS);
                generateBox(reader, bb, 0, y0 + 1, z, 0, y1 - 1, z, false, random, BRICKS);
                generateBox(reader, bb, 4, y0 + 1, z, 4, y1 - 1, z, false, random, BRICKS);

                if ((z & 1) == 0) {
                    generateBox(reader, bb, 0, y0 + 2, z, 0, y0 + 3, z, fenceNS, fenceNS, false);
                    generateBox(reader, bb, 4, y0 + 2, z, 4, y0 + 3, z, fenceNS, fenceNS, false);
                }
            }

            return true;
        }
    }

    public static class Corridor4 extends BasePiece {
        public Corridor4(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_CORRIDOR_T_BALCONY, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Corridor4(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_CORRIDOR_T_BALCONY, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            int h = 1;

            Direction direction = getOrientation();
            if (direction == Direction.WEST || direction == Direction.NORTH) {
                h = 5;
            }

            generateChildLeft((Start) start, pieces, random, 0, h, random.nextInt(8) > 0);
            generateChildRight((Start) start, pieces, random, 0, h, random.nextInt(8) > 0);
        }

        @Nullable
        public static Corridor4 createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -3, 0, 0, 9, 7, 9, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Corridor4(genDepth, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);
            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);

            generateBox(reader, bb, 0, 0, 0, 8, 1, 8, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 6, 0, 8, 6, 5, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 2, 5, 0, false, random, BRICKS);
            generateBox(reader, bb, 6, 2, 0, 8, 5, 0, false, random, BRICKS);
            generateBox(reader, bb, 1, 3, 0, 1, 4, 0, fenceWE, fenceWE, false);
            generateBox(reader, bb, 7, 3, 0, 7, 4, 0, fenceWE, fenceWE, false);
            generateBox(reader, bb, 0, 2, 4, 8, 2, 8, false, random, BRICKS);
            generateBox(reader, bb, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 1, 3, 8, 7, 3, 8, fenceWE, fenceWE, false);
            placeBlock(reader, FENCE.setValue(FenceBlock.EAST, true).setValue(FenceBlock.SOUTH, true), 0, 3, 8, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.SOUTH, true), 8, 3, 8, bb);
            generateBox(reader, bb, 0, 3, 6, 0, 3, 7, fenceNS, fenceNS, false);
            generateBox(reader, bb, 8, 3, 6, 8, 3, 7, fenceNS, fenceNS, false);
            generateBox(reader, bb, 0, 3, 4, 0, 5, 5, false, random, BRICKS);
            generateBox(reader, bb, 8, 3, 4, 8, 5, 5, false, random, BRICKS);
            generateBox(reader, bb, 1, 3, 5, 2, 5, 5, false, random, BRICKS);
            generateBox(reader, bb, 6, 3, 5, 7, 5, 5, false, random, BRICKS);
            generateBox(reader, bb, 1, 4, 5, 1, 5, 5, fenceWE, fenceWE, false);
            generateBox(reader, bb, 7, 4, 5, 7, 5, 5, fenceWE, fenceWE, false);

            return true;
        }
    }

    public static class Corridor5 extends BasePiece {
        public Corridor5(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Corridor5(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) start, pieces, random, 1, 0, true);
        }

        @Nullable
        public static Corridor5 createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -1, 0, 0, 5, 7, 5, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Corridor5(genDepth, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 0, 0, 4, 1, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);
            generateBox(reader, bb, 0, 2, 0, 0, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 4, 2, 0, 4, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 3, 1, 0, 4, 1, fenceNS, fenceNS, false);
            generateBox(reader, bb, 0, 3, 3, 0, 4, 3, fenceNS, fenceNS, false);
            generateBox(reader, bb, 4, 3, 1, 4, 4, 1, fenceNS, fenceNS, false);
            generateBox(reader, bb, 4, 3, 3, 4, 4, 3, fenceNS, fenceNS, false);
            generateBox(reader, bb, 0, 6, 0, 4, 6, 4, false, random, BRICKS);

            return true;
        }
    }

    public static class Crossing extends BasePiece {
        public Crossing(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_ROOM_CROSSING, genDepth);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Crossing(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_ROOM_CROSSING, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) start, pieces, random, 2, 0, false);
            generateChildLeft((Start) start, pieces, random, 0, 2, false);
            generateChildRight((Start) start, pieces, random, 0, 2, false);
        }

        @Nullable
        public static Crossing createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -2, 0, 0, 7, 9, 7, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Crossing(genDepth, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 0, 0, 6, 1, 6, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 2, 0, 1, 6, 0, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 6, 1, 6, 6, false, random, BRICKS);
            generateBox(reader, bb, 5, 2, 0, 6, 6, 0, false, random, BRICKS);
            generateBox(reader, bb, 5, 2, 6, 6, 6, 6, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 0, 6, 1, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 5, 0, 6, 6, false, random, BRICKS);
            generateBox(reader, bb, 6, 2, 0, 6, 6, 1, false, random, BRICKS);
            generateBox(reader, bb, 6, 2, 5, 6, 6, 6, false, random, BRICKS);

            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);

            generateBox(reader, bb, 2, 6, 0, 4, 6, 0, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 0, 4, 5, 0, fenceWE, fenceWE, false);
            generateBox(reader, bb, 2, 6, 6, 4, 6, 6, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 6, 4, 5, 6, fenceWE, fenceWE, false);
            generateBox(reader, bb, 0, 6, 2, 0, 6, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 5, 2, 0, 5, 4, fenceNS, fenceNS, false);
            generateBox(reader, bb, 6, 6, 2, 6, 6, 4, false, random, BRICKS);
            generateBox(reader, bb, 6, 5, 2, 6, 5, 4, fenceNS, fenceNS, false);

            return true;
        }
    }

    public static class Crossing2 extends BasePiece {
        public Crossing2(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR_CROSSING, genDepth);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Crossing2(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_SMALL_CORRIDOR_CROSSING, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) start, pieces, random, 1, 0, true);
            generateChildLeft((Start) start, pieces, random, 0, 1, true);
            generateChildRight((Start) start, pieces, random, 0, 1, true);
        }

        @Nullable
        public static Crossing2 createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -1, 0, 0, 5, 7, 5, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Crossing2(genDepth, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 0, 0, 4, 1, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 2, 0, 0, 5, 0, false, random, BRICKS);
            generateBox(reader, bb, 4, 2, 0, 4, 5, 0, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 4, 0, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 4, 2, 4, 4, 5, 4, false, random, BRICKS);
            generateBox(reader, bb, 0, 6, 0, 4, 6, 4, false, random, BRICKS);

            return true;
        }
    }

    public static class Crossing3 extends BasePiece {
        public Crossing3(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_BRIDGE_CROSSING, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        protected Crossing3(Random random, int x0, int z0) {
            super(ModStructurePieces.SC_BRIDGE_CROSSING, 0);
            setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            boundingBox = new MutableBoundingBox(x0, 64, z0, x0 + 19 - 1, 73, z0 + 19 - 1);
        }

        protected Crossing3(IStructurePieceType type, CompoundNBT compoundNBT) {
            super(type, compoundNBT);
        }

        public Crossing3(TemplateManager manager, CompoundNBT compoundNBT) {
            this(ModStructurePieces.SC_BRIDGE_CROSSING, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) start, pieces, random, 8, 3, false);
            generateChildLeft((Start) start, pieces, random, 3, 8, false);
            generateChildRight((Start) start, pieces, random, 3, 8, false);
        }

        @Nullable
        public static Crossing3 createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -8, -3, 0, 19, 10, 19, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Crossing3(genDepth, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 7, 3, 0, 11, 4, 18, false, random, BRICKS);
            generateBox(reader, bb, 0, 3, 7, 18, 4, 11, false, random, BRICKS);
            generateBox(reader, bb, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 7, 5, 0, 7, 5, 7, false, random, BRICKS);
            generateBox(reader, bb, 7, 5, 11, 7, 5, 18, false, random, BRICKS);
            generateBox(reader, bb, 11, 5, 0, 11, 5, 7, false, random, BRICKS);
            generateBox(reader, bb, 11, 5, 11, 11, 5, 18, false, random, BRICKS);
            generateBox(reader, bb, 0, 5, 7, 7, 5, 7, false, random, BRICKS);
            generateBox(reader, bb, 11, 5, 7, 18, 5, 7, false, random, BRICKS);
            generateBox(reader, bb, 0, 5, 11, 7, 5, 11, false, random, BRICKS);
            generateBox(reader, bb, 11, 5, 11, 18, 5, 11, false, random, BRICKS);
            generateBox(reader, bb, 7, 2, 0, 11, 2, 5, false, random, BRICKS);
            generateBox(reader, bb, 7, 2, 13, 11, 2, 18, false, random, BRICKS);
            generateBox(reader, bb, 7, 0, 0, 11, 1, 3, false, random, BRICKS);
            generateBox(reader, bb, 7, 0, 15, 11, 1, 18, false, random, BRICKS);

            generateBox(reader, bb, 0, 2, 7, 5, 2, 11, false, random, BRICKS);
            generateBox(reader, bb, 13, 2, 7, 18, 2, 11, false, random, BRICKS);
            generateBox(reader, bb, 0, 0, 7, 3, 1, 11, false, random, BRICKS);
            generateBox(reader, bb, 15, 0, 7, 18, 1, 11, false, random, BRICKS);

            return true;
        }
    }

    public static class End extends BasePiece {
        private final int selfSeed;

        public End(int genDepth, Random random, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_BRIDGE_END_FILLER, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
            this.selfSeed = random.nextInt();
        }

        public End(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_BRIDGE_END_FILLER, compoundNBT);
            this.selfSeed = compoundNBT.getInt("Seed");
        }

        @Nullable
        public static End createPiece(List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -1, -3, 0, 5, 10, 8, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new End(genDepth, random, boundingBox, direction) : null;
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
            super.addAdditionalSaveData(compoundNBT);
            compoundNBT.putInt("Seed", selfSeed);
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            Random selfRandom = new Random(selfSeed);

            for (int x = 0; x <= 4; ++x) {
                for (int y = 3; y <= 4; ++y) {
                    int randomZIn = selfRandom.nextInt(8);
                    generateBox(reader, bb, x, y, 0, x, y, randomZIn, false, random, BRICKS);
                }
            }

            int randomZ = selfRandom.nextInt(8);
            generateBox(reader, bb, 0, 5, 0, 0, 5, randomZ, false, random, BRICKS);
            randomZ = selfRandom.nextInt(8);
            generateBox(reader, bb, 4, 5, 0, 4, 5, randomZ, false, random, BRICKS);

            for (int x = 0; x <= 4; ++x) {
                int randomZIn = selfRandom.nextInt(5);
                generateBox(reader, bb, x, 2, 0, x, 2, randomZIn, false, random, BRICKS);
            }

            for (int x = 0; x <= 4; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    int randomZIn = selfRandom.nextInt(3);
                    generateBox(reader, bb, x, y, 0, x, y, randomZIn, false, random, BRICKS);
                }
            }

            return true;
        }
    }

    public static class Entrance extends BasePiece {
        public Entrance(int genDepth, Random random, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_ENTRANCE, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Entrance(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_ENTRANCE, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) start, pieces, random, 5, 3, true);
        }

        @Nullable
        public static Entrance createPiece(List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -5, -3, 0, 13, 14, 13, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new Entrance(genDepth, random, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 3, 0, 12, 4, 12, false, random, BRICKS);
            generateBox(reader, bb, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 5, 0, 1, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 11, 5, 0, 12, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 11, 4, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 8, 5, 11, 10, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 5, 9, 11, 7, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 0, 4, 12, 1, false, random, BRICKS);
            generateBox(reader, bb, 8, 5, 0, 10, 12, 1, false, random, BRICKS);
            generateBox(reader, bb, 5, 9, 0, 7, 12, 1, false, random, BRICKS);
            generateBox(reader, bb, 2, 11, 2, 10, 12, 10, false, random, BRICKS);
            generateBox(reader, bb, 5, 8, 0, 7, 8, 0, FENCE, FENCE, false);

            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);

            for (int x = 1; x <= 11; x += 2) {
                generateBox(reader, bb, x, 10, 0, x, 11, 0, fenceWE, fenceWE, false);
                generateBox(reader, bb, x, 10, 12, x, 11, 12, fenceWE, fenceWE, false);
                generateBox(reader, bb, 0, 10, x, 0, 11, x, fenceNS, fenceNS, false);
                generateBox(reader, bb, 12, 10, x, 12, 11, x, fenceNS, fenceNS, false);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), x, 13, 0, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), x, 13, 12, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), 0, 13, x, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), 12, 13, x, bb);

                if (x != 11) {
                    placeBlock(reader, fenceWE, x + 1, 13, 0, bb);
                    placeBlock(reader, fenceWE, x + 1, 13, 12, bb);
                    placeBlock(reader, fenceNS, 0, 13, x + 1, bb);
                    placeBlock(reader, fenceNS, 12, 13, x + 1, bb);
                }
            }

            placeBlock(reader, FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.EAST, true), 0, 13, 0, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.SOUTH, true).setValue(FenceBlock.EAST, true), 0, 13, 12, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.SOUTH, true).setValue(FenceBlock.WEST, true), 12, 13, 12, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.WEST, true), 12, 13, 0, bb);

            for (int z = 3; z <= 9; z += 2) {
                generateBox(reader, bb, 1, 7, z, 1, 8, z, fenceNS.setValue(FenceBlock.WEST, true), fenceNS.setValue(FenceBlock.WEST, true), false);
                generateBox(reader, bb, 11, 7, z, 11, 8, z, fenceNS.setValue(FenceBlock.EAST, true), fenceNS.setValue(FenceBlock.EAST, true), false);
            }

            generateBox(reader, bb, 4, 2, 0, 8, 2, 12, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 4, 12, 2, 8, false, random, BRICKS);
            generateBox(reader, bb, 4, 0, 0, 8, 1, 3, false, random, BRICKS);
            generateBox(reader, bb, 4, 0, 9, 8, 1, 12, false, random, BRICKS);
            generateBox(reader, bb, 0, 0, 4, 3, 1, 8, false, random, BRICKS);
            generateBox(reader, bb, 9, 0, 4, 12, 1, 8, false, random, BRICKS);

            for (int x = 4; x <= 8; ++x) {
                for (int z = 0; z <= 2; ++z) {
                    fillColumnDown(reader, BRICKS_SELECTION.getRandom(random), x, -1, z, bb);
                    fillColumnDown(reader, BRICKS_SELECTION.getRandom(random), x, -1, 12 - z, bb);
                }
            }

            for (int x = 0; x <= 2; ++x) {
                for (int z = 4; z <= 8; ++z) {
                    fillColumnDown(reader, BRICKS_SELECTION.getRandom(random), x, -1, z, bb);
                    fillColumnDown(reader, BRICKS_SELECTION.getRandom(random), 12 - x, -1, z, bb);
                }
            }

            generateBox(reader, bb, 5, 5, 5, 7, 5, 7, false, random, BRICKS);
            generateBox(reader, bb, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            placeBlock(reader, BRICKS_SELECTION.getRandom(random), 6, 4 /* 0 -> 4 */, 6, bb);
            // addition
            placeBlock(reader, BRICKS_SELECTION.getRandom(random), 6, 3, 6, bb);
            placeBlock(reader, BRICKS_SELECTION.getRandom(random), 6, 2, 6, bb);
            // -----

            placeBlock(reader, ModBlocks.SOUL_LAVA_FLUID_BLOCK.defaultBlockState(), 6, 5, 6, bb);

            BlockPos lavaPos = new BlockPos(getWorldX(6, 6), getWorldY(5), getWorldZ(6, 6));

            if (bb.isInside(lavaPos)) {
                reader.getLiquidTicks().scheduleTick(lavaPos, Fluids.LAVA, 0);
            }

            return true;
        }
    }

    public static class BossRoom extends BasePiece {
        private static final int MAX_XZ = BOSS_ROOM_SIZE - 1;
        private static final int SIZE_IN = BOSS_ROOM_SIZE - 2;
        private static final int MAX_Y = BOSS_ROOM_HEIGHT - 1;

        public BossRoom(int genDepth, Random random, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_BOSS_ROOM, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public BossRoom(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_BOSS_ROOM, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            ((Start) start).hasBossRoom = true;
        }

        @Nullable
        public static BossRoom createPiece(List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -SIZE_IN / 2, -3, 0, BOSS_ROOM_SIZE, BOSS_ROOM_HEIGHT, BOSS_ROOM_SIZE, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new BossRoom(genDepth, random, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 3, 0, MAX_XZ, 4, MAX_XZ, false, random, BRICKS);
            generateBox(reader, bb, 0, 5, 0, MAX_XZ, MAX_Y, MAX_XZ, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 5, 0, 1, MAX_Y - 1, MAX_XZ, false, random, BRICKS);
            generateBox(reader, bb, MAX_XZ - 1, 5, 0, MAX_XZ, MAX_Y - 1, MAX_XZ, false, random, BRICKS);

            int ct = MAX_XZ / 2;
            generateBox(reader, bb, 2, 5, MAX_XZ - 1, MAX_XZ - 2, MAX_Y - 1, MAX_XZ, false, random, BRICKS);
//            generateBox(reader, bb, ct + 2, 5, MAX_XZ - 1, MAX_XZ - 2, MAX_Y - 1, MAX_XZ, false, random, BRICKS);
//            generateBox(reader, bb, ct - 1, MAX_Y - 4, MAX_XZ - 1, ct + 1, MAX_Y - 1, MAX_XZ, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 0, ct - 2, MAX_Y - 1, 1, false, random, BRICKS);
            generateBox(reader, bb, ct + 2, 5, 0, MAX_XZ - 2, MAX_Y - 1, 1, false, random, BRICKS);
            generateBox(reader, bb, ct - 1, MAX_Y - 4, 0, ct + 1, MAX_Y - 1, 1, false, random, BRICKS);
            generateBox(reader, bb, 2, MAX_Y - 2, 2, MAX_XZ - 2, MAX_Y - 1, MAX_XZ - 2, false, random, BRICKS);
            generateBox(reader, bb, ct - 1, MAX_Y - 5, 0, ct + 1, MAX_Y - 5, 0, FENCE, FENCE, false);

            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);
            int fenceY0 = MAX_Y - 3;
            int fenceY1 = MAX_Y - 2;

            for (int x = 1; x <= MAX_XZ - 1; x += 2) {
                generateBox(reader, bb, x, fenceY0, 0, x, fenceY1, 0, fenceWE, fenceWE, false);
                generateBox(reader, bb, x, fenceY0, MAX_XZ, x, fenceY1, MAX_XZ, fenceWE, fenceWE, false);
                generateBox(reader, bb, 0, fenceY0, x, 0, fenceY1, x, fenceNS, fenceNS, false);
                generateBox(reader, bb, MAX_XZ, fenceY0, x, MAX_XZ, fenceY1, x, fenceNS, fenceNS, false);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), x, MAX_Y, 0, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), x, MAX_Y, MAX_XZ, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), 0, MAX_Y, x, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), MAX_XZ, MAX_Y, x, bb);

                if (x != MAX_XZ - 1) {
                    placeBlock(reader, fenceWE, x + 1, MAX_Y, 0, bb);
                    placeBlock(reader, fenceWE, x + 1, MAX_Y, MAX_XZ, bb);
                    placeBlock(reader, fenceNS, 0, MAX_Y, x + 1, bb);
                    placeBlock(reader, fenceNS, MAX_XZ, MAX_Y, x + 1, bb);
                }
            }

            placeBlock(reader, FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.EAST, true), 0, MAX_Y, 0, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.SOUTH, true).setValue(FenceBlock.EAST, true), 0, MAX_Y, MAX_XZ, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.SOUTH, true).setValue(FenceBlock.WEST, true), MAX_XZ, MAX_Y, MAX_XZ, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.WEST, true), MAX_XZ, MAX_Y, 0, bb);

            fenceY0 -= 3;
            fenceY1 -= 3;
            for (int z = 3; z <= MAX_XZ - 3; z += 2) {
                generateBox(reader, bb, 1, fenceY0, z, 1, fenceY1, z, fenceNS.setValue(FenceBlock.WEST, true), fenceNS.setValue(FenceBlock.WEST, true), false);
                generateBox(reader, bb, MAX_XZ - 1, fenceY0, z, MAX_XZ - 1, fenceY1, z, fenceNS.setValue(FenceBlock.EAST, true), fenceNS.setValue(FenceBlock.EAST, true), false);
            }

//            int s1 = 4;
//            int s0 = 3;
//            generateBox(reader, bb, s1, 2, 0, MAX_XZ - s1, 2, MAX_XZ, false, random, BRICKS);
//            generateBox(reader, bb, 0, 2, s1, MAX_XZ, 2, MAX_XZ - s1, false, random, BRICKS);
//            generateBox(reader, bb, s1, 0, 0, MAX_XZ - s1, 1, s0, false, random, BRICKS);
//            generateBox(reader, bb, s1, 0, MAX_XZ - s0, MAX_XZ - s1, 1, MAX_XZ, false, random, BRICKS);
//            generateBox(reader, bb, 0, 0, s1, s0, 1, MAX_XZ - s1, false, random, BRICKS);
//            generateBox(reader, bb, MAX_XZ - s0, 0, s1, MAX_XZ, 1, MAX_XZ - s1, false, random, BRICKS);

            int altarY = 5;
            generateBox(reader, bb, ct - 1, altarY, ct - 1, ct + 1, altarY, ct + 1, false, random, BRICKS);
            placeBlock(reader, Blocks.AIR.defaultBlockState(), ct, altarY, ct, bb);

            placeBlock(reader, ModBlocks.SOUL_LAVA_FLUID_BLOCK.defaultBlockState(), ct, altarY, ct, bb);
            BlockPos lavaPos = new BlockPos(getWorldX(ct, ct), getWorldY(altarY), getWorldZ(ct, ct));

            if (bb.isInside(lavaPos)) {
                reader.getLiquidTicks().scheduleTick(lavaPos, Fluids.LAVA, 0);
            }

            return true;
        }
    }

    public static class SoulStalkRoom extends BasePiece {
        public SoulStalkRoom(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_CASTLE_STALK_ROOM, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public SoulStalkRoom(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_CASTLE_STALK_ROOM, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) start, pieces, random, 5, 3, true);
            generateChildForward((Start) start, pieces, random, 5, 11, true);
        }

        @Nullable
        public static SCP0.SoulStalkRoom createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox boundingBox = MutableBoundingBox.orientBox(x0, y0, z0, -5, -3, 0, 13, 14, 13, direction);
            return isOkBox(boundingBox) && StructurePiece.findCollisionPiece(pieces, boundingBox) == null ? new SoulStalkRoom(genDepth, boundingBox, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 3, 0, 12, 4, 12, false, random, BRICKS);
            generateBox(reader, bb, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 5, 0, 1, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 11, 5, 0, 12, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 11, 4, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 8, 5, 11, 10, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 5, 9, 11, 7, 12, 12, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 0, 4, 12, 1, false, random, BRICKS);
            generateBox(reader, bb, 8, 5, 0, 10, 12, 1, false, random, BRICKS);
            generateBox(reader, bb, 5, 9, 0, 7, 12, 1, false, random, BRICKS);
            generateBox(reader, bb, 2, 11, 2, 10, 12, 10, false, random, BRICKS);

            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);
            BlockState fenceNSW = fenceNS.setValue(FenceBlock.WEST, true);
            BlockState fenceNSE = fenceNS.setValue(FenceBlock.EAST, true);

            for (int x = 1; x <= 11; x += 2) {
                generateBox(reader, bb, x, 10, 0, x, 11, 0, fenceWE, fenceWE, false);
                generateBox(reader, bb, x, 10, 12, x, 11, 12, fenceWE, fenceWE, false);
                generateBox(reader, bb, 0, 10, x, 0, 11, x, fenceNS, fenceNS, false);
                generateBox(reader, bb, 12, 10, x, 12, 11, x, fenceNS, fenceNS, false);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), x, 13, 0, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), x, 13, 12, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), 0, 13, x, bb);
                placeBlock(reader, BRICKS_SELECTION.getRandom(random), 12, 13, x, bb);
                if (x != 11) {
                    placeBlock(reader, fenceWE, x + 1, 13, 0, bb);
                    placeBlock(reader, fenceWE, x + 1, 13, 12, bb);
                    placeBlock(reader, fenceNS, 0, 13, x + 1, bb);
                    placeBlock(reader, fenceNS, 12, 13, x + 1, bb);
                }
            }

            placeBlock(reader, FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.EAST, true), 0, 13, 0, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.SOUTH, true).setValue(FenceBlock.EAST, true), 0, 13, 12, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.SOUTH, true).setValue(FenceBlock.WEST, true), 12, 13, 12, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.WEST, true), 12, 13, 0, bb);

            for (int z = 3; z <= 9; z += 2) {
                generateBox(reader, bb, 1, 7, z, 1, 8, z, fenceNSW, fenceNSW, false);
                generateBox(reader, bb, 11, 7, z, 11, 8, z, fenceNSE, fenceNSE, false);
            }

            BlockState stairsN = STAIRS.getRandom(random).setValue(StairsBlock.FACING, Direction.NORTH);

            for (int h = 0; h <= 6; ++h) {
                int z = h + 4;

                for (int x = 5; x <= 7; ++x) {
                    placeBlock(reader, stairsN, x, 5 + h, z, bb);
                }

                if (z >= 5 && z <= 8) {
                    generateBox(reader, bb, 5, 5, z, 7, h + 4, z, false, random, BRICKS);
                } else if (z >= 9) {
                    generateBox(reader, bb, 5, 8, z, 7, h + 4, z, false, random, BRICKS);
                }

                if (h >= 1) {
                    generateBox(reader, bb, 5, 6 + h, z, 7, 9 + h, z, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
                }
            }

            for (int x = 5; x <= 7; ++x) {
                placeBlock(reader, stairsN, x, 12, 11, bb);
            }

            generateBox(reader, bb, 5, 6, 7, 5, 7, 7, fenceNSE, fenceNSE, false);
            generateBox(reader, bb, 7, 6, 7, 7, 7, 7, fenceNSW, fenceNSW, false);
            generateBox(reader, bb, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 2, 5, 2, 3, 5, 3, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 9, 3, 5, 10, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 4, 2, 5, 8, false, random, BRICKS);
            generateBox(reader, bb, 9, 5, 2, 10, 5, 3, false, random, BRICKS);
            generateBox(reader, bb, 9, 5, 9, 10, 5, 10, false, random, BRICKS);
            generateBox(reader, bb, 10, 5, 4, 10, 5, 8, false, random, BRICKS);

            BlockState stairsE = stairsN.setValue(StairsBlock.FACING, Direction.EAST);
            BlockState stairsW = stairsN.setValue(StairsBlock.FACING, Direction.WEST);

            placeBlock(reader, stairsW, 4, 5, 2, bb);
            placeBlock(reader, stairsW, 4, 5, 3, bb);
            placeBlock(reader, stairsW, 4, 5, 9, bb);
            placeBlock(reader, stairsW, 4, 5, 10, bb);
            placeBlock(reader, stairsE, 8, 5, 2, bb);
            placeBlock(reader, stairsE, 8, 5, 3, bb);
            placeBlock(reader, stairsE, 8, 5, 9, bb);
            placeBlock(reader, stairsE, 8, 5, 10, bb);

            generateBox(reader, bb, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            generateBox(reader, bb, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            generateBox(reader, bb, 3, 5, 4, 4, 5, 8, ModBlocks.SOUL_WART.defaultBlockState(), ModBlocks.SOUL_WART.defaultBlockState(), false);
            generateBox(reader, bb, 8, 5, 4, 9, 5, 8, ModBlocks.SOUL_WART.defaultBlockState(), ModBlocks.SOUL_WART.defaultBlockState(), false);
            generateBox(reader, bb, 4, 2, 0, 8, 2, 12, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 4, 12, 2, 8, false, random, BRICKS);
            generateBox(reader, bb, 4, 0, 0, 8, 1, 3, false, random, BRICKS);
            generateBox(reader, bb, 4, 0, 9, 8, 1, 12, false, random, BRICKS);
            generateBox(reader, bb, 0, 0, 4, 3, 1, 8, false, random, BRICKS);
            generateBox(reader, bb, 9, 0, 4, 12, 1, 8, false, random, BRICKS);

            return true;
        }
    }

    public static class Stairs extends BasePiece {
        public Stairs(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_STAIRS_ROOM, genDepth);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Stairs(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_STAIRS_ROOM, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            generateChildRight((Start) start, pieces, random, 6, 2, false);
        }

        @Nullable
        public static Stairs createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox bb = MutableBoundingBox.orientBox(x0, y0, z0, -2, 0, 0, 7, 11, 7, direction);
            return isOkBox(bb) && StructurePiece.findCollisionPiece(pieces, bb) == null ? new Stairs(genDepth, bb, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 0, 0, 6, 1, 6, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 2, 0, 1, 8, 0, false, random, BRICKS);
            generateBox(reader, bb, 5, 2, 0, 6, 8, 0, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 1, 0, 8, 6, false, random, BRICKS);
            generateBox(reader, bb, 6, 2, 1, 6, 8, 6, false, random, BRICKS);
            generateBox(reader, bb, 1, 2, 6, 5, 8, 6, false, random, BRICKS);
            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);
            generateBox(reader, bb, 0, 3, 2, 0, 5, 4, fenceNS, fenceNS, false);
            generateBox(reader, bb, 6, 3, 2, 6, 5, 2, fenceNS, fenceNS, false);
            generateBox(reader, bb, 6, 3, 4, 6, 5, 4, fenceNS, fenceNS, false);
            placeBlock(reader, BRICKS_SELECTION.getRandom(random), 5, 2, 5, bb);
            generateBox(reader, bb, 4, 2, 5, 4, 3, 5, false, random, BRICKS);
            generateBox(reader, bb, 3, 2, 5, 3, 4, 5, false, random, BRICKS);
            generateBox(reader, bb, 2, 2, 5, 2, 5, 5, false, random, BRICKS);
            generateBox(reader, bb, 1, 2, 5, 1, 6, 5, false, random, BRICKS);
            generateBox(reader, bb, 1, 7, 1, 5, 7, 4, false, random, BRICKS);
            generateBox(reader, bb, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 2, 6, 0, 4, 8, 0, false, random, BRICKS);
            generateBox(reader, bb, 2, 5, 0, 4, 5, 0, fenceWE, fenceWE, false);

            return true;
        }
    }

    public static class Start extends Crossing3 {
        public boolean hasBossRoom;
        public PieceWeight previousPiece;
        public List<PieceWeight> availableBridgePieces;
        public List<PieceWeight> availableCastlePieces;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public Start(Random random, int x, int z) {
            super(random, x, z);

            availableBridgePieces = Lists.newArrayList();
            for (PieceWeight weight : BRIDGE_PIECE_WEIGHTS) {
                weight.placeCount = 0;
                availableBridgePieces.add(weight);
            }

            availableCastlePieces = Lists.newArrayList();
            for (PieceWeight weight : CASTLE_PIECE_WEIGHTS) {
                weight.placeCount = 0;
                availableCastlePieces.add(weight);
            }
        }

        public Start(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_START, compoundNBT);
        }
    }

    public static class Straight extends BasePiece {
        public Straight(int genDepth, Random random, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_BRIDGE_STRAIGHT, genDepth);
            setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Straight(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_BRIDGE_STRAIGHT, compoundNBT);
        }

        @Override
        public void addChildren(StructurePiece piece, List<StructurePiece> pieces, Random random) {
            generateChildForward((Start) piece, pieces, random, 1, 3, false);
        }

        @Nullable
        public static Straight createPiece(List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox bb = MutableBoundingBox.orientBox(x0, y0, z0, -1, -3, 0, 5, 10, 19, direction);
            return isOkBox(bb) && StructurePiece.findCollisionPiece(pieces, bb) == null ? new Straight(genDepth, random, bb, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 3, 0, 4, 4, 18, false, random, BRICKS);
            generateBox(reader, bb, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 0, 5, 0, 0, 5, 18, false, random, BRICKS);
            generateBox(reader, bb, 4, 5, 0, 4, 5, 18, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 0, 4, 2, 5, false, random, BRICKS);
            generateBox(reader, bb, 0, 2, 13, 4, 2, 18, false, random, BRICKS);
            generateBox(reader, bb, 0, 0, 0, 4, 1, 3, false, random, BRICKS);
            generateBox(reader, bb, 0, 0, 15, 4, 1, 18, false, random, BRICKS);

            BlockState fence = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);
            BlockState fenceE = fence.setValue(FenceBlock.EAST, true);
            BlockState fenceW = fence.setValue(FenceBlock.WEST, true);
            generateBox(reader, bb, 0, 1, 1, 0, 4, 1, fenceE, fenceE, false);
            generateBox(reader, bb, 0, 3, 4, 0, 4, 4, fenceE, fenceE, false);
            generateBox(reader, bb, 0, 3, 14, 0, 4, 14, fenceE, fenceE, false);
            generateBox(reader, bb, 0, 1, 17, 0, 4, 17, fenceE, fenceE, false);
            generateBox(reader, bb, 4, 1, 1, 4, 4, 1, fenceW, fenceW, false);
            generateBox(reader, bb, 4, 3, 4, 4, 4, 4, fenceW, fenceW, false);
            generateBox(reader, bb, 4, 3, 14, 4, 4, 14, fenceW, fenceW, false);
            generateBox(reader, bb, 4, 1, 17, 4, 4, 17, fenceW, fenceW, false);

            return true;
        }
    }

    public static class Throne extends BasePiece {
        private boolean hasPlacedSpawner;

        public Throne(int genDepth, MutableBoundingBox boundingBox, Direction direction) {
            super(ModStructurePieces.SC_MONSTER_THRONE, genDepth);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public Throne(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.SC_MONSTER_THRONE, compoundNBT);
            this.hasPlacedSpawner = compoundNBT.getBoolean("Mob");
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
            super.addAdditionalSaveData(compoundNBT);
            compoundNBT.putBoolean("Mob", this.hasPlacedSpawner);
        }

        @Nullable
        public static Throne createPiece(List<StructurePiece> pieces, int x0, int y0, int z0, Direction direction, int genDepth) {
            MutableBoundingBox bb = MutableBoundingBox.orientBox(x0, y0, z0, -2, 0, 0, 7, 8, 9, direction);
            return isOkBox(bb) && StructurePiece.findCollisionPiece(pieces, bb) == null ? new Throne(genDepth, bb, direction) : null;
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
            generateBox(reader, bb, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            generateBox(reader, bb, 1, 0, 0, 5, 1, 7, false, random, BRICKS);
            generateBox(reader, bb, 1, 2, 1, 5, 2, 7, false, random, BRICKS);
            generateBox(reader, bb, 1, 3, 2, 5, 3, 7, false, random, BRICKS);
            generateBox(reader, bb, 1, 4, 3, 5, 4, 7, false, random, BRICKS);
            generateBox(reader, bb, 1, 2, 0, 1, 4, 2, false, random, BRICKS);
            generateBox(reader, bb, 5, 2, 0, 5, 4, 2, false, random, BRICKS);
            generateBox(reader, bb, 1, 5, 2, 1, 5, 3, false, random, BRICKS);
            generateBox(reader, bb, 5, 5, 2, 5, 5, 3, false, random, BRICKS);
            generateBox(reader, bb, 0, 5, 3, 0, 5, 8, false, random, BRICKS);
            generateBox(reader, bb, 6, 5, 3, 6, 5, 8, false, random, BRICKS);
            generateBox(reader, bb, 1, 5, 8, 5, 5, 8, false, random, BRICKS);
            BlockState fenceWE = FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.EAST, true);
            BlockState fenceNS = FENCE.setValue(FenceBlock.NORTH, true).setValue(FenceBlock.SOUTH, true);
            placeBlock(reader, FENCE.setValue(FenceBlock.WEST, true), 1, 6, 3, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.EAST, true), 5, 6, 3, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.EAST, true).setValue(FenceBlock.NORTH, true), 0, 6, 3, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.NORTH, true), 6, 6, 3, bb);
            generateBox(reader, bb, 0, 6, 4, 0, 6, 7, fenceNS, fenceNS, false);
            generateBox(reader, bb, 6, 6, 4, 6, 6, 7, fenceNS, fenceNS, false);
            placeBlock(reader, FENCE.setValue(FenceBlock.EAST, true).setValue(FenceBlock.SOUTH, true), 0, 6, 8, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.WEST, true).setValue(FenceBlock.SOUTH, true), 6, 6, 8, bb);
            generateBox(reader, bb, 1, 6, 8, 5, 6, 8, fenceWE, fenceWE, false);
            placeBlock(reader, FENCE.setValue(FenceBlock.EAST, true), 1, 7, 8, bb);
            generateBox(reader, bb, 2, 7, 8, 4, 7, 8, fenceWE, fenceWE, false);
            placeBlock(reader, FENCE.setValue(FenceBlock.WEST, true), 5, 7, 8, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.EAST, true), 2, 8, 8, bb);
            placeBlock(reader, fenceWE, 3, 8, 8, bb);
            placeBlock(reader, FENCE.setValue(FenceBlock.WEST, true), 4, 8, 8, bb);

            if (!hasPlacedSpawner) {
                BlockPos pos = new BlockPos(getWorldX(3, 5), getWorldY(5), getWorldZ(3, 5));
                if (bb.isInside(pos)) {
                    hasPlacedSpawner = true;
                    reader.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
                    TileEntity spawner = reader.getBlockEntity(pos);
                    if (spawner instanceof MobSpawnerTileEntity) {
                        ((MobSpawnerTileEntity)spawner).getSpawner().setEntityId(ModEntities.SOUL_SKELETON);
                    }
                }
            }

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
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

        private int updatePieceWeight(List<PieceWeight> weights) {
            boolean hasPlaceable = false;
            int totalWeight = 0;

            for (PieceWeight weight : weights) {
                if (weight.maxPlaceCount > 0 && weight.placeCount < weight.maxPlaceCount) {
                    hasPlaceable = true;
                }

                totalWeight += weight.weight;
            }

            return hasPlaceable ? totalWeight : -1;
        }

        @Nullable
        private BasePiece generatePiece(Start start, List<PieceWeight> weights, List<StructurePiece> pieces, Random random, int x0, int y0, int z0, Direction direction, int genDepth) {
            int totalWeight = updatePieceWeight(weights);
            boolean placeable = totalWeight > 0 && genDepth <= MAX_GEN_DEPTH;
            int placeCount = 0;

            while (placeCount < 5 && placeable) {
                placeCount++;
                int randomInt = random.nextInt(totalWeight);

                for (PieceWeight weight : weights) {
                    randomInt -= weight.weight;
                    if (randomInt < 0) {
                        if (!weight.doPlace(genDepth) || weight == start.previousPiece && !weight.allowInRow) {
                            break;
                        }

                        BasePiece piece = findAndCreateBridgePieceFactory(weight, pieces, random, x0, y0, z0, direction, genDepth);
                        if (piece != null) {
                            weight.placeCount++;
                            start.previousPiece = weight;
                            if (!weight.isValid()) {
                                weights.remove(weight);
                            }
                            return piece;
                        }
                    }
                }
            }

            return End.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }

        private StructurePiece generateAndAddPiece(Start start, List<StructurePiece> pieces, Random random, int x0, int y0, int z0, @Nullable Direction direction, int genDepth, boolean castle) {
            if (Math.abs(x0 - start.getBoundingBox().x0) <= MAX_GEN_DISTANCE && Math.abs(z0 - start.getBoundingBox().z0) <= MAX_GEN_DISTANCE) {
                List<PieceWeight> availablePieces = start.availableBridgePieces;
                if (castle) {
                    availablePieces = start.availableCastlePieces;
                }

                StructurePiece piece = generatePiece(start, availablePieces, pieces, random, x0, y0, z0, direction, genDepth + 1);
                if (piece != null) {
                    pieces.add(piece);
                    start.pendingChildren.add(piece);
                }

                return piece;
            }
            return End.createPiece(pieces, random, x0, y0, z0, direction, genDepth);
        }

        @Nullable
        protected StructurePiece generateChildForward(Start start, List<StructurePiece> pieces, Random random, int depth, int height, boolean castle) {
            Direction direction = getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z0 - 1, direction, getGenDepth(), castle);
                    case SOUTH:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z1 + 1, direction, getGenDepth(), castle);
                    case WEST:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 - 1, boundingBox.y0 + height, boundingBox.z0 + depth, direction, getGenDepth(), castle);
                    case EAST:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x1 + 1, boundingBox.y0 + height, boundingBox.z0 + depth, direction, getGenDepth(), castle);
                }
            }

            return null;
        }

        @Nullable
        protected StructurePiece generateChildLeft(Start start, List<StructurePiece> pieces, Random random, int height, int depth, boolean castle) {
            Direction direction = getOrientation();

            if (direction != null) {
                switch (direction) {
                    case NORTH:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 - 1, boundingBox.y0 + height, boundingBox.z0 + depth, Direction.WEST, getGenDepth(), castle);
                    case SOUTH:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 - 1, boundingBox.y0 + height, boundingBox.z0 + depth, Direction.WEST, getGenDepth(), castle);
                    case WEST:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z0 - 1, Direction.NORTH, getGenDepth(), castle);
                    case EAST:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z0 - 1, Direction.NORTH, getGenDepth(), castle);
                }
            }

            return null;
        }

        @Nullable
        protected StructurePiece generateChildRight(Start start, List<StructurePiece> pieces, Random random, int height, int depth, boolean castle) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x1 + 1, boundingBox.y0 + height, boundingBox.z0 + depth, Direction.EAST, getGenDepth(), castle);
                    case SOUTH:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x1 + 1, boundingBox.y0 + height, boundingBox.z0 + depth, Direction.EAST, getGenDepth(), castle);
                    case WEST:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z1 + 1, Direction.SOUTH, getGenDepth(), castle);
                    case EAST:
                        return generateAndAddPiece(start, pieces, random, boundingBox.x0 + depth, boundingBox.y0 + height, boundingBox.z1 + 1, Direction.SOUTH, getGenDepth(), castle);
                }
            }

            return null;
        }

        protected static boolean isOkBox(@Nullable MutableBoundingBox boundingBox) {
            return boundingBox != null && boundingBox.y0 > 10;
        }
    }

    public static class PieceWeight {
        public final Class<? extends BasePiece> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;
        public final boolean allowInRow;

        public PieceWeight(Class<? extends BasePiece> type, int weight, int maxPlaceCount, boolean allowInRow) {
            this.pieceClass = type;
            this.weight = weight;
            this.maxPlaceCount = maxPlaceCount;
            this.allowInRow = allowInRow;
        }

        public PieceWeight(Class<? extends BasePiece> type, int weight, int maxPlaceCount) {
            this(type, weight, maxPlaceCount, false);
        }

        public boolean doPlace(int genDepth) {
            return maxPlaceCount == 0 || placeCount < maxPlaceCount;
        }

        public boolean isValid() {
            return maxPlaceCount == 0 || placeCount < maxPlaceCount;
        }
    }
}
