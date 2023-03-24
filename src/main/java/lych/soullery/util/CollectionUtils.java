package lych.soullery.util;

import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static <T> Collector<T, ?, NonNullList<T>> toNonNullList() {
        return Collectors.toCollection(NonNullList::create);
    }

    @SafeVarargs
    public static <T> Collector<T, ?, NonNullList<T>> toNonNullList(T defaultValue, T... values) {
        return Collectors.toCollection(() -> createMutableNonNullList(defaultValue, values));
    }

    public static <T> Collector<T, ?, NonNullList<T>> toSizedNonNullList(T defaultValue, int size) {
        return Collectors.toCollection(() -> createMutableNonNullListWithSize(size, defaultValue));
    }

    @SafeVarargs
    public static <T> NonNullList<T> createMutableNonNullList(T defaultValue, T... elements) {
        return new NonNullList<T>(new ArrayList<>(Arrays.asList(elements)), defaultValue) {};
    }

    public static <T> NonNullList<T> createMutableNonNullListWithSize(int size, T defaultValue) {
        return new NonNullList<T>(new ArrayList<>(size), defaultValue) {};
    }

    public static <T> T getNonnullRandom(Collection<T> collection, Random random) {
        return Objects.requireNonNull(getRandomIn(new ArrayList<>(collection), random));
    }

    @Nullable
    public static <T> T getRandom(Collection<T> collection, Random random) {
        return getRandomIn(collection instanceof List && collection instanceof RandomAccess ? (List<T>) collection : new ArrayList<>(collection), random);
    }

    @Nullable
    private static <T> T getRandomIn(List<T> list, Random random) {
        if (isNullOrEmpty(list)) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    public static <T> void refill(Collection<? super T> oldCollection, Collection<? extends T> newCollection) {
        oldCollection.clear();
        oldCollection.addAll(newCollection);
    }

    public static <K, V> void refill(Map<? super K, ? super V> oldMap, Map<? extends K, ? extends V> newMap) {
        oldMap.clear();
        oldMap.putAll(newMap);
    }

    public static <K, V> Stream<? extends Map.Entry<? extends K, ? extends V>> stream(Map<? extends K, ? extends V> map) {
        return map.entrySet().stream();
    }

    public static boolean isNullOrEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> Iterable<T> iterable(Iterator<T> itr) {
        return () -> itr;
    }

    public static <T> List<T> list(Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }
        return Util.make(new ArrayList<>(), list -> iterable.forEach(list::add));
    }
}
