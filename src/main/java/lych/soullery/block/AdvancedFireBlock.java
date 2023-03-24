package lych.soullery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class AdvancedFireBlock extends FireBlock implements IExtendedFireBlock {
    private final Block block;
    private final ITag<Block> tag;

    public AdvancedFireBlock(Properties properties, Block block, ITag<Block> tag) {
        super(properties);
        this.block = block;
        this.tag = tag;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader reader, BlockPos pos) {
        return super.canSurvive(state, reader, pos) && canSurviveOnBlock(reader.getBlockState(pos.below()).getBlock());
    }

    @Override
    public boolean canSurviveOnBlock(Block block) {
        return block.is(tag);
    }

    @Override
    protected BlockState getStateWithAge(IWorld world, BlockPos pos, int age) {
        BlockState state = getState(world, pos);
        return state.is(block) ? state.setValue(AGE, age) : state;
    }
}
