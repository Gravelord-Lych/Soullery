package lych.soullery.mixin;

import lych.soullery.util.mixin.ITaskMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Task.class)
public abstract class TaskMixin<E extends LivingEntity> implements ITaskMixin<E> {
    @Shadow public abstract void doStop(ServerWorld level, E entity, long gameTime);

    private boolean disabled;

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void disable(ServerWorld level, E entity, long gameTime) {
        disabled = true;
        doStop(level, entity, gameTime);
    }

    @Override
    public void restart() {
        disabled = false;
        onRestart();
    }

    @Inject(method = "tryStart", at = @At("HEAD"), cancellable = true)
    private void noStartIfDisabled(ServerWorld level, E entity, long gameTime, CallbackInfoReturnable<Boolean> cir) {
        if (isDisabled()) {
            cir.setReturnValue(false);
        }
    }
}
