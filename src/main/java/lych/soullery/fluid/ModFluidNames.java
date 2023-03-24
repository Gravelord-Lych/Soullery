package lych.soullery.fluid;

public final class ModFluidNames {
    private static final String STILL = "_still";
    private static final String FLOWING = "_flow";

    public static final String SOUL_LAVA = "soul_lava";

    private ModFluidNames() {}

    public static String still(String o) {
        return o + STILL;
    }

    public static String flowing(String o) {
        return o + FLOWING;
    }
}
