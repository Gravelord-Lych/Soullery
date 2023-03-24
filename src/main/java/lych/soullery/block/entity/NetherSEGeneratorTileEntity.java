package lych.soullery.block.entity;

import lych.soullery.block.ModBlockNames;
import lych.soullery.gui.container.NetherSEGeneratorContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherSEGeneratorTileEntity extends AbstractSEGeneratorTileEntity {
    private static final int GEN_FREQ = 5;

    public NetherSEGeneratorTileEntity(TileEntityType<? extends AbstractSEGeneratorTileEntity> type, int tier) {
        super(type, tier);
    }

    @Override
    protected int getGenFrequency() {
        return GEN_FREQ;
    }

    @Override
    protected boolean shouldGenerateSE() {
        return level != null && level.dimension() == World.NETHER;
    }

    @Override
    protected Container createMenu(int sycID, BlockPos worldPosition, PlayerInventory inventory, World level, IIntArray seProgress, IWorldPosCallable callable) {
        return new NetherSEGeneratorContainer(sycID, worldPosition, inventory, level, seProgress, callable);
    }

    @Override
    protected String getName() {
        return ModBlockNames.NETHER_SEGEN;
    }
}
