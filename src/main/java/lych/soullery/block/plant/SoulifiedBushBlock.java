package lych.soullery.block.plant;

import com.google.common.collect.ImmutableList;
import lych.soullery.tag.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulifiedBushBlock extends DeadBushBlock {
    public static final int BURN_TIME = 100;

    public SoulifiedBushBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader reader, BlockPos pos) {
        return state.is(ModBlockTags.SOULIFIED_BUSH_PLACEABLE_BLOCKS);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return ModPlantTypes.SOUL;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 60;
    }

    @NotNull
    @Override
    public List<ItemStack> onSheared(@Nullable PlayerEntity player, @NotNull ItemStack item, World world, BlockPos pos, int fortune) {
        return ImmutableList.of(new ItemStack(this));
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 100;
    }
}
