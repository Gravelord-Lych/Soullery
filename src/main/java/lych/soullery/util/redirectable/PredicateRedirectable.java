package lych.soullery.util.redirectable;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PredicateRedirectable<T extends A, A> implements Redirectable<T, A> {
    private final Supplier<? extends T> value;
    private final Predicate<? super A> predicate;

    private PredicateRedirectable(Supplier<? extends T> value, Predicate<? super A> predicate) {
        this.value = value;
        this.predicate = predicate;
    }

    public static <T extends A, A> PredicateRedirectable<T, A> createDirectly(Supplier<? extends T> value, Predicate<? super A> predicate) {
        return new PredicateRedirectable<>(value, predicate);
    }

    @Override
    public T redirect(A a, Function<? super A, ? extends T> ifNotFound) {
        return predicate.test(a) ? value.get() : ifNotFound.apply(a);
    }

    public static <T> Creator<T> withValue(T value) {
        return new Creator<>(() -> value);
    }

    public static <T> Creator<T> withFactory(Supplier<T> supplier) {
        return new Creator<>(supplier);
    }

    public static class Creator<T> {
        private final Supplier<T> value;

        private Creator(Supplier<T> value) {
            this.value = value;
        }

        public PredicateRedirectable<T, T> using(Predicate<? super T> predicate) {
            return createDirectly(value, predicate);
        }
    }
}
