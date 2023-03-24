package lych.soullery.util;

import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import org.jetbrains.annotations.Nullable;

public final class PositionCalculators {
    private static final int SMART_POS_CALCULATOR_Y_OFFSET_AMOUNT = 2;

    private PositionCalculators() {}

    public static int up(int x,  int z, World world) {
        return up(x, 0, z, world);
    }

    public static int up(int x, int y, int z,  World world) {
        return up(x, y, z, false, world);
    }

    public static int upLq(int x, int z, World world) {
        return upLq(x, 0, z, world);
    }

    public static int upLq(int x, int y, int z, World world) {
        return up(x, y, z, true, world);
    }

    private static int up(int x, int y, int z, boolean allowLiquid, World world) {
        BlockPos.Mutable pos = new BlockPos.Mutable(x, y, z);
        while (net.minecraft.world.World.isInWorldBounds(pos) && (hasCollider(world, pos) || allowLiquid && world.getBlockState(pos).getMaterial().isLiquid())) {
            pos.move(Direction.UP);
        }
        return pos.getY();
    }

    public static int down(int x,  int z, World world) {
        return down(x, 255, z, world);
    }

    public static int down(int x, int y, int z, World world) {
        return down(x, y, z, false, world);
    }

    public static int downLq(int x,  int z, World world) {
        return downLq(x, 255, z, world);
    }

    public static int downLq(int x, int y, int z, World world) {
        return down(x, y, z, true, world);
    }

    private static int down(int x, int y, int z, boolean allowLiquid, World world) {
        BlockPos.Mutable pos = new BlockPos.Mutable(x, y, z);
        while (net.minecraft.world.World.isInWorldBounds(pos) && !(hasCollider(world, pos) || allowLiquid && world.getBlockState(pos).getMaterial().isLiquid())) {
            pos.move(Direction.DOWN);
        }
        return pos.getY() + 1;
    }

    public static int ud(int x, int y, int z, World world) {
        return ud(new BlockPos(x, y, z), world);
    }

    public static int ud(BlockPos pos, World world) {
        if (world.getBlockState(pos).getMaterial().blocksMotion()) {
            return up(pos.getX(), pos.getY(), pos.getZ(), world);
        } else {
            return down(pos.getX(), pos.getY(), pos.getZ(), world);
        }
    }

    public static int udLq(int x, int y, int z, World world) {
        return udLq(new BlockPos(x, y, z), world);
    }

    public static int udLq(BlockPos pos, World world) {
        Material material = world.getBlockState(pos).getMaterial();
        if (material.blocksMotion() || material.isLiquid()) {
            return upLq(pos.getX(), pos.getY(), pos.getZ(), world);
        } else {
            return downLq(pos.getX(), pos.getY(), pos.getZ(), world);
        }
    }

    public static int smart(int x, int y, int z, World world) {
        return smart(x, y, z, world, null);
    }

    public static int smart(int x, int y, int z, World world, @Nullable BlockPos summonerPos) {
        return smart(new BlockPos(x, y, z), world, summonerPos);
    }

    public static int smart(BlockPos pos, World world) {
        return smart(pos, world, null);
    }

    public static int smart(BlockPos pos, World world, @Nullable BlockPos summonerPos) {
        return smart(pos, world, summonerPos, false);
    }

    public static int smartLq(int x, int y, int z, World world) {
        return smart(x, y, z, world, null);
    }

    public static int smartLq(int x, int y, int z, World world, @Nullable BlockPos summonerPos) {
        return smart(new BlockPos(x, y, z), world, summonerPos);
    }

    public static int smartLq(BlockPos pos, World world) {
        return smart(pos, world, null);
    }

    public static int smartLq(BlockPos pos, World world, @Nullable BlockPos summonerPos) {
        return smart(pos, world, summonerPos, true);
    }

    private static int smart(BlockPos pos, World world, @Nullable BlockPos summonerPos, boolean allowLiquid) {
        return smart(pos, world, summonerPos, pos.getY() - SMART_POS_CALCULATOR_Y_OFFSET_AMOUNT, pos.getY() + SMART_POS_CALCULATOR_Y_OFFSET_AMOUNT, allowLiquid);
    }

    private static int smart(BlockPos pos, World world, @Nullable BlockPos summonerPos, int minY, int maxY, boolean allowLiquid) {
        int reqY = pos.getY();

        BlockPos.Mutable min = pos.mutable();
        min.setY(minY);
        min.setY(allowLiquid ? udLq(min, world) : ud(min, world));
        BlockPos.Mutable max = pos.mutable();
        max.setY(maxY);
        max.setY(allowLiquid ? udLq(max, world) : ud(max, world));

        if (min.getY() == max.getY()) {
            return min.getY();
        }

        int minDelta = Math.abs(reqY - min.getY());
        int maxDelta = Math.abs(reqY - max.getY());

        int maxReachDifficulty = 0, minReachDifficulty = 0;

        boolean minPositionTooNarrow = hasCollider(world, min.below()) && hasCollider(world, min.above());
        boolean maxPositionTooNarrow = hasCollider(world, max.below()) && hasCollider(world, max.above());

        if (minPositionTooNarrow != maxPositionTooNarrow) {
            return minPositionTooNarrow ? max.getY() : min.getY();
        }

//      All Positions are not suitable
        if (minPositionTooNarrow) {
            return ud(pos.getX(), pos.getY() + 8, pos.getZ(), world);
        }

        if (summonerPos != null) {
            BresenhamPositionIterator maxItr = new BresenhamPositionIterator(min, summonerPos);
            BresenhamPositionIterator minItr = new BresenhamPositionIterator(min, summonerPos);

            for (BlockPos posIn : maxItr) {
                if (hasCollider(world, posIn)) {
                    maxReachDifficulty++;
                }
            }
            for (BlockPos posIn : minItr) {
                if (hasCollider(world, posIn)) {
                    minReachDifficulty++;
                }
            }

            if (maxReachDifficulty >= minReachDifficulty && maxDelta > minDelta) {
                return min.getY();
            }
            if (minReachDifficulty >= maxReachDifficulty && minDelta > maxDelta) {
                return max.getY();
            }
        }

        int maxDifficultyValue = maxReachDifficulty * 2 + maxDelta;
        int minDifficultyValue = minReachDifficulty * 2 + minDelta;
        int average = ud(pos.getX(), (max.getY() + min.getY()) >> 1, pos.getZ(), world);

        if (average == min.getY()) {
            minDifficultyValue -= 3;
        }
        if (average == max.getY()) {
            maxDifficultyValue -= 3;
        }

        if (maxDifficultyValue < minDifficultyValue) {
            return max.getY();
        }
        if (minDifficultyValue < maxDifficultyValue) {
            return min.getY();
        }

        return average;
    }

    private static boolean hasCollider(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().blocksMotion();
    }

    public static int heightmap(int x, int z, World world) {
        return world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z);
    }

    public static int heightmapLq(int x, int z, World world) {
        return world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    public interface IYCalculator2 {
        int calculate(int x, int z, World world);
    }

    public interface IYCalculator3 {
        int calculate(BlockPos pos, World world);
    }
}
