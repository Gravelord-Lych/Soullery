package lych.soullery.fluid;

import lych.soullery.block.ModBlocks;
import lych.soullery.client.particle.ModParticles;
import lych.soullery.item.ModItems;
import lych.soullery.tag.ModFluidTags;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class SoulLavaFluid extends ForgeFlowingFluid {
    protected SoulLavaFluid(Properties properties) {
        super(properties);
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_SOUL_LAVA;
    }

    @Override
    public Fluid getSource() {
        return ModFluids.SOUL_LAVA;
    }

    @Override
    public Item getBucket() {
        return ModItems.SOUL_LAVA_BUCKET;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(World world, BlockPos pos, FluidState state, Random random) {
        BlockPos blockpos = pos.above();
        if (world.getBlockState(blockpos).isAir() && !world.getBlockState(blockpos).isSolidRender(world, blockpos)) {
            if (random.nextInt(100) == 0) {
                double x = (double) pos.getX() + random.nextDouble();
                double y = (double) pos.getY() + 1;
                double z = (double) pos.getZ() + random.nextDouble();
                world.addParticle(ModParticles.SOUL_LAVA, x, y, z, 0, 0, 0);
                world.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0) {
                world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }
    }

    @Override
    public void randomTick(World world, BlockPos pos, FluidState state, Random random) {
        if (world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            int rand = random.nextInt(3);
            if (rand > 0) {
                BlockPos finalPos = pos;

                for (int i = 0; i < rand; ++i) {
                    finalPos = finalPos.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    if (!world.isLoaded(finalPos)) {
                        return;
                    }
                    BlockState finalState = world.getBlockState(finalPos);
                    if (finalState.isAir()) {
                        if (hasFlammableNeighbours(world, finalPos)) {
                            world.setBlockAndUpdate(finalPos, ForgeEventFactory.fireFluidPlaceBlockEvent(world, finalPos, pos, AbstractFireBlock.getState(world, pos)));
                            return;
                        }
                    } else if (finalState.getMaterial().blocksMotion()) {
                        return;
                    }
                }
            } else {
                for (int i = 0; i < 3; ++i) {
                    BlockPos finalPos = pos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                    if (!world.isLoaded(finalPos)) {
                        return;
                    }

                    if (world.isEmptyBlock(finalPos.above()) && isFlammable(world, finalPos, Direction.UP)) {
                        world.setBlockAndUpdate(finalPos.above(), ForgeEventFactory.fireFluidPlaceBlockEvent(world, finalPos.above(), pos, AbstractFireBlock.getState(world, pos)));
                    }
                }
            }
        }
    }

    private boolean hasFlammableNeighbours(IWorldReader reader, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (isFlammable(reader, pos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    private boolean isFlammable(IWorldReader world, BlockPos pos, Direction face) {
        return (pos.getY() < 0 || pos.getY() >= 256 || world.hasChunkAt(pos)) && world.getBlockState(pos).isFlammable(world, pos, face);
    }

    @Override
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public IParticleData getDripParticle() {
        return ModParticles.DRIPPING_SOUL_LAVA;
    }

    @Override
    protected void beforeDestroyingBlock(IWorld world, BlockPos pos, BlockState state) {
        fizz(world, pos);
    }

    @Override
    public int getSlopeFindDistance(IWorldReader reader) {
        return reader.dimensionType().ultraWarm() ? 4 : 2;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.SOUL_LAVA_FLUID_BLOCK.defaultBlockState().setValue(FlowingFluidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.SOUL_LAVA || fluid == ModFluids.FLOWING_SOUL_LAVA;
    }

    @Override
    public int getDropOff(IWorldReader reader) {
        return reader.dimensionType().ultraWarm() ? 1 : 2;
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, IBlockReader reader, BlockPos pos, Fluid fluid, Direction direction) {
        return state.getHeight(reader, pos) >= 0.44444445F && fluid.is(FluidTags.WATER);
    }

    @Override
    public int getTickDelay(IWorldReader reader) {
        return reader.dimensionType().ultraWarm() ? 10 : 30;
    }

    @Override
    public int getSpreadDelay(World world, BlockPos pos, FluidState state, FluidState p_215667_4_) {
        int tickDelay = getTickDelay(world);
        if (!state.isEmpty() && !p_215667_4_.isEmpty() && !state.getValue(FALLING) && !p_215667_4_.getValue(FALLING) && p_215667_4_.getHeight(world, pos) > state.getHeight(world, pos) && world.getRandom().nextInt(4) != 0) {
            tickDelay *= 4;
        }
        return tickDelay;
    }

    private void fizz(IWorld world, BlockPos pos) {
        world.levelEvent(Constants.WorldEvents.LAVA_EXTINGUISH, pos, 0);
    }

    @Override
    protected boolean canConvertToSource() {
        return false;
    }

    @Override
    protected void spreadTo(IWorld world, BlockPos pos, BlockState bs, Direction direction, FluidState fs) {
        if (direction == Direction.DOWN) {
            FluidState fluidstate = world.getFluidState(pos);
            if (is(FluidTags.LAVA) && fluidstate.is(FluidTags.WATER) || is(ModFluidTags.SOUL_LAVA) && fluidstate.is(FluidTags.LAVA)) {
                if (bs.getBlock() instanceof FlowingFluidBlock) {
                    world.setBlock(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(world, pos, pos, ModBlocks.SOUL_STONE.defaultBlockState()), Constants.BlockFlags.DEFAULT);
                }
                fizz(world, pos);
                return;
            }
        }
        super.spreadTo(world, pos, bs, direction, fs);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    @Override
    protected float getExplosionResistance() {
        return 100;
    }

    public static class Flowing extends SoulLavaFluid {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends SoulLavaFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
