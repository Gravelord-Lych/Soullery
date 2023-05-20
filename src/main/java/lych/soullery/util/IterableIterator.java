package lych.soullery.util;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface IterableIterator<T> extends Iterable<T>, Iterator<T> {
    @Override
    default Iterator<T> iterator() {
        return this;
    }

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
