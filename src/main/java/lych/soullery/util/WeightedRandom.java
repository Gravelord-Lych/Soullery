package lych.soullery.util;

import net.minecraft.util.Util;

import java.util.List;
import java.util.Random;

/**
 * {@link net.minecraft.util.WeightedRandom.Item Vanilla item} is a class.
 * {@link Item This item} uses interface to expand its use.
 */
public final class WeightedRandom {
    private WeightedRandom() {}

    public static int getTotalWeight(List<? extends Item> list) {
        int totalWeight = 0;
        int index = 0;
        for (int i = list.size(); index < i; index++) {
            Item item = list.get(index);
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }

    public static <T extends Item> T getRandomItem(Random random, List<T> list, int bound) {
        return getRandomItem(random::nextInt, list, bound);
    }

    public static <T extends Item> T getRandomItem(IRandom random, List<T> list, int bound) {
        if (bound <= 0) {
            throw Util.pauseInIde(new IllegalArgumentException("Bound must be positive"));
        }
        int randomIndex = random.nextInt(bound);
        return getWeightedItem(list, randomIndex);
    }

    public static <T extends Item> T getWeightedItem(List<T> itemList, int weight) {
        int index = 0;
        for (int i = itemList.size(); index < i; index++) {
            T item = itemList.get(index);
            weight -= item.getWeight();
            if (weight < 0) {
                return item;
            }
        }
        throw new IllegalArgumentException("Weight must not be bigger than totalWeight");
    }

    public static <T extends Item> T getRandomItem(Random random, List<T> itemList) {
        return getRandomItem(random::nextInt, itemList);
    }

    public static <T extends Item> T getRandomItem(IRandom random, List<T> itemList) {
        return getRandomItem(random, itemList, getTotalWeight(itemList));
    }

    public static <T> ItemImpl<T> makeItem(T obj, int weight) {
        return new ItemImpl<>(obj, weight);
    }

    public interface Item {
        int getWeight();
    }

    public interface IRandom {
        int nextInt(int bound);
    }

    public static final class ItemImpl<T> implements Item {
        private final T obj;
        private final int weight;

        private ItemImpl(T obj, int weight) {
            this.obj = obj;
            this.weight = weight;
        }

        public T get() {
            return obj;
        }

        @Override
        public int getWeight() {
            return weight;
        }
    }
}
