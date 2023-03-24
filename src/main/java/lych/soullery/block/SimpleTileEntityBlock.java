package lych.soullery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SimpleTileEntityBlock extends Block {
    private final Supplier<? extends TileEntity> supplier;

    public SimpleTileEntityBlock(Properties properties, Supplier<? extends TileEntity> supplier) {
        super(properties);
        this.supplier = supplier;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return supplier.get();
    }
}

