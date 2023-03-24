package lych.soullery.block.entity;

import com.google.common.collect.ImmutableSet;
import lych.soullery.block.ModBlockNames;
import lych.soullery.fluid.ModFluids;
import lych.soullery.gui.container.HeatSEGeneratorContainer;
import lych.soullery.tag.ModFluidTags;
import lych.soullery.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeatSEGeneratorTileEntity extends AbstractSEGeneratorTileEntity {
    private static final ImmutableSet<ITag<Block>> AVAILABLE_BLOCK_TAGS;
    private static final ImmutableSet<Block> AVAILABLE_BLOCKS;
    private static final ImmutableSet<ITag<Fluid>> AVAILABLE_FLUID_TAGS;
    private static final ImmutableSet<Fluid> AVAILABLE_FLUIDS;
    private static final int GEN_FREQ = 4;

    public HeatSEGeneratorTileEntity(TileEntityType<? extends AbstractSEGeneratorTileEntity> type, int tier) {
        super(type, tier);
    }

    @Override
    protected int getGenFrequency() {
        return GEN_FREQ;
    }

    @Override
    protected boolean shouldGenerateSE() {
        if (level == null) {
            return false;
        }
        for (BlockPos pos : WorldUtils.getNearbyBlocks(worldPosition)) {
            BlockState block = level.getBlockState(pos);
            if (AVAILABLE_BLOCK_TAGS.stream().anyMatch(block::is) || AVAILABLE_BLOCKS.stream().anyMatch(block::is)) {
                return true;
            }
            FluidState fluid = level.getFluidState(pos);
            if (AVAILABLE_FLUID_TAGS.stream().anyMatch(fluid::is) || AVAILABLE_FLUIDS.stream().anyMatch(fluid.getType()::equals)) {
                return true;
            }
            if (isHighTemperatureFluid(fluid.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Container createMenu(int sycID, BlockPos worldPosition, PlayerInventory inventory, World level, IIntArray seProgress, IWorldPosCallable callable) {
        return new HeatSEGeneratorContainer(sycID, worldPosition, inventory, level, seProgress, callable);
    }

    @Override
    protected String getName() {
        return ModBlockNames.HEAT_SEGEN;
    }

    private static boolean isHighTemperatureFluid(Fluid fluid) {
        return fluid.getAttributes().getTemperature() >= 1300;
    }

    static {
        AVAILABLE_BLOCK_TAGS = ImmutableSet.<ITag<Block>>builder()
                .add(BlockTags.CAMPFIRES)
                .add(BlockTags.FIRE)
                .build();
        AVAILABLE_BLOCKS = ImmutableSet.<Block>builder()
                .add(Blocks.CAMPFIRE)
                .add(Blocks.FIRE)
                .add(Blocks.LAVA)
                .add(Blocks.MAGMA_BLOCK)
                .add(Blocks.SOUL_CAMPFIRE)
                .add(Blocks.SOUL_FIRE)
                .build();
        AVAILABLE_FLUID_TAGS = ImmutableSet.<ITag<Fluid>>builder()
                .add(FluidTags.LAVA)
                .add(ModFluidTags.SOUL_LAVA)
                .build();
        AVAILABLE_FLUIDS = ImmutableSet.<Fluid>builder()
                .add(Fluids.LAVA)
                .add(Fluids.FLOWING_LAVA)
                .add(ModFluids.SOUL_LAVA)
                .add(ModFluids.FLOWING_SOUL_LAVA)
                .build();
    }
}
