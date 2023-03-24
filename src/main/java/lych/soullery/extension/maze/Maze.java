package lych.soullery.extension.maze;

import com.google.common.collect.Lists;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * A maze generator using Depth First Search Algorithm.
 */
public class Maze {
    private static final int[][] MOVEMENTS = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    private final int width;
    private final int length;
    private final Random random;
    private final MazePosition[][] positions;
    private final boolean[][] visitedPositions;
    private final MazePosition start;
    private final Deque<MazePosition> stack = new ArrayDeque<>();
    private List<MazePosition> wayOut = new ArrayList<>();
    private int posRemaining;
    private MazePosition end;

    public Maze(int width, int length, Random random, MazePosition start, MazePosition end) {
        checkArguments(width, length, random, start, end);

        this.width = width;
        this.length = length;
        this.random = random;
        this.positions = new MazePosition[width][length];
        this.visitedPositions = new boolean[width][length];

        posRemaining = width * length;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                positions[i][j] = new MazePosition(i, j);
            }
        }
        this.start = positions[start.x][start.z];
        this.end = positions[end.x][end.z];
        generateMaze();
        findWayOut();
    }

    private static void checkArguments(int width, int length, Random random, MazePosition start, MazePosition end) {
        requireNonNull(start);
        requireNonNull(end);
        requireNonNull(random);
        checkArgument(width > 0 && length > 0, "Width and length must be positive");
        checkArgument(isValidPosition(start, width, length), "Maze's start muse be inside the maze");
        checkArgument(isValidPosition(end, width, length), "Maze's end muse be inside the maze");
    }

    private void generateMaze() {
        MazePosition curr = start;
        while (posRemaining > 0) {
            List<MazePosition> neighbors = getNeighbors(curr);
            if (neighbors.isEmpty()) {
                if (notVisited(curr)) {
                    visit(curr);
                }
                curr = stack.removeFirst();
            } else {
                MazePosition next = neighbors.get(random.nextInt(neighbors.size()));
                stack.addFirst(curr);
                if (notVisited(curr)) {
                    visit(curr);
                }
                next.setFather(curr);
                curr = next;
            }
        }
    }

    private List<MazePosition> getNeighbors(MazePosition pos) {
        List<MazePosition> list = new ArrayList<>(4);
        for (int[] movement : MOVEMENTS) {
            MazePosition neighbor = new MazePosition(pos.x + movement[0], pos.z + movement[1]);
            if (isValidPosition(neighbor) && notVisited(neighbor)) {
                list.add(positions[neighbor.x][neighbor.z]);
            }
        }
        return list;
    }

    public void findWayOut() {
        List<MazePosition> wayOut = new ArrayList<>(length * width / 2);
        wayOut.add(end);
        MazePosition father = end.getFather();
        while (father != null) {
            wayOut.add(father);
            father = father.getFather();
        }
        this.wayOut = reversed(wayOut);
    }

    private List<MazePosition> reversed(List<MazePosition> list) {
        return Lists.reverse(list);
    }

    private boolean isValidPosition(MazePosition pos) {
        return isValidPosition(pos, width, length);
    }

    private static boolean isValidPosition(MazePosition pos, int width, int length) {
        return pos.x >= 0 && pos.x < width && pos.z >= 0 && pos.z < length;
    }

    private boolean notVisited(MazePosition pos) {
        return !visitedPositions[pos.x][pos.z];
    }

    private void visit(MazePosition pos) {
        if (visitedPositions[pos.x][pos.z]) {
            throw new IllegalArgumentException("Visited " + pos);
        }
        visitedPositions[pos.x][pos.z] = true;
        posRemaining--;
    }

    public MazePosition getEnd() {
        return end;
    }

    public void setEnd(MazePosition end) {
        checkArgument(isValidPosition(end, width, length), "Maze's end muse be inside the maze");
        this.end = positions[end.x][end.z];
        findWayOut();
    }

    public List<MazePosition> getWayOut() {
        return wayOut;
    }

    public MazePosition getPositionAt(int x, int z) {
        return positions[x][z];
    }
}
