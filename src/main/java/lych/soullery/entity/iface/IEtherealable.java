package lych.soullery.entity.iface;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

public interface IEtherealable {
    boolean isEthereal();

    double getSizeForCalculation();

    @Nullable
    Vector3d getSneakTarget();

    boolean setSneakTarget(@Nullable Vector3d sneakTarget);

    default void onReachedSneakTarget(Vector3d sneakTarget) {}

    default boolean isInAir() {
        return true;
    }

    default void floatToAir(BlockPos pos) {}
}
