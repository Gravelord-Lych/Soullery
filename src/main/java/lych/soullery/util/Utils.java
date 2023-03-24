package lych.soullery.util;

import lych.soullery.world.CommandData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Utils {
    private Utils() {}

    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static boolean allowsCommands(ServerWorld world) {
        return world.getServer().getWorldData().getAllowCommands() || world.getServer().getPlayerList().isAllowCheatsForAllPlayers();
    }

    public static boolean allowedCommands(ServerWorld world) {
        return CommandData.get(world.getServer()).isAllowedCommands();
    }

    public static ResourceLocation getRegistryName(IForgeRegistryEntry<?> entry) {
        return getRegistryName(entry, "If you call this method, you must ensure that registry name is not null");
    }

    public static ResourceLocation getRegistryName(IForgeRegistryEntry<?> entry, String message) {
        return Objects.requireNonNull(entry.getRegistryName(), message);
    }

    public static <T> Collector<T, ?, TreeSet<T>> toTreeSet() {
        return Collectors.toCollection(TreeSet::new);
    }

    public static <T> Collector<T, ?, TreeSet<T>> toTreeSet(Comparator<T> comparator) {
        return Collectors.toCollection(() -> new TreeSet<>(comparator));
    }

    public static float fade(float x) {
        x = MathHelper.clamp(x, 0, 1);
        return (6 * x * x - 15 * x + 10) * x * x * x;
    }

    public static float round(float value, int scale) {
        return round(value, scale, RoundingMode.HALF_UP);
    }

    public static float round(float value, int scale, RoundingMode mode) {
        BigDecimal d = new BigDecimal(Float.toString(value)).setScale(scale, mode);
        return d.floatValue();
    }

    public static double round(double value, int scale) {
        return round(value, scale, RoundingMode.HALF_UP);
    }

    public static double round(double value, int scale, RoundingMode mode) {
        BigDecimal d = new BigDecimal(Double.toString(value)).setScale(scale, mode);
        return d.doubleValue();
    }

    public static <T> T getOrDefault(@Nullable T obj, T defaultValue) {
//      Equivalent to obj == null ? defaultValue : obj
        return getOrDefault(obj, defaultValue, Function.identity());
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static <T, U> U applyIfNonnull(@Nullable T obj, Function<? super T, ? extends U> ifNonNull) {
        return getOrDefault(obj, null, ifNonNull);
    }

    public static <T, U> U getOrDefault(@Nullable T obj, U defaultValue, Function<? super T, ? extends U> ifNonNull) {
        return obj == null ? defaultValue : ifNonNull.apply(obj);
    }

    public static String snakeToCamel(String s) {
        Objects.requireNonNull(s);
        if (s.length() <= 1) {
            return s.toUpperCase();
        }
        String[] arr = s.split("_");
        if (Arrays.stream(arr).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Malformed string " + s);
        }
        StringBuilder builder = new StringBuilder();
        Arrays.stream(arr).forEach(ss -> {
            builder.append(ss.substring(0, 1).toUpperCase());
            builder.append(ss.substring(1));
        });
        return builder.toString();
    }

    public static Color lerpColor(float amount, Color colorO, Color color) {
        int ro = colorO.getRed();
        int go = colorO.getGreen();
        int bo = colorO.getBlue();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return new Color((int) MathHelper.lerp(amount, ro, r), (int) MathHelper.lerp(amount, go, g), (int) MathHelper.lerp(amount, bo, b));
    }

    public static int randomlyCast(float num, Random random) {
        int i = (int) num;
        num -= i;
        if (random.nextFloat() < num) {
            i++;
        }
        return i;
    }

    public static int randomlyCast(double num, Random random) {
        int i = (int) num;
        num -= i;
        if (random.nextDouble() < num) {
            i++;
        }
        return i;
    }
}
