package lych.soullery.mixin;

import lych.soullery.util.DefaultValues;
import lych.soullery.util.mixin.ITaskMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.function.Predicate;

@Mixin(FindNewAttackTargetTask.class)
public abstract class FindNewAttackTargetTaskMixin<E extends MobEntity> implements ITaskMixin<E> {
    @Mutable
    @Shadow
    @Final
    private Predicate<LivingEntity> stopAttackingWhen;
    @Nullable
    @Unique
    private Predicate<LivingEntity> cachedPredicate;

    @Override
    public void disablePartially(ServerWorld level, E entity, long gameTime) {
        cachedPredicate = stopAttackingWhen;
        stopAttackingWhen = DefaultValues.alwaysFalse();
    }

    @Override
    public void onRestart() {
        if (cachedPredicate != null) {
            stopAttackingWhen = cachedPredicate;
            cachedPredicate = null;
        }
    }
}
