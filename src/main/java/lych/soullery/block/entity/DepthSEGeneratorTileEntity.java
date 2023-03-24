package lych.soullery.block.entity;

import lych.soullery.block.ModBlockNames;
import lych.soullery.gui.container.DepthSEGeneratorContainer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DepthSEGeneratorTileEntity extends AbstractSEGeneratorTileEntity {
    private static final int GEN_FREQ = 4;

    public DepthSEGeneratorTileEntity(TileEntityType<? extends AbstractSEGeneratorTileEntity> type, int tier) {
        super(type, tier);
    }

    @Override
    protected int getGenFrequency() {
        return GEN_FREQ;
    }

    @Override
    protected boolean shouldGenerateSE() {
        if (level == null || worldPosition.getY() > 16) {
            return false;
        }
        return level.getBlockState(worldPosition.below()).is(Blocks.BEDROCK);
    }

    @Override
    protected Container createMenu(int sycID, BlockPos worldPosition, PlayerInventory inventory, World level, IIntArray seProgress, IWorldPosCallable callable) {
        return new DepthSEGeneratorContainer(sycID, worldPosition, inventory, level, seProgress, callable);
    }

    @Override
    protected String getName() {
        return ModBlockNames.DEPTH_SEGEN;
    }
}
