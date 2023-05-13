package lych.soullery.api.capability;

import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.ControllerType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.PriorityQueue;
import java.util.function.Predicate;

public interface IControlledMobData<T extends MobEntity> extends INBTSerializable<CompoundNBT> {
    PriorityQueue<Controller<?>> getControllers();

    @Nullable
    Controller<? super T> add(PlayerEntity player, @Nullable ControllerType<? super T> type);

    @Nullable
    Controller<?> removeIf(Predicate<? super Controller<?>> removePredicate, boolean checkDuplication);

    Timer getTimer();

    void tick();

    @Nullable
    default PlayerEntity getPlayerController() {
        if (getActiveController() == null) {
            return null;
        }
        return getActiveController().getPlayer();
    }

    @Nullable
    default Controller<?> getActiveController() {
        return getControllers().peek();
    }

    default boolean hasControllers() {
        return !getControllers().isEmpty();
    }

    interface Timer extends INBTSerializable<CompoundNBT> {
        void tick();

        void setTime(ControllerType<?> type, int time);

        int timeRemaining(ControllerType<?> type);

        double getRemainingPercent(ControllerType<?> type);
    }
}
