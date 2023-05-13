package lych.soullery.util.selection;

import com.google.common.collect.ImmutableList;
import lych.soullery.util.WeightedRandom;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unchecked")
public final class Selections {
    @SuppressWarnings("rawtypes")
    private static final Selection EMPTY_SELECTION = new EmptySelection<>();

    private Selections() {}

    @SafeVarargs
    public static <T extends WeightedRandom.Item> DirectSelection<T> directSelection(T... items) {
        return directSelection(ImmutableList.copyOf(items));
    }

    public static <T extends WeightedRandom.Item> DirectSelection<T> directSelection(List<T> items) {
        if (items.size() == 0) {
            return (DirectSelection<T>) EMPTY_SELECTION;
        }
        if (items.size() == 1) {
            return new SingletonDirectSelection<>(items.get(0));
        }
        return new DirectSelectionImpl<>(ImmutableList.copyOf(items));
    }

    @SafeVarargs
    public static <T> Selection<T> selection(WeightedRandom.ItemImpl<T>... items) {
        return selection(ImmutableList.copyOf(items));
    }

    public static <T> Selection<T> selection(List<WeightedRandom.ItemImpl<T>> items) {
        if (items.size() == 0) {
            return (Selection<T>) EMPTY_SELECTION;
        }
        if (items.size() == 1) {
            return new SingletonSelection<>(items.get(0));
        }
        return new SelectionImpl<>(ImmutableList.copyOf(items));
    }

    private static class DirectSelectionImpl<T extends WeightedRandom.Item> implements DirectSelection<T> {
        protected final List<T> items;

        protected DirectSelectionImpl(List<T> items) {
            this.items = items;
        }

        @Override
        public T getRandomItem(Random random) {
            return WeightedRandom.getRandomItem(random, items);
        }

        @Override
        public List<T> getAllItems() {
            return items;
        }
    }

    private static class SelectionImpl<T> extends DirectSelectionImpl<WeightedRandom.ItemImpl<T>> implements Selection<T> {
        protected SelectionImpl(List<WeightedRandom.ItemImpl<T>> items) {
            super(items);
        }
    }

    private static class EmptySelection<T> implements Selection<T> {
        @Override
        public WeightedRandom.ItemImpl<T> getRandomItem(Random random) {
            return null;
        }

        @Override
        public List<WeightedRandom.ItemImpl<T>> getAllItems() {
            return Collections.emptyList();
        }

        @Override
        public T getRandom(Random random) {
            return null;
        }
    }

    private static class SingletonDirectSelection<T extends WeightedRandom.Item> implements DirectSelection<T> {
        protected final T item;

        private SingletonDirectSelection(T item) {
            this.item = item;
        }

        @Override
        public T getRandomItem(Random random) {
            return item;
        }

        @Override
        public List<T> getAllItems() {
            return Collections.singletonList(item);
        }
    }

    private static class SingletonSelection<T> extends SingletonDirectSelection<WeightedRandom.ItemImpl<T>> implements Selection<T> {
        private SingletonSelection(WeightedRandom.ItemImpl<T> item) {
            super(item);
        }
    }
}
