package lych.soullery.util.mixin;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IGoalSelectorMixin {
    Set<PrioritizedGoal> getAvailableGoals();

    void setMob(MobEntity mob);

    @Nullable
    GoalSelector getAlt();

    void setAlt(GoalSelector alt);

    void transferGoals();

    void removeAllAltGoals();

    default boolean isAlt() {
        return getAlt() == null;
    }
}
