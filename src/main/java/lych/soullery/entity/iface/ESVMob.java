package lych.soullery.entity.iface;

import lych.soullery.api.IStrongMinded;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface ESVMob extends IStrongMinded {
    static boolean isESVMob(@Nullable Entity mob) {
        return mob instanceof ESVMob;
    }

    static boolean nonESVMob(@Nullable Entity mob) {
        return !isESVMob(mob);
    }
}
