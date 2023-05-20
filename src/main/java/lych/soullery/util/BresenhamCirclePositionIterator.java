package lych.soullery.util;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class BresenhamCirclePositionIterator extends AbstractIterator<BlockPos> implements IterableIterator<BlockPos> {
    private static final int[][] INITIAL_STEPS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int[][] STEPS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private final int cx;
    private final int cy;
    private final int cz;
    private final int radius;
    private final Direction.Axis normal;
    private int x;
    private int y;
    private int d;
    private int remain = 4;
    private boolean initial = true;
    private boolean ends;

    public BresenhamCirclePositionIterator(BlockPos pos, int radius, Direction.Axis normal) {
        this(pos.getX(), pos.getY(), pos.getZ(), radius, normal);
    }

    public BresenhamCirclePositionIterator(int cx, int cy, int cz, int radius, Direction.Axis normal) {
        Objects.requireNonNull(normal);
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.radius = radius;
        this.normal = normal;
        x = 0;
        y = radius;
        d = 1 - radius;
    }

    @Override
    protected BlockPos computeNext() {
        if (remain <= 0) {
            initial = false;
            if (ends) {
                return endOfData();
            }
            x++;
            findNext();
        }
        if (x > y) {
            return endOfData();
        }
        remain--;
        if (initial) {
            return move(cx + radius * INITIAL_STEPS[remain][0], cy + radius * INITIAL_STEPS[remain][1]);
        } else {
            if (remain >= 4) {
                return move(cx + x * STEPS[remain - 4][0], cy + y * STEPS[remain - 4][1]);
            } else {
                return move(cx + y * STEPS[remain][0], cy + x * STEPS[remain][1]);
            }
        }
    }

    private void findNext() {
        if (d < 0) {
            d += 2 * x + 1;
        } else {
            y--;
            d += 2 * (x - y) + 1;
        }
        if (x >= y) {
            ends = true;
            remain = 4;
        } else {
            remain = 8;
        }
    }

    private BlockPos move(int x, int y) {
        switch (normal) {
            case X:
                return new BlockPos(cz, x, y);
            case Y:
                return new BlockPos(x, cz, y);
            case Z:
                return new BlockPos(x, y, cz);
            default:
                throw new AssertionError();
        }
    }

    private void bresenham(){
        int r = radius;

        draw(cx, cy + r);

        draw(cx, cy - r);
        draw(cx + r, cy);
        draw(cx - r, cy);

        for (x = 1; x <= y ; x++){
            findNext();

            draw(cx + x, cy + y);

            draw(cx + x, cy - y);
            draw(cx - x, cy + y);
            draw(cx - x, cy - y);
            if (x != y) {
                draw(cy + y, cx + x);
                draw(cy + y, cx - x);
                draw(cy - y, cx + x);
                draw(cy - y, cx - x);
            }
        }
    }

    private static void draw(int x, int y) {
        System.out.printf("(%d, %d)\n", x, y);
    }
}
