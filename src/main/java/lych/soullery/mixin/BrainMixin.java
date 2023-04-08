package lych.soullery.mixin;

import lych.soullery.util.mixin.IBrainMixin;
import lych.soullery.util.mixin.ITaskMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(Brain.class)
public abstract class BrainMixin<E extends LivingEntity> implements IBrainMixin<E> {
    @Shadow
    @Final
    private Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors;
    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories;

    @Shadow public abstract void stopAll(ServerWorld p_218227_1_, E p_218227_2_);

    @Shadow @Final private Map<Integer, Map<Activity, Set<Task<? super E>>>> availableBehaviorsByPriority;
    @Unique
    private boolean disabled;

    @Override
    public boolean isValidBrain() {
        return !sensors.isEmpty() && !memories.isEmpty();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void noTick(ServerWorld world, E mob, CallbackInfo ci) {
        if (disabled) {
            stopAll(world, mob);
            ci.cancel();
        }
    }

    @Override
    public Set<Task<? super E>> getTasks(Predicate<? super Task<? super E>> predicate) {
        return availableBehaviorsByPriority.values().stream().map(Map::values).flatMap(Collection::stream).flatMap(Collection::stream).filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public Set<Task<? super E>> getTasks(Predicate<? super Task<? super E>> predicate, Activity activity) {
        return availableBehaviorsByPriority.values().stream().map(map -> map.get(activity)).flatMap(Collection::stream).filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void disableTargetTasks(ServerWorld level, E entity, long gameTime) {
        getTasks(task -> task instanceof ForgetAttackTargetTask).stream().map(ITaskMixin::cast).forEach(task -> task.disable(level, entity, gameTime));
        getTasks(task -> task instanceof FindNewAttackTargetTask).stream().map(ITaskMixin::cast).forEach(task -> task.disablePartially(level, entity, gameTime));
    }

    @Override
    public void restartTargetTasks() {
        getTasks(task -> task instanceof ForgetAttackTargetTask).stream().map(ITaskMixin::cast).forEach(ITaskMixin::restart);
        getTasks(task -> task instanceof FindNewAttackTargetTask).stream().map(ITaskMixin::cast).forEach(ITaskMixin::restart);
    }

    @Override
    public boolean anyMatch(Class<?> type) {
        return availableBehaviorsByPriority.values().stream().anyMatch(map -> map.values().stream().flatMap(Collection::stream).anyMatch(type::isInstance));
    }
}
