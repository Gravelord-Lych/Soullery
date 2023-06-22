package lych.soullery.world.gen.structure.piece;

import lych.soullery.block.ModBlocks;
import lych.soullery.block.entity.InstantSpawnerTileEntity;
import lych.soullery.entity.ModEntities;
import lych.soullery.util.WeightedRandom;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public class FireTemplePiece extends ScatteredStructurePiece {
    private static final Selection<BlockState> BASE_SELECTION = Selections.selection(WeightedRandom.makeItem(Blocks.NETHER_BRICKS.defaultBlockState(), 4), WeightedRandom.makeItem(Blocks.CRACKED_NETHER_BRICKS.defaultBlockState(), 1));
    private static final BlockSelector SELECTOR = WorldUtils.selectorFrom(BASE_SELECTION);
    private static final BlockSelector HOLLOW_SELECTOR = WorldUtils.hollowSelectorFrom(BASE_SELECTION);
    private static final int ROOM_HEIGHT = 8;
    private static final int SOIL_THICKNESS = 2;
    private static final int WIDTH = 27;
    private static final int HEIGHT = 15;
    private boolean placedBlazeSpawner;
    private boolean placedBossSpawner;

    public FireTemplePiece(Random random, int x, int z) {
        super(ModStructurePieces.FIRE_TEMPLE, random, x, 64, z, WIDTH, HEIGHT, WIDTH);
    }

    public FireTemplePiece(TemplateManager manager, CompoundNBT compoundNBT) {
        super(ModStructurePieces.FIRE_TEMPLE, compoundNBT);
        placedBlazeSpawner = compoundNBT.getBoolean("PlacedBlazeSpawner");
        placedBossSpawner = compoundNBT.getBoolean("PlacedBossSpawner");
    }

    @Override
    public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos mbbBottomCenter) {
        if (!updateAverageGroundHeight(reader, boundingBox, 0)) {
            return false;
        }

        for (int i = 0; i < width / 2; i++) {
            generateBox(reader, boundingBox, i, i, i, width - 1 - i, i, width - 1 - i, false, random, SELECTOR);
        }

        WorldUtils.StructureAccessors accessors = WorldUtils.group(this::placeBlock, this::getWorldX, this::getWorldY, this::getWorldZ);
        int maxY = width / 2 - 1;
        int cX = width / 2, cZ = width / 2;

        placeOuterFences(reader, boundingBox, cX, maxY, cZ);

        generateBox(reader, boundingBox, cX - 2, -height + ROOM_HEIGHT + 1, cZ - 2, cX + 2, maxY - 1, cZ + 2, false, random, HOLLOW_SELECTOR);
        generateBox(reader, boundingBox, 0, -height, 0, width - 1, -height + ROOM_HEIGHT + 1, width - 1, false, random, HOLLOW_SELECTOR);
        generateBox(reader, boundingBox, 1, -height + 1, 1, width - 2, -height + SOIL_THICKNESS, width - 2, ModBlocks.PARCHED_SOIL.defaultBlockState(), ModBlocks.PARCHED_SOIL.defaultBlockState(), false);
        if (!placedBossSpawner) {
            placedBossSpawner = WorldUtils.placeBlockEntity(accessors, reader, ModBlocks.INSTANT_SPAWNER.defaultBlockState(), InstantSpawnerTileEntity.class, cX, -height + SOIL_THICKNESS + 2, cZ, boundingBox, spawner -> {
                spawner.setType(ModEntities.ENERGIZED_BLAZE);
                spawner.setRange(InstantSpawnerTileEntity.SHORT_RANGE);
            });
        }

        carve(reader, boundingBox, maxY, cX, cZ);
        if (!placedBlazeSpawner) {
            placedBlazeSpawner = WorldUtils.placeBlockEntity(accessors, reader, Blocks.SPAWNER.defaultBlockState(), MobSpawnerTileEntity.class, cX, maxY + 2, cZ, boundingBox, spawner -> spawner.getSpawner().setEntityId(EntityType.BLAZE));
        }

        return true;
    }

    private void placeOuterFences(ISeedReader reader, MutableBoundingBox boundingBox, int cX, int y, int cZ) {
        for (int x = cX - 2; x <= cX + 2; x++) {
            for (int z = cZ - 2; z <= cZ + 2; z++) {
                if (Math.abs(x - cX) == 2 || Math.abs(z - cZ) == 2) {
                    boolean corner = Math.abs(x - cX) == 2 && Math.abs(z - cZ) == 2;
                    BlockState state;
                    if (corner) {
                        state = WorldUtils.discussCornerPosition(Blocks.NETHER_BRICK_FENCE.defaultBlockState(), x - cX, z - cZ, 2);
                    } else {
                        state = WorldUtils.discussEdgePosition(Blocks.NETHER_BRICK_FENCE.defaultBlockState(), x - cX, z - cZ, 2);
                    }
                    placeBlock(reader, state, x, y, z, boundingBox);
                }
            }
        }
    }

    private void carve(ISeedReader reader, MutableBoundingBox boundingBox, int maxY, int cX, int cZ) {
        generateAirBox(reader, boundingBox, cX - 1, maxY - 2, cZ - 1, cX + 1, maxY + 2, cZ + 1);
        generateAirBox(reader, boundingBox, cX - 1, -height + ROOM_HEIGHT + 1, cZ - 1, cX + 1, -height + ROOM_HEIGHT + 1, cZ + 1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("PlacedBlazeSpawner", placedBlazeSpawner);
        compoundNBT.putBoolean("PlacedBossSpawner", placedBossSpawner);
    }
}
