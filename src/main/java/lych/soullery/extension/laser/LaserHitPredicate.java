package lych.soullery.extension.laser;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class LaserHitPredicate<T> implements BiFunction<Vector3d, World, T>, Predicate<Object>, Comparable<LaserHitPredicate<?>> {
    private final LaserHitType<T> hitType;
    private final Predicate<? super T> predicate;
//  If returns null, the LaserHitPredicate which has lower priority should be used.
    private final BiFunction<? super Vector3d, ? super World, ? extends T> function;
//  The higher the value is, the lower the priority is. MUST BE UNIQUE
    private final int priority;
    private final boolean noResult;

    private LaserHitPredicate(LaserHitType<T> hitType, Predicate<? super T> predicate, BiFunction<? super Vector3d, ? super World, ? extends T> function, boolean noResult, int priority) {
        this.hitType = hitType;
        this.predicate = predicate;
        this.function = function;
        this.noResult = noResult;
        this.priority = priority;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean test(Object t) {
        if (t == null) {
            return false;
        }
        if (hitType.matches(t)) {
            return predicate.test((T) t);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("LaserHitPredicate(Priority: %d)", priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaserHitPredicate<?> that = (LaserHitPredicate<?>) o;
        return priority == that.priority;
    }

    @Override
    public int hashCode() {
        return priority;
    }

    public static <U> Builder<U> by(LaserHitType<U> hitType) {
        return new Builder<>(hitType);
    }

    @Override
    public int compareTo(@NotNull LaserHitPredicate<?> o) {
        return Integer.compare(priority, o.priority);
    }

    @Override
    public T apply(Vector3d vec, World world) {
        return function.apply(vec, world);
    }

    boolean shouldAddToLaserHitResult() {
        if (noResult) {
            return false;
        }
        return hitType.addToLaserAttackResult;
    }

    public LaserHitType<T> getHitType() {
        return hitType;
    }

    public static final class Builder<T> {
        private final LaserHitType<T> hitType;
        private Predicate<? super T> predicate;
        private BiFunction<? super Vector3d, ? super World, ? extends T> function;
        private boolean noResult;

        private Builder(LaserHitType<T> hitType) {
            this.hitType = hitType;
        }

        public Builder<T> predicate(Predicate<? super T> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder<T> makerFunction(BiFunction<? super Vector3d, ? super World, ? extends T> function) {
            this.function = function;
            return this;
        }

        public Builder<T> noResult() {
            this.noResult = true;
            return this;
        }

        public LaserHitPredicate<T> build(int priority) {
            LaserHitPredicate<T> hitPredicate = new LaserHitPredicate<>(hitType, predicate, function, noResult, priority);
            Objects.requireNonNull(hitPredicate.predicate, "Predicate should be non-null");
            Objects.requireNonNull(hitPredicate.function, "Maker function should be non-null");
            return hitPredicate;
        }
    }
}
