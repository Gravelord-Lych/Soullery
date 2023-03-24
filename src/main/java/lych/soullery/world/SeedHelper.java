package lych.soullery.world;

public final class SeedHelper {
    public static long seed;

    private SeedHelper() {}

    public static long getSeed() {
        return seed;
    }

    public static void setSeed(long seed) {
        SeedHelper.seed = seed;
    }
}
