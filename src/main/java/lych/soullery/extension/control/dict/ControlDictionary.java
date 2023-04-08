package lych.soullery.extension.control.dict;

import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.ControllerType;
import lych.soullery.extension.control.SoulManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public interface ControlDictionary {
    @Nullable
    <T extends MobEntity> ControllerType<? super T> get(EntityType<T> type);

    @Nullable
    @SuppressWarnings("unchecked")
    default <T extends MobEntity> ControllerType<? super T> specify(T mob, PlayerEntity player, ServerWorld world, int time) {
        return get((EntityType<T>) mob.getType());
    }

    @Nullable
    default <T extends MobEntity> Controller<? super T> control(T mob, PlayerEntity player) {
        return control(mob, player, Integer.MAX_VALUE);
    }

    @Nullable
    default <T extends MobEntity> Controller<? super T> control(T mob, PlayerEntity player, int time) {
        if (player.level.isClientSide()) {
            throw new IllegalStateException("Cannot control a mob clientside");
        }
        return control(mob, player, (ServerWorld) player.level, time);
    }

    @Nullable
    default <T extends MobEntity> Controller<? super T> control(T mob, PlayerEntity player, ServerWorld world, int time) {
        ControllerType<? super T> type = specify(mob, player, world, time);
        if (type == null) {
            return null;
        }
        SoulManager manager = SoulManager.get(world);
        Controller<? super T> controller = manager.add(mob, player, type);
//      Null-check to prevent setting time for a controller that previously existed.
        if (controller != null && time < Integer.MAX_VALUE) {
            manager.getTimes().setTime(mob, type, time);
        }
        return controller;
    }
}
