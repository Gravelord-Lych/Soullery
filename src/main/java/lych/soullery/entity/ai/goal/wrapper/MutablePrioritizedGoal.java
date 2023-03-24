package lych.soullery.entity.ai.goal.wrapper;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

import java.util.function.IntSupplier;

class MutablePrioritizedGoal extends PrioritizedGoal implements IGoalWrapper {
    private final IntSupplier prioritySupplier;

    MutablePrioritizedGoal(IntSupplier prioritySupplier, Goal goal) {
        super(prioritySupplier.getAsInt(), goal);
        this.prioritySupplier = prioritySupplier;
    }

    @Override
    public int getPriority() {
        return prioritySupplier.getAsInt();
    }

    @Override
    public Goal get() {
        return getGoal();
    }
}