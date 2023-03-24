package lych.soullery.world.gen.structure.piece;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;

import java.util.Random;

public abstract class StructureHelper extends StructurePiece {
    protected StructureHelper(IStructurePieceType type, int genDepth) {
        super(type, genDepth);
    }

    protected StructureHelper(IStructurePieceType type, CompoundNBT compoundNBT) {
        super(type, compoundNBT);
    }

    @Override
    public abstract boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos mbbBottomCenter);

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

    @Override
    protected void generateBox(ISeedReader reader, MutableBoundingBox boundingBox, int fx, int fy, int fz, int tx, int ty, int tz, boolean onlyReplaceNonAir, Random random, BlockSelector selector) {
        super.generateBox(reader, boundingBox, fx, fy, fz, tx, ty, tz, onlyReplaceNonAir, random, selector);
    }

    @Override
    protected void generateBox(ISeedReader reader, MutableBoundingBox boundingBox, int fx, int fy, int fz, int tx, int ty, int tz, BlockState edgeBlock, BlockState centerBlock, boolean onlyReplaceNonAir) {
        super.generateBox(reader, boundingBox, fx, fy, fz, tx, ty, tz, edgeBlock, centerBlock, onlyReplaceNonAir);
    }
}
