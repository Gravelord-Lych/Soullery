package lych.soullery.mixin;

import net.minecraft.entity.ai.goal.RangedAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RangedAttackGoal.class)
public interface RangedAttackGoalAccessor {
    @Accessor
    int getAttackIntervalMin();

    @Accessor
    int getAttackIntervalMax();
}
