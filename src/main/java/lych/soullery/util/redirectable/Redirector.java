package lych.soullery.util.redirectable;

import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.function.Function;

import static org.apache.commons.lang3.ArrayUtils.add;

public class Redirector<T extends A, A> implements Redirectable<T, A> {
    private final T value;
    private final A[] aliases;

    @SafeVarargs
    public Redirector(T value, A... aliases) {
        Objects.requireNonNull(aliases, "Aliases should be non-null");
        this.value = value;
        this.aliases = add(aliases, value);
        Preconditions.checkArgument(this.aliases.length > 0, "Aliases should not be empty");
    }

    @Override
    public T redirect(A a, Function<? super A, ? extends T> ifNotFound) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(ifNotFound);
        for (A alias : aliases) {
            if (isEqual(a, alias)) {
                return value;
            }
        }
        return ifNotFound.apply(a);
    }

    protected boolean isEqual(A a, A alias) {
        return Objects.equals(a, alias);
    }
}
