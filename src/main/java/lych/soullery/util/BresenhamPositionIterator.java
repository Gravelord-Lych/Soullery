package lych.soullery.util;

import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;

public class BresenhamPositionIterator implements IterableIterator<BlockPos> {
    private final int xInc;
    private final int yInc;
    private final int zInc;
    private final int doubleAbsDx;
    private final int doubleAbsDy;
    private final int doubleAbsDz;
    private final int length;
    private final BlockPos original;
    private final BlockPos.Mutable voxel;
    private final Axis direction;
    private final boolean infinite;

    private int i;
    private int e1;
    private int e2;

    public BresenhamPositionIterator(BlockPos voxel, BlockPos towards) {
        this(voxel, towards, -1);
    }

    public BresenhamPositionIterator(BlockPos voxel, BlockPos towards, int fixedLength) {
        this(voxel, towards.getX(), towards.getY(), towards.getZ(), fixedLength);
    }

    private BresenhamPositionIterator(BlockPos voxel, int x2, int y2, int z2, int fixedLength) {
        this.original = voxel.immutable();
        this.voxel = voxel.mutable();
        this.infinite = fixedLength == Integer.MAX_VALUE;

        int x1 = this.voxel.getX();
        int y1 = this.voxel.getY();
        int z1 = this.voxel.getZ();

        int xVec = x2 - x1;
        int yVec = y2 - y1;
        int zVec = z2 - z1;

        int absDx = Math.abs(xVec);
        int absDy = Math.abs(yVec);
        int absDz = Math.abs(zVec);

        xInc = (xVec < 0) ? -1 : 1;
        yInc = (yVec < 0) ? -1 : 1;
        zInc = (zVec < 0) ? -1 : 1;

        doubleAbsDx = absDx << 1;
        doubleAbsDy = absDy << 1;
        doubleAbsDz = absDz << 1;

        if (absDx >= absDy && absDx >= absDz) {
            e1 = doubleAbsDy - absDx;
            e2 = doubleAbsDz - absDx;
            direction = Axis.X;
            length = fixedLength > 0 ? fixedLength : absDx + 1;
        } else if (absDy >= absDx && absDy >= absDz) {
            e1 = doubleAbsDx - absDy;
            e2 = doubleAbsDz - absDy;
            direction = Axis.Y;
            length = fixedLength > 0 ? fixedLength : absDy + 1;
        } else {
            e1 = doubleAbsDy - absDz;
            e2 = doubleAbsDx - absDz;
            direction = Axis.Z;
            length = fixedLength > 0 ? fixedLength : absDz + 1;
        }
    }

    public void reInit() {
        reInit(doubleAbsDx >> 1, doubleAbsDy >> 1, doubleAbsDz >> 1);
    }

    private void reInit(int absDx, int absDy, int absDz) {
        if (absDx >= absDy && absDx >= absDz) {
            e1 = doubleAbsDy - absDx;
            e2 = doubleAbsDz - absDx;
        } else if (absDy >= absDx && absDy >= absDz) {
            e1 = doubleAbsDx - absDy;
            e2 = doubleAbsDz - absDy;
        } else {
            e1 = doubleAbsDy - absDz;
            e2 = doubleAbsDx - absDz;
        }
        voxel.set(original);
        i = 0;
    }

    @Override
    public boolean hasNext() {
        if (infinite) {
            return true;
        }
        return i < length;
    }

    @Override
    public BlockPos next() {
        BlockPos immutable = voxel.immutable();
        if (hasNext()) {
            findNext();
            i++;
        }
        return immutable;
    }

    private void findNext() {
        switch (direction) {
            case X:
                if (e1 > 0) {
                    voxel.move(0, yInc, 0);
                    e1 -= doubleAbsDx;
                }
                if (e2 > 0) {
                    voxel.move(0, 0, zInc);
                    e2 -= doubleAbsDx;
                }
                e1 += doubleAbsDy;
                e2 += doubleAbsDz;
                voxel.move(xInc, 0, 0);
                break;
            case Y:
                if (e1 > 0) {
                    voxel.move(xInc, 0, 0);
                    e1 -= doubleAbsDy;
                }
                if (e2 > 0) {
                    voxel.move(0, 0, zInc);
                    e2 -= doubleAbsDy;
                }
                e1 += doubleAbsDx;
                e2 += doubleAbsDz;
                voxel.move(0, yInc, 0);
                break;
            case Z:
                if (e1 > 0) {
                    voxel.move(0, yInc, 0);
                    e1 -= doubleAbsDz;
                }
                if (this.e2 > 0) {
                    voxel.move(xInc, 0, 0);
                    e2 -= doubleAbsDz;
                }
                e1 += doubleAbsDy;
                e2 += doubleAbsDx;
                voxel.move(0, 0, zInc);
        }
    }
}
