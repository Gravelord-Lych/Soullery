package lych.soullery.util.redirectable;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Redirectable<T extends A, A> extends Predicate<A> {
    T redirect(A a, Function<? super A, ? extends T> ifNotFound);

    @Nullable
    default T redirect(A a) {
        return redirect(a, al -> null);
    }

    @Override
    default boolean test(A a) {
        T t = redirect(a);
        return t != null;
    }

    default Redirectable<T, A> or(Redirectable<T, A> redirectable) {
        return (a, ifNotFound) -> {
            T t = redirect(a);
            if (t != null) {
                return t;
            }
            t = redirectable.redirect(a);
            return t == null ? ifNotFound.apply(a) : t;
        };
    }
}
