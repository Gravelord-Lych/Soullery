package lych.soullery.extension.maze;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MazePosition {
    public final int x;
    public final int z;
    @Nullable
    private MazePosition father;

    public MazePosition(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Nullable
    public MazePosition getFather() {
        return father;
    }

    public void setFather(MazePosition father) {
        this.father = father;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MazePosition that = (MazePosition) o;
        return posEquals(that);
    }

    public boolean posEquals(MazePosition o) {
        return posEquals(this, o);
    }

    public static boolean posEquals(MazePosition a, MazePosition b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.x == b.x && a.z == b.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, getFather());
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, z);
    }

    public String toFullString() {
        return String.format("(%d, %d) -> %s", x, z, father == null ? "null" : father.toFullString());
    }
}
