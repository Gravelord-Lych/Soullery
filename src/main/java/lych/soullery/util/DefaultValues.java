package lych.soullery.util;

import lych.soullery.Soullery;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class DefaultValues {
    public static final ITextComponent COMMA = new TranslationTextComponent(Soullery.prefixMsg("comma"));
    public static final ITextComponent SPACE = new TranslationTextComponent(Soullery.prefixMsg("space"));
    public static final ITextComponent TRUE_SPACE = new StringTextComponent(" ");
    private static final Consumer<?> DUMMY_CONSUMER = o -> {};
    private static final Runnable DUMMY_RUNNABLE = () -> {};
    private static final Supplier<?> DUMMY_SUPPLIER = () -> null;
    private static final Function<?, ?> DUMMY_FUNCTION = o -> null;
    private static final Predicate<?> TRUE = o -> true;
    private static final Predicate<?> FALSE = o -> false;
    private static final Goal NO_GOAL = new Goal() {
        @Override
        public boolean canUse() {
            return false;
        }
    };

    private DefaultValues() {}

    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> dummyFunction() {
        return (Function<T, R>) DUMMY_FUNCTION;
    }

    public static Runnable dummyRunnable() {
        return DUMMY_RUNNABLE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> dummySupplier() {
        return (Supplier<T>) DUMMY_SUPPLIER;
    }

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> dummyConsumer() {
        return (Consumer<T>) DUMMY_CONSUMER;
    }

    public static StringTextComponent dummyTextComponent() {
        return (StringTextComponent) StringTextComponent.EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysTrue() {
        return (Predicate<T>) TRUE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysFalse() {
        return (Predicate<T>) FALSE;
    }

    public static Goal dummyGoal() {
        return NO_GOAL;
    }

    public static boolean always() {
        return true;
    }

    public static <T> boolean always(T t) {
        return true;
    }

    public static <T1, T2> boolean always(T1 t1, T2 t2) {
        return true;
    }
}
