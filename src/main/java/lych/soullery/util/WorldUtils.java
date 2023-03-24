package lych.soullery.util;

import com.google.common.base.Preconditions;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class WorldUtils {
    private WorldUtils() {}

    public static List<BlockPos> getNearbyBlocks(BlockPos pos) {
        return getNearbyBlocks(pos, 1);
    }

    public static List<BlockPos> getNearbyBlocks(BlockPos pos, int radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be positive");
        List<BlockPos> blocks = new ArrayList<>((radius + 2) * (radius + 2) * (radius + 2));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
//                  Exclude self
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    blocks.add(pos.offset(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static BlockPos calculateSummonPosition(int x, int z, World world, PositionCalculators.IYCalculator2 calculator) {
        return new BlockPos(x, calculator.calculate(x, z, world), z);
    }

    public static BlockPos calculateSummonPosition(double x, double y, World world, PositionCalculators.IYCalculator2 calculator) {
        return calculateSummonPosition((int) x, (int) y, world, calculator);
    }

    public static BlockPos calculateSummonPosition(int x, int y, int z, World world, PositionCalculators.IYCalculator3 calculator) {
        return calculateSummonPosition(new BlockPos(x, y, z), world, calculator);
    }

    public static BlockPos calculateSummonPosition(double x, double y, double z, World world, PositionCalculators.IYCalculator3 calculator) {
        return calculateSummonPosition(new BlockPos(x, y, z), world, calculator);
    }

    public static BlockPos calculateSummonPosition(BlockPos pos, World world, PositionCalculators.IYCalculator3 calculator) {
        return new BlockPos(pos.getX(), calculator.calculate(pos, world), pos.getZ());
    }

    public static Vector3d calculateSummonPosition(Vector3d pos, World world, PositionCalculators.IYCalculator3 calculator) {
        int y = calculator.calculate(new BlockPos(pos), world);
        return new Vector3d(pos.x, y, pos.z);
    }

}
