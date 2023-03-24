package lych.soullery.util;

import net.minecraft.util.IIntArray;

public class ProgressArray implements IIntArray {
    private int progress;
    private int total;

    @Override
    public int get(int index) {
        return index == 0 ? progress : total;
    }

    @Override
    public void set(int index, int value) {
        if (index == 0) {
            progress = value;
        } else {
            total = value;
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
