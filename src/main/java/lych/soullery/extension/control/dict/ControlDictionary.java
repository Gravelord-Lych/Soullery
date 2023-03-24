package lych.soullery.extension.control.dict;

import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.ControllerType;
import lych.soullery.extension.control.SoulManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;

public interface ControlDictionary {
    <T extends MobEntity> ControllerType<? super T> get(EntityType<T> type);

    default <T extends MobEntity> Controller<? super T> control(T mob, PlayerEntity player) {
        return control(mob, player, Integer.MAX_VALUE);
    }

    default <T extends MobEntity> Controller<? super T> control(T mob, PlayerEntity player, int time) {
        if (player.level.isClientSide()) {
            throw new IllegalStateException("Cannot control a mob clientside");
        }
        return control(mob, player, (ServerWorld) player.level, time);
    }

    @SuppressWarnings("unchecked")
    default <T extends MobEntity> Controller<? super T> control(T mob, PlayerEntity player, ServerWorld world, int time) {
        ControllerType<? super T> type = get((EntityType<T>) mob.getType());
        SoulManager manager = SoulManager.get(world);
        Controller<? super T> controller = manager.add(mob, player, type);
        if (time < Integer.MAX_VALUE) {
            manager.getTimes().setTime(mob, type, time);
        }
        return controller;
    }
}
