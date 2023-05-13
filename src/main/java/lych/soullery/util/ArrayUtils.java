package lych.soullery.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("ConstantConditions")
public final class ArrayUtils {
    private ArrayUtils() {}

    public static void checkNonNull(@Nullable Object... objects) {
        if (objects == null || Arrays.stream(objects).anyMatch(Objects::isNull)) {
            throw new NullPointerException();
        }
    }

    public static <T> boolean isNullOrEmpty(@Nullable T[] array) {
        return array == null || array.length == 0;
    }

    public static int[] shuffle(int[] arr, Random random) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, random.nextInt(i));
        }
        return arr;
    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static int[] split(int num, int cnt) {
        Preconditions.checkArgument(num >= 0, "Num should not be negative");
        Preconditions.checkArgument(cnt > 0, "Count must be positive");
        int[] a = new int[cnt];
        int quotient = num / cnt;
        int remainder = num % cnt;
        if (cnt > remainder) {
            Arrays.fill(a, 0, cnt - remainder, quotient);
        }
        if (remainder > 0) {
            Arrays.fill(a, cnt - remainder, cnt, quotient + 1);
        }
        return a;
    }

    public static <T> T last(T[] array) {
        Objects.requireNonNull(array, "Array cannot be null");
        if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        return array[array.length - 1];
    }

    public static <T> T[] reversed(T[] array) {
        T[] newArray = array.clone();
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[array.length - 1 - i];
        }
        return newArray;
    }
}
