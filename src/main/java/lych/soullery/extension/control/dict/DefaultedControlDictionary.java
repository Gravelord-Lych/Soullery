package lych.soullery.extension.control.dict;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.ControllerType;
import lych.soullery.extension.control.SoulManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DefaultedControlDictionary implements ControlDictionary {
    private final Map<EntityType<?>, ControllerType<?>> controllerMap;
    private final List<Pair<Predicate<? super MobEntity>, ControllerType<?>>> conditions;
    private final ControllerType<?> defaultValue;

    private DefaultedControlDictionary(Map<EntityType<?>, ControllerType<?>> controllerMap, ControllerType<?> defaultValue, List<Pair<Predicate<? super MobEntity>, ControllerType<?>>> conditions) {
        this.controllerMap = controllerMap;
        this.defaultValue = defaultValue;
        this.conditions = conditions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MobEntity> ControllerType<? super T> get(EntityType<T> type) {
        ControllerType<?> ct = controllerMap.get(type);
        if (ct == null) {
            ct = defaultValue;
        }
        try {
            return (ControllerType<? super T>) ct;
        } catch (ClassCastException e) {
            throw createException(type, ct, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MobEntity> Controller<? super T> control(T mob, PlayerEntity player, ServerWorld world, int time) {
        if (controllerMap.containsKey(mob.getType())) {
            return ControlDictionary.super.control(mob, player, world, time);
        }
        for (Pair<Predicate<? super MobEntity>, ControllerType<?>> pair : conditions) {
            if (pair.getFirst().test(mob)) {
                try {
                    SoulManager manager = SoulManager.get(world);
                    Controller<? super T> controller = manager.add(mob, player, (ControllerType<? super T>) pair.getSecond());
                    if (time < Integer.MAX_VALUE) {
                        manager.getTimes().setTime(mob, controller.getType(), time);
                    }
                    return controller;
                } catch (ClassCastException e) {
                    throw createException(mob.getType(), pair.getSecond(), e);
                }
            }
        }
        return ControlDictionary.super.control(mob, player, world, time);
    }

    private static RuntimeException createException(EntityType<?> type, ControllerType<?> ct, ClassCastException e) {
        return new RuntimeException(String.format("Invalid EntityType-ControllerType pair: %s -> %s", type.getRegistryName(), ct.getRegistryName()), e);
    }

    public static Builder withDefault(ControllerType<?> ct) {
        return new Builder(ct);
    }

    public static DefaultedControlDictionary only(ControllerType<?> ct) {
        return new DefaultedControlDictionary(Collections.emptyMap(), ct, Collections.emptyList());
    }

    public static class Builder {
        private final ImmutableMap.Builder<EntityType<?>, ControllerType<?>> controllerMapBuilder = ImmutableMap.builder();
        private final ImmutableList.Builder<Pair<Predicate<? super MobEntity>, ControllerType<?>>> conditions = ImmutableList.builder();
        private final ControllerType<?> defaultValue;
        private boolean canControlBosses;

        private Builder(ControllerType<?> defaultValue) {
            this.defaultValue = defaultValue;
        }

        public <T extends MobEntity> Builder specify(EntityType<T> type, ControllerType<? super T> ct) {
            controllerMapBuilder.put(type, ct);
            return this;
        }

        public Builder canControlBosses() {
            canControlBosses = true;
            return this;
        }

        public Builder doNotControl(EntityType<?> type) {
            return addCondition(mob -> mob.getType() == type, null);
        }

        public Builder addCondition(Predicate<? super MobEntity> condition, @Nullable ControllerType<?> ct) {
            conditions.add(Pair.of(condition, ct));
            return this;
        }

        public DefaultedControlDictionary build() {
            if (!canControlBosses) {
                conditions.add(Pair.of(mob -> !mob.canChangeDimensions(), null));
            }
            return new DefaultedControlDictionary(controllerMapBuilder.build(), defaultValue, conditions.build());
        }
    }
}
