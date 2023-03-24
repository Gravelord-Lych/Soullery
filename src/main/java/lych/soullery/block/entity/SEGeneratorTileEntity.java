package lych.soullery.block.entity;

import lych.soullery.block.ModBlockNames;
import lych.soullery.gui.container.SEGeneratorContainer;
import lych.soullery.world.gen.dimension.ModDimensions;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SEGeneratorTileEntity extends AbstractSEGeneratorTileEntity {
    private static final int GEN_FREQ = 7;
    private static final int GEN_FREQ_SL = 4;

    public SEGeneratorTileEntity(TileEntityType<? extends AbstractSEGeneratorTileEntity> type, int tier) {
        super(type, tier);
    }

    @Override
    protected int getGenFrequency() {
        boolean inSoulLand = level != null && level.dimension() == ModDimensions.SOUL_LAND;
        return inSoulLand ? GEN_FREQ_SL : GEN_FREQ;
    }

    @Override
    protected boolean shouldGenerateSE() {
        return true;
    }

    @Override
    protected Container createMenu(int sycID, BlockPos worldPosition, PlayerInventory inventory, World level, IIntArray seProgress, IWorldPosCallable callable) {
        return new SEGeneratorContainer(sycID, worldPosition, inventory, level, seProgress, callable);
    }

    @Override
    protected String getName() {
        return ModBlockNames.SEGEN;
    }
}
