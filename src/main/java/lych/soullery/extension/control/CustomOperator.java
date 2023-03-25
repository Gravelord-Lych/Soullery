package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.HandlerWrapper;
import lych.soullery.extension.control.attack.MeleeHandler;
import lych.soullery.extension.control.attack.RightClickHandler;
import lych.soullery.extension.control.attack.TargetFinder;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.rotation.RotationHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomOperator<T extends MobEntity & ICustomOperable<T>> extends MindOperator<T> {
    public CustomOperator(ControllerType<T> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public CustomOperator(ControllerType<T> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    private <H extends IHandler<? super T>> Supplier<H> by(Function<? super T, ? extends H> mappingFunction) {
        return () -> getMob().map(mappingFunction).orElse(null);
    }

    @Override
    protected MeleeHandler<? super T> initMeleeHandler() {
        return new HandlerWrapper.Melee<>(by(ICustomOperable::getMeleeHandler));
    }

    @Override
    protected MovementHandler<? super T> initMovementHandler() {
        return new HandlerWrapper.Movement<>(by(ICustomOperable::getMovementHandler));
    }

    @Override
    protected RightClickHandler<? super T> initRightClickHandler() {
        return new HandlerWrapper.RClick<>(by(ICustomOperable::getRightClickHandler));
    }

    @Override
    protected RotationHandler<? super T> initRotationHandler() {
        return new HandlerWrapper.Rotation<>(by(ICustomOperable::getRotationHandler));
    }

    @Override
    protected TargetFinder<? super T> initTargetFinder() {
        return new HandlerWrapper.Target<>(by(ICustomOperable::getTargetFinder));
    }

    @Nullable
    @Override
    protected TargetFinder<? super T> initAlternativeTargetFinder() {
        return new HandlerWrapper.Target<>(by(ICustomOperable::getAlternativeTargetFinder));
    }
}
