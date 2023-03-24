package lych.soullery.block.entity;

import lych.soullery.block.ModBlockNames;
import lych.soullery.gui.container.SolarSEGeneratorContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SolarSEGeneratorTileEntity extends AbstractSEGeneratorTileEntity {
    private static final int GEN_FREQ = 5;

    public SolarSEGeneratorTileEntity(TileEntityType<? extends AbstractSEGeneratorTileEntity> type, int tier) {
        super(type, tier);
    }

    @Override
    protected boolean shouldGenerateSE() {
        return level != null && level.isDay() && level.canSeeSky(getBlockPos().above());
    }

    @Override
    protected int getGenFrequency() {
        return GEN_FREQ;
    }

    @Override
    protected Container createMenu(int sycID, BlockPos worldPosition, PlayerInventory inventory, World level, IIntArray seProgress, IWorldPosCallable callable) {
        return new SolarSEGeneratorContainer(sycID, worldPosition, inventory, level, seProgress, callable);
    }

    @Override
    protected String getName() {
        return ModBlockNames.SOLAR_SEGEN;
    }
}
