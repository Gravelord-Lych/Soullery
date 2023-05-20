package lych.soullery.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class InstantSpawnerBlock extends SimpleTileEntityBlock {
    public InstantSpawnerBlock(Properties properties, Supplier<? extends TileEntity> supplier) {
        super(properties, supplier);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }
}
