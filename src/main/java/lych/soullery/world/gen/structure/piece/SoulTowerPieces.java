package lych.soullery.world.gen.structure.piece;

import lych.soullery.block.ModBlocks;
import lych.soullery.block.entity.InstantSpawnerTileEntity;
import lych.soullery.entity.ModEntities;
import lych.soullery.util.BresenhamCirclePositionIterator;
import lych.soullery.util.WorldUtils;
import lych.soullery.world.gen.config.SoulTowerConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public final class SoulTowerPieces {
    private static final int INNER_RADIUS = 12;
    private static final int STEP_RADIUS = 4;
    private static final int STAIR_RADIUS = INNER_RADIUS + STEP_RADIUS;
    private static final int RADIUS = INNER_RADIUS + STEP_RADIUS * 2;
    private static final int ROOM_ADDITIONAL_RADIUS = 8;
    private static final int ROOM_RADIUS = RADIUS + ROOM_ADDITIONAL_RADIUS;
    private static final int ROOM_HEIGHT = 9;
    private static final int OUTER_ROOM_HEIGHT = ROOM_HEIGHT + 2;

    private SoulTowerPieces() {}

    public static final class MainTower extends ScatteredStructurePiece {
        private final SoulTowerConfig config;

        public MainTower(Random random, int x, int z, SoulTowerConfig config) {
            super(ModStructurePieces.ST_MAIN_TOWER, random, x, 64, z, RADIUS * 2, config.getRandomHeight(random), RADIUS * 2);
            this.config = config;
        }

        public MainTower(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.ST_MAIN_TOWER, compoundNBT);
            config = new SoulTowerConfig(compoundNBT.getCompound("TowerConfig"));
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            if (!updateAverageGroundHeight(reader, boundingBox, 0)) {
                return false;
            }

            WorldUtils.StructureAccessors accessors = WorldUtils.group(this::placeBlock, this::getWorldX, this::getWorldY, this::getWorldZ);
            List<BlockPos> circleSurrounder = WorldUtils.getCircleEdges(0, 0, 0, STAIR_RADIUS, Direction.Axis.Y);
            int i = 0;

            for (int y = 0; y <= height; y++) {
                BlockPos pos = circleSurrounder.get(i);
                i += (y & 1) == 0 ? 2 : 1;
                if (i >= circleSurrounder.size()) {
                    i = 0;
                }
                WorldUtils.fillCircle(accessors, reader, config.getStairBlocks(), random, r(pos.getX()), y, r(pos.getZ()), STEP_RADIUS, boundingBox);
                WorldUtils.fillCircle(accessors, reader, config.getOuterTowerBlocks(), random, r(0), y, r(0), INNER_RADIUS + 1, boundingBox);
                WorldUtils.fillCircle(accessors, reader, config.getInnerTowerBlocks(), random, r(0), y, r(0), INNER_RADIUS - 2, boundingBox);
            }

            return true;
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
            super.addAdditionalSaveData(compoundNBT);
            compoundNBT.put("TowerConfig", config.save());
        }

        @Override
        public void addChildren(StructurePiece start, List<StructurePiece> pieces, Random random) {
            TopRoom room = new TopRoom(random, boundingBox.x0 - ROOM_ADDITIONAL_RADIUS, boundingBox.y1 + 1, boundingBox.z0 - ROOM_ADDITIONAL_RADIUS, config);
            pieces.add(room);
        }

        private static int r(int p) {
            return p + RADIUS;
        }
    }

    public static final class TopRoom extends ScatteredStructurePiece {
        private final SoulTowerConfig config;
        private boolean placedSpawner;

        public TopRoom(Random random, int x, int y, int z, SoulTowerConfig config) {
            super(ModStructurePieces.ST_TOP_ROOM, random, x, y, z, (ROOM_RADIUS + 1) * 2, OUTER_ROOM_HEIGHT, (ROOM_RADIUS + 1) * 2);
            this.config = config;
            genDepth++;
        }

        public TopRoom(TemplateManager manager, CompoundNBT compoundNBT) {
            super(ModStructurePieces.ST_TOP_ROOM, compoundNBT);
            config = new SoulTowerConfig(compoundNBT.getCompound("TowerConfig"));
            placedSpawner = compoundNBT.getBoolean("PlacedSpawner");
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
            super.addAdditionalSaveData(compoundNBT);
            compoundNBT.put("TowerConfig", config.save());
            compoundNBT.putBoolean("PlacedSpawner", placedSpawner);
        }

        @Override
        public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            WorldUtils.StructureAccessors accessors = WorldUtils.group(this::placeBlock, this::getWorldX, this::getWorldY, this::getWorldZ);
            for (int y = 0; y < OUTER_ROOM_HEIGHT; y++) {
                if (y == 0 || y == OUTER_ROOM_HEIGHT - 1) {
                    WorldUtils.fillCircle(accessors, reader, config.getInnerTowerBlocks(), random, r(0), y, r(0), ROOM_RADIUS, boundingBox);
                    WorldUtils.fillCircle(accessors, reader, config.getSlabBlocks(), random, r(0), y, r(0), ROOM_RADIUS - 3, boundingBox);
                } else {
                    BresenhamCirclePositionIterator itr = new BresenhamCirclePositionIterator(r(0), r(0), y, ROOM_RADIUS - 2, Direction.Axis.Y);
                    for (BlockPos pos : itr) {
                        placeBlock(reader, config.getGlassBlocks().getRandom(random), pos.getX(), pos.getY(), pos.getZ(), boundingBox);
                    }
                    if (y == ROOM_HEIGHT / 2) {
                        if (!placedSpawner) {
                            placedSpawner = WorldUtils.placeBlockEntity(accessors, reader, ModBlocks.INSTANT_SPAWNER.defaultBlockState(), InstantSpawnerTileEntity.class, r(0), y, r(0), boundingBox, spawner -> {
                                spawner.setType(ModEntities.SOUL_SKELETON_KING);
                                spawner.setRange(ROOM_RADIUS);
                                spawner.setVerticalRange(ROOM_HEIGHT / 2);
                                spawner.setRestrictRadiusMultiplier(1);
                            });
                        }
                    }
                }
            }
            return true;
        }

        private static int r(int p) {
            return p + ROOM_RADIUS;
        }
    }
}
