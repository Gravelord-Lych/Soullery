package lych.soullery.util.selection;

import com.google.common.collect.ImmutableList;
import lych.soullery.util.WeightedRandom;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public interface Selection<T> extends DirectSelection<WeightedRandom.ItemImpl<T>> {
    default T getRandom(Random random) {
        return getRandomItem(random).get();
    }

    default List<T> getAll() {
        return stream().collect(ImmutableList.toImmutableList());
    }

    default Stream<T> stream() {
        return directlyStream().map(WeightedRandom.ItemImpl::get);
    }
}
