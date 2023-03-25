package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.MeleeHandler;
import lych.soullery.extension.control.attack.RightClickHandler;
import lych.soullery.extension.control.attack.TargetFinder;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.rotation.RotationHandler;
import net.minecraft.entity.MobEntity;
import org.jetbrains.annotations.Nullable;

public interface ICustomOperable<T extends MobEntity> {
    @Nullable
    default MeleeHandler<? super T> getMeleeHandler() {
        return null;
    }

    @Nullable
    default MovementHandler<? super T> getMovementHandler() {
        return null;
    }

    @Nullable
    default RightClickHandler<? super T> getRightClickHandler() {
        return null;
    }

    @Nullable
    default RotationHandler<? super T> getRotationHandler() {
        return null;
    }

    @Nullable
    default TargetFinder<? super T> getTargetFinder() {
        return null;
    }

    @Nullable
    default TargetFinder<? super T> getAlternativeTargetFinder() {
        return null;
    }
}
