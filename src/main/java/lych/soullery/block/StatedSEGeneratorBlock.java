package lych.soullery.block;

import lych.soullery.block.entity.AbstractSEGeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

import java.util.function.Supplier;

public class StatedSEGeneratorBlock extends SEGeneratorBlock {
    public StatedSEGeneratorBlock(Properties properties, Supplier<? extends AbstractSEGeneratorTileEntity> supplier) {
        super(properties, supplier);
        registerDefaultState(stateDefinition.any().setValue(ModBlockStateProperties.IS_GENERATING_SE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModBlockStateProperties.IS_GENERATING_SE);
    }
}
