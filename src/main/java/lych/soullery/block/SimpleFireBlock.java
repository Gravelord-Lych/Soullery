package lych.soullery.block;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

@SuppressWarnings("deprecation")
public class SimpleFireBlock extends AbstractFireBlock implements IExtendedFireBlock {
    private final ITag<Block> tag;

    public SimpleFireBlock(Properties properties, ITag<Block> tag) {
//      The fireDamage is unimportant now because SoulCraft uses a new fire system
        super(properties, 1);
        this.tag = tag;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos) {
        return canSurvive(state, world, pos) ? defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader reader, BlockPos pos) {
        return canSurviveOnBlock(reader.getBlockState(pos.below()).getBlock());
    }

    @Override
    public boolean canSurviveOnBlock(Block block) {
        return block.is(tag);
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }
}
