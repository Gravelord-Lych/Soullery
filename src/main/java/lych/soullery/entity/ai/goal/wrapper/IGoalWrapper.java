package lych.soullery.entity.ai.goal.wrapper;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

import java.util.function.Supplier;

@FunctionalInterface
public interface IGoalWrapper extends Supplier<Goal> {
    default Goal getWrappedGoal() {
        Goal goal = get();
        if (goal instanceof PrioritizedGoal) {
            goal = ((PrioritizedGoal) goal).getGoal();
            if (goal instanceof IGoalWrapper) {
                return ((IGoalWrapper) goal).getWrappedGoal();
            }
            return goal;
        }
        return goal instanceof IGoalWrapper ? ((IGoalWrapper) goal).getWrappedGoal() : goal;
    }
}
