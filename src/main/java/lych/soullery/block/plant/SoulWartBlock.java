package lych.soullery.block.plant;

import lych.soullery.block.ModBlocks;
import lych.soullery.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

public class SoulWartBlock extends NetherWartBlock {
    public SoulWartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.SOUL_WART);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return ModPlantTypes.SOUL;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader reader, BlockPos pos) {
        return super.mayPlaceOn(state, reader, pos) || state.is(ModBlocks.REFINED_SOUL_SAND);
    }
}
