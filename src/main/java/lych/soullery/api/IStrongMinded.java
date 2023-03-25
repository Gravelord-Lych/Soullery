package lych.soullery.api;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface IStrongMinded {
    static boolean isStrongMinded(@Nullable Entity mob) {
        return mob instanceof IStrongMinded;
    }

    static boolean nonStrongMinded(@Nullable Entity mob) {
        return !isStrongMinded(mob);
    }
}
