package lych.soullery.util;

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

final class SortedBresenhamCirclePositionIterator extends BresenhamCirclePositionIterator {
    @SuppressWarnings("unchecked")
    private final List<BlockPos>[] positions = Util.make(new List[8], SortedBresenhamCirclePositionIterator::initArray);

    public SortedBresenhamCirclePositionIterator(BlockPos pos, int radius, Direction.Axis normal) {
        super(pos, radius, normal);
    }

    public SortedBresenhamCirclePositionIterator(int cx, int cy, int cz, int radius, Direction.Axis normal) {
        super(cx, cy, cz, radius, normal);
    }

    @SuppressWarnings("rawtypes")
    private static void initArray(List[] l) {
        for (int i = 0; i < l.length; i++) {
            l[i] = new ArrayList<>();
        }
    }

    @Override
    protected BlockPos move(int x, int y, int area) {
        BlockPos pos = super.move(x, y, area);
        positions[area - 1].add(pos);
        return pos;
    }

    public List<BlockPos> get() {
        while (hasNext()) next();

        List<BlockPos> list = new ArrayList<>();
        for (int i = 0; i < positions.length; i++) {
            if ((i & 1) != 0) {
                positions[i] = Lists.reverse(positions[i]);
            }
            list.addAll(positions[i]);
        }
        return list;
    }
}
