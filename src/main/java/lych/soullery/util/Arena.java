package lych.soullery.util;

import lych.soullery.Soullery;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public final class Arena {
    public static final Arena INFINITY = new Arena(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    public final int x0;
    public final int y0;
    public final int z0;
    public final int x1;
    public final int y1;
    public final int z1;

    public Arena(int x0, int y0, int z0, int x1, int y1, int z1) {
        this.x0 = Math.min(x0, x1);
        this.y0 = Math.min(y0, y1);
        this.z0 = Math.min(z0, z1);
        this.x1 = Math.max(x0, x1);
        this.y1 = Math.max(y0, y1);
        this.z1 = Math.max(z0, z1);
    }

    private Arena(CompoundNBT compoundNBT) {
        x0 = compoundNBT.getInt("X0");
        y0 = compoundNBT.getInt("Y0");
        z0 = compoundNBT.getInt("Z0");
        x1 = compoundNBT.getInt("X1");
        y1 = compoundNBT.getInt("Y1");
        z1 = compoundNBT.getInt("Z1");
    }

    public static Arena load(CompoundNBT compoundNBT) {
        return new Arena(compoundNBT);
    }

    public static Arena link(BlockPos from, BlockPos to) {
        return new Arena(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    public static Arena max(double x0, double y0, double z0, double x1, double y1, double z1) {
        return new Arena((int) x0, (int) y0, (int) z0, (int) x1 + 1, (int) y1 + 1, (int) z1 + 1);
    }

    public AxisAlignedBB bb() {
        AxisAlignedBB bb = directBoundingBox();
        double size = bb.getSize();
        if (size > 100) {
            Soullery.LOGGER.warn("BoundingBox too large! Found: {}. Its size is {}", bb, size);
        }
        return bb;
    }

    private AxisAlignedBB directBoundingBox() {
        return new AxisAlignedBB(x0, y0, z0, x1, y1, z1);
    }

    public boolean isInsideHorizontally(BlockPos pos) {
        return pos.getX() >= x0 && pos.getX() <= x1 && pos.getZ() >= z0 && pos.getZ() <= z1;
    }

    public boolean isInsideHorizontally(Vector3d vec) {
        return vec.x() >= x0 && vec.x() <= x1 + 1 && vec.z() >= z0 && vec.z() <= z1 + 1;
    }

    public boolean isInsideHorizontally(Entity entity) {
        return entity.getX() >= x0 && entity.getX() <= x1 + 1 && entity.getZ() >= z0 && entity.getZ() <= z1 + 1;
    }

    public double minHorizontalDistanceToEdge(Vector3d vec) {
        double dx = Math.min(Math.abs(vec.x - x0), Math.abs(vec.x - x1));
        double dz = Math.min(Math.abs(vec.z - z0), Math.abs(vec.z - z1));
        return Math.min(dx, dz);
    }

    public double minDistanceToEdge(Vector3d vec) {
        double dx = Math.min(Math.abs(vec.x - x0), Math.abs(vec.x - x1));
        double dy = Math.min(Math.abs(vec.y - y0), Math.abs(vec.y - y1));
        double dz = Math.min(Math.abs(vec.z - z0), Math.abs(vec.z - z1));
        return Math.min(Math.min(dx, dy), dz);
    }

    @Nullable
    public BlockPos tryFind(Supplier<BlockPos> posSup) {
        return tryFind(posSup, 20);
    }

    @Nullable
    public BlockPos tryFind(Supplier<BlockPos> posSup, int maxTries) {
        for (int i = 0; i < maxTries; i++) {
            BlockPos pos = posSup.get();
            if (pos != null && isInsideHorizontally(pos)) {
                return pos;
            }
        }
        return null;
    }

    public boolean isInfiniteArena() {
        return Objects.equals(this, INFINITY);
    }

    public Vector3d getHorizontalCenter() {
        return new Vector3d((x0 + x1) / 2.0, 0, (z0 + z1) / 2.0);
    }

    public Vector3d moveTowardsHorizontalCenter(Vector3d vec, double dis) {
        return vec.add(vec.vectorTo(getHorizontalCenter().add(0, vec.y, 0)).normalize().scale(dis));
    }

    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("X0", x0);
        compoundNBT.putInt("Y0", y0);
        compoundNBT.putInt("Z0", z0);
        compoundNBT.putInt("X1", x1);
        compoundNBT.putInt("Y1", y1);
        compoundNBT.putInt("Z1", z1);
        return compoundNBT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return x0 == arena.x0 && y0 == arena.y0 && z0 == arena.z0 && x1 == arena.x1 && y1 == arena.y1 && z1 == arena.z1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x0, y0, z0, x1, y1, z1);
    }

    @Override
    public String toString() {
        return String.format("Arena[%d, %d, %d] -> [%d, %d, %d]", x0, y0, z0, x1, y1, z1);
    }
}
