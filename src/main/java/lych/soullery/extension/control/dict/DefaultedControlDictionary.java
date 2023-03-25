package lych.soullery.extension.control.dict;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import lych.soullery.extension.control.ControllerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class DefaultedControlDictionary implements ControlDictionary {
    private final Map<EntityType<?>, ControllerType<?>> controllerMap;
    private final List<Pair<BiPredicate<? super MobEntity, ? super PlayerEntity>, ControllerType<?>>> conditions;
    private final Set<EntityType<?>> nulls;
    private final ControllerType<?> defaultValue;

    private DefaultedControlDictionary(Map<EntityType<?>, ControllerType<?>> controllerMap, Set<EntityType<?>> nulls, List<Pair<BiPredicate<? super MobEntity, ? super PlayerEntity>, ControllerType<?>>> conditions, ControllerType<?> defaultValue) {
        this.controllerMap = controllerMap;
        this.defaultValue = defaultValue;
        this.conditions = conditions;
        this.nulls = nulls;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public <T extends MobEntity> ControllerType<? super T> get(EntityType<T> type) {
        if (nulls.contains(type)) {
            return null;
        }
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

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public <T extends MobEntity> ControllerType<? super T> specify(T mob, PlayerEntity player, ServerWorld world, int time) {
        if (nulls.contains(mob.getType())) {
            return null;
        }
        if (controllerMap.containsKey(mob.getType())) {
            return get((EntityType<T>) mob.getType());
        }
        for (Pair<BiPredicate<? super MobEntity, ? super PlayerEntity>, ControllerType<?>> pair : conditions) {
            if (pair.getFirst().test(mob, player)) {
                try {
                    return (ControllerType<? super T>) pair.getSecond();
                } catch (ClassCastException e) {
                    throw createException(mob.getType(), pair.getSecond(), e);
                }
            }
        }
        return ControlDictionary.super.specify(mob, player, world, time);
    }

    private static RuntimeException createException(EntityType<?> type, ControllerType<?> ct, ClassCastException e) {
        return new RuntimeException(String.format("Invalid EntityType-ControllerType pair: %s -> %s", type.getRegistryName(), ct.getRegistryName()), e);
    }

    public static Builder withDefault(ControllerType<?> ct) {
        return new Builder(ct);
    }

    public static DefaultedControlDictionary only(ControllerType<?> ct) {
        return new DefaultedControlDictionary(Collections.emptyMap(), Collections.emptySet(), Collections.emptyList(), ct);
    }

    public static class Builder {
        private final ImmutableSet.Builder<EntityType<?>> nulls = ImmutableSet.builder();
        private final ImmutableMap.Builder<EntityType<?>, ControllerType<?>> controllerMapBuilder = ImmutableMap.builder();
        private final ImmutableList.Builder<Pair<BiPredicate<? super MobEntity, ? super PlayerEntity>, ControllerType<?>>> conditions = ImmutableList.builder();
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
            nulls.add(type);
            return this;
        }

        public Builder addCondition(Predicate<? super MobEntity> condition, @Nullable ControllerType<?> ct) {
            return addBiCondition((mob, player) -> condition.test(mob), ct);
        }

        public Builder doNotControlIf(Predicate<? super MobEntity> condition) {
            return addCondition(condition, null);
        }

        public Builder addBiCondition(BiPredicate<? super MobEntity, ? super PlayerEntity> condition, @Nullable ControllerType<?> ct) {
            conditions.add(Pair.of(condition, ct));
            return this;
        }

        public DefaultedControlDictionary build() {
            if (!canControlBosses) {
                addCondition(mob -> !mob.canChangeDimensions(), null);
            }
            return new DefaultedControlDictionary(controllerMapBuilder.build(), nulls.build(), conditions.build(), defaultValue);
        }
    }
}
