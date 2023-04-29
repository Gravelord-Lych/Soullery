package lych.soullery.entity.monster;

import java.util.function.Function;

public interface IPurifiable {
    boolean isPurified();

    void setPurified(boolean purified);

    static <T extends IPurifiable, U> Function<T, U> select(U p, U c) {
        return t -> t.isPurified() ? p : c;
    }
}
