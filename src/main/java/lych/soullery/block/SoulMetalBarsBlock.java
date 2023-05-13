package lych.soullery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lych.soullery.block.ModBlockStateProperties.DAMAGE_LINKABLE;

public class SoulMetalBarsBlock extends PaneBlock {
    private static final int MAX_LINKED_DAMAGE_DIG = 20;
    private static final int MAX_LINKED_DAMAGE_PROJECTILE = 10;
    private static final LazyValue<Map<Integer, SoulMetalBarsBlock>> SOUL_METAL_BARS = new LazyValue<>(SoulMetalBarsBlock::init);
    private final int health;

    public SoulMetalBarsBlock(Properties properties, int health) {
        super(properties);
        this.health = health;
        registerDefaultState(defaultBlockState().setValue(DAMAGE_LINKABLE, false));
    }

    private static Map<Integer, SoulMetalBarsBlock> init() {
        return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.getClass() == SoulMetalBarsBlock.class).map(block -> (SoulMetalBarsBlock) block).collect(Collectors.toMap(SoulMetalBarsBlock::getHealth, Function.identity()));
    }

    public BlockState getState(IBlockReader level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        BlockPos n = pos.north();
        BlockPos s = pos.south();
        BlockPos w = pos.west();
        BlockPos e = pos.east();
        BlockState ns = level.getBlockState(n);
        BlockState ss = level.getBlockState(s);
        BlockState ws = level.getBlockState(w);
        BlockState es = level.getBlockState(e);
        return defaultBlockState().setValue(NORTH, attachsTo(ns, ns.isFaceSturdy(level, n, Direction.SOUTH))).setValue(SOUTH, attachsTo(ss, ss.isFaceSturdy(level, s, Direction.NORTH))).setValue(WEST, attachsTo(ws, ws.isFaceSturdy(level, w, Direction.EAST))).setValue(EAST, attachsTo(es, es.isFaceSturdy(level, e, Direction.WEST))).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    public static boolean handleBlockDestroy(IWorld level, BlockPos start, SoulMetalBarsBlock startBlock, int maxLinkedDamage, boolean dropResources) {
        boolean destroyed = handle(level, start, level.getBlockState(start), startBlock, dropResources);
//      BFS
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        for (int i = 0; i < maxLinkedDamage && !queue.isEmpty(); i++) {
            BlockPos front = queue.remove();
            for (Direction direction : UPDATE_SHAPE_ORDER) {
                BlockPos next = front.offset(direction.getNormal());
                if (visited.contains(next)) {
                    continue;
                }
                BlockState nextState = level.getBlockState(next);
                Block nextBlock = nextState.getBlock();
                if (nextBlock instanceof SoulMetalBarsBlock && nextState.getValue(DAMAGE_LINKABLE)) {
                    handle(level, next, nextState, (SoulMetalBarsBlock) nextBlock, dropResources);
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return destroyed;
    }

    private static boolean handle(IWorld level, BlockPos pos, BlockState state, SoulMetalBarsBlock block, boolean dropResources) {
        int health = block.getHealth();
        if (health <= 1) {
            level.destroyBlock(pos, dropResources);
            return true;
        } else {
            SoulMetalBarsBlock newBlock = block.get(health - 1);
            adjust(level, pos, state, newBlock);
            return false;
        }
    }

    private static void adjust(IWorld level, BlockPos pos, BlockState nextState, SoulMetalBarsBlock block) {
        level.setBlock(pos, block.getState(level, pos).setValue(DAMAGE_LINKABLE, nextState.getValue(DAMAGE_LINKABLE)), Constants.BlockFlags.DEFAULT);
    }

    public static void addParticles(ServerWorld level, Vector3d position, Random random) {
        for (int i = 0; i < 6 + random.nextInt(3); i++) {
            double xo = random.nextGaussian() * 0.2;
            double yo = random.nextGaussian() * 0.2;
            double zo = random.nextGaussian() * 0.2;
            Vector3d particlePos = position.add(xo, yo, zo);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
        }
    }

    public static void destroyHitProjectile(Entity projectile) {
        projectile.remove();
    }

    public int getHealth() {
        return health;
    }

    public int getMaxLinkedDamageDig() {
        return MAX_LINKED_DAMAGE_DIG;
    }

    public int getMaxLinkedDamageProjectile() {
        return MAX_LINKED_DAMAGE_PROJECTILE;
    }

    public SoulMetalBarsBlock get(int health) {
        return SOUL_METAL_BARS.get().get(health);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DAMAGE_LINKABLE);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (state.getValue(ModBlockStateProperties.DAMAGE_LINKABLE) && ((SoulMetalBarsBlock) state.getBlock()).getHealth() > 1) {
            return false;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
