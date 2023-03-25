package lych.soullery.extension.control.attack;

import lych.soullery.extension.control.IHandler;
import lych.soullery.extension.control.movement.DefaultMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.rotation.DefaultRotationHandler;
import lych.soullery.extension.control.rotation.RotationHandler;
import lych.soullery.network.MovementData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class HandlerWrapper<T extends MobEntity, H extends IHandler<? super T>> implements IHandler<T> {
    protected final Supplier<Optional<H>> sup;
    private boolean preparing = true;

    public HandlerWrapper(Supplier<H> sup) {
        this.sup = () -> Optional.ofNullable(sup.get());
    }

    @Override
    public void saveTo(CompoundNBT data) {
        get().ifPresent(h -> h.saveTo(data));
    }

    @Override
    public void loadFrom(CompoundNBT data) {
        if (get().isPresent()) {
            get().get().loadFrom(data);
            preparing = false;
        }
    }

    @Override
    public void tick(T operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        if (preparing && get().isPresent()) {
            get().get().loadFrom(data);
        }
        preparing = false;
        get().ifPresent(h -> h.tick(operatingMob, player, data));
    }

    protected final Optional<H> get() {
        if (preparing) {
            return sup.get();
        }
        return Optional.of(sup.get().orElseGet(this::defaultValue));
    }

    protected abstract H defaultValue();

    public static class Melee<T extends MobEntity> extends HandlerWrapper<T, MeleeHandler<? super T>> implements MeleeHandler<T> {
        public Melee(Supplier<MeleeHandler<? super T>> sup) {
            super(sup);
        }

        @Override
        public void handleMeleeAttack(T operatingMob, LivingEntity target, ServerPlayerEntity player, CompoundNBT data) {
            get().ifPresent(h -> h.handleMeleeAttack(operatingMob, target, player, data));
        }

        @Override
        protected MeleeHandler<? super T> defaultValue() {
            return DefaultMeleeHandler.INSTANCE;
        }
    }

    public static class RClick<T extends MobEntity> extends HandlerWrapper<T, RightClickHandler<? super T>> implements RightClickHandler<T> {
        private final DynamicRightClickHandler rch = new DynamicRightClickHandler();

        public RClick(Supplier<RightClickHandler<? super T>> sup) {
            super(sup);
        }

        @Override
        public boolean needsExactTarget(T operatingMob) {
            return get().map(r -> r.needsExactTarget(operatingMob)).orElse(false);
        }

        @Override
        public void handleRightClick(T operatingMob, ServerPlayerEntity player, CompoundNBT data) {
            get().ifPresent(h -> h.handleRightClick(operatingMob, player, data));
        }

        @Override
        public void handleRightClick(T operatingMob, LivingEntity target, ServerPlayerEntity player, CompoundNBT data) {
            get().ifPresent(h -> h.handleRightClick(operatingMob, target, player, data));
        }

        @Override
        protected RightClickHandler<? super T> defaultValue() {
            return rch;
        }
    }

    public static class Movement<T extends MobEntity> extends HandlerWrapper<T, MovementHandler<? super T>> implements MovementHandler<T> {
        public Movement(Supplier<MovementHandler<? super T>> sup) {
            super(sup);
        }

        @Override
        public void handleMovement(T operatingMob, ServerPlayerEntity player, MovementData movement, @Nullable JumpController jumpControl, CompoundNBT data) {
            get().ifPresent(h -> h.handleMovement(operatingMob, player, movement, jumpControl, data));
        }

        @Override
        protected MovementHandler<? super T> defaultValue() {
            return DefaultMovementHandler.NORMAL;
        }
    }

    public static class Rotation<T extends MobEntity> extends HandlerWrapper<T, RotationHandler<? super T>> implements RotationHandler<T> {
        public Rotation(Supplier<RotationHandler<? super T>> sup) {
            super(sup);
        }

        @Override
        public void handleRotation(T operatingMob, ServerPlayerEntity player, float rotationDelta, CompoundNBT data) {
            get().ifPresent(h -> h.handleRotation(operatingMob, player, rotationDelta, data));
        }

        @Override
        protected RotationHandler<? super T> defaultValue() {
            return DefaultRotationHandler.INSTANCE;
        }
    }

    public static class Target<T extends MobEntity> extends HandlerWrapper<T, TargetFinder<? super T>> implements TargetFinder<T> {
        public Target(Supplier<TargetFinder<? super T>> sup) {
            super(sup);
        }

        @Nullable
        @Override
        public LivingEntity findTarget(T operatingMob, ServerPlayerEntity player, CompoundNBT data) {
            return get().map(f -> f.findTarget(operatingMob, player, data)).orElse(null);
        }

        @Nullable
        @Override
        public TargetFinder<? super T> getPrimary() {
            return get().map(TargetFinder::getPrimary).orElse(null);
        }

        @Override
        protected TargetFinder<? super T> defaultValue() {
            return NoTargetFinder.INSTANCE;
        }
    }
}
