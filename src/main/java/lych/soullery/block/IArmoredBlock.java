package lych.soullery.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface IArmoredBlock {
    @Nullable
    BlockState getChild(World level, BlockPos pos, BlockState parent, @Nullable TileEntity oldBlockEntity);

    void restoreFrom(World level, BlockPos pos, BlockState parent, BlockState child, @Nullable TileEntity oldBlockEntity);

    default boolean enableForMobs() {
        return false;
    }

    default boolean enableForExplosion() {
        return true;
    }
}
