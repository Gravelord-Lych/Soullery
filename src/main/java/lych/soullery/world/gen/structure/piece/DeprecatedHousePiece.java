package lych.soullery.world.gen.structure.piece;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.List;
import java.util.Random;

public class DeprecatedHousePiece extends StructureHelper {
    private final List<BlockState> fenceBlocks;
    private final List<BlockState> slabBlocks;
    private final List<BlockState> stairsBlocks;
    private final List<BlockState> wallBlocks;
    private final List<BlockState> windowBlocks;

    public DeprecatedHousePiece(IStructurePieceType type, int genDepth, List<BlockState> fenceBlocks, List<BlockState> slabBlocks, List<BlockState> stairsBlocks, List<BlockState> wallBlocks, List<BlockState> windowBlocks) {
        super(type, genDepth);
        this.fenceBlocks = fenceBlocks;
        this.slabBlocks = slabBlocks;
        this.stairsBlocks = stairsBlocks;
        this.wallBlocks = wallBlocks;
        this.windowBlocks = windowBlocks;
    }

    public DeprecatedHousePiece(IStructurePieceType type, CompoundNBT compoundNBT, List<BlockState> fenceBlocks, List<BlockState> slabBlocks, List<BlockState> stairsBlocks, List<BlockState> wallBlocks, List<BlockState> windowBlocks) {
        super(type, compoundNBT);
        this.fenceBlocks = fenceBlocks;
        this.slabBlocks = slabBlocks;
        this.stairsBlocks = stairsBlocks;
        this.wallBlocks = wallBlocks;
        this.windowBlocks = windowBlocks;
    }

    @Override
    public boolean postProcess(ISeedReader reader, StructureManager manager, ChunkGenerator generator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos mbbBottomCenter) {
//        generateBox(reader, boundingBox, 0, 0, 0, 9, 0, 9, block);
        return false;
    }

    @SuppressWarnings("deprecation")
    protected boolean blockBelowIsSolid(IBlockReader reader, int rx, int ry, int rz, MutableBoundingBox mbb) {
        return !getBlock(reader, rx, ry - 1, rz, mbb).isAir();
    }
}
