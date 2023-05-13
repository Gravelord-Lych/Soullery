package lych.soullery.util.selection;

import lych.soullery.util.WeightedRandom;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public interface DirectSelection<T extends WeightedRandom.Item> {
    T getRandomItem(Random random);

    List<T> getAllItems();

    default Stream<T> directlyStream() {
        return getAllItems().stream();
    }
}
