package lych.soullery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IArmoredBlockEntity<C extends TileEntity> {
    void handleChild(World level, BlockPos pos, BlockState parentState, BlockState childState, C child);

    static void reload(TileEntity src, TileEntity dest, BlockState destState) {
        dest.load(destState, src.save(new CompoundNBT()));
    }
}
