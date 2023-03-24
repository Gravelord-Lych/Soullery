package lych.soullery.mixin;

import net.minecraft.entity.ai.goal.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TargetGoal.class)
public interface TargetGoalAccessor {
    @Accessor(value = "mustReach")
    boolean mustReach();
}
