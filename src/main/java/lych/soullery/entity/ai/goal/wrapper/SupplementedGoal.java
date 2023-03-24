package lych.soullery.entity.ai.goal.wrapper;

import net.minecraft.entity.ai.goal.Goal;

import java.util.function.BooleanSupplier;

class SupplementedGoal extends Goal implements IGoalWrapper {
    private final Goal goal;
    private final BooleanSupplier canUse;
    private final BooleanSupplier canContinueToUse;

    SupplementedGoal(Goal goal, BooleanSupplier canUse) {
        this(goal, canUse, canUse);
    }

    SupplementedGoal(Goal goal, BooleanSupplier canUse, BooleanSupplier canContinueToUse) {
        this.goal = goal;
        this.canUse = canUse;
        this.canContinueToUse = canContinueToUse;
        setFlags(goal.getFlags());
    }

    @Override
    public boolean isInterruptable() {
        return goal.isInterruptable();
    }

    @Override
    public boolean canUse() {
        return goal.canUse() & canUse.getAsBoolean();
    }

    @Override
    public void start() {super.start();
        goal.start();
    }

    @Override
    public void tick() {
        goal.tick();
    }

    @Override
    public boolean canContinueToUse() {
        return goal.canContinueToUse() & canContinueToUse.getAsBoolean();
    }

    @Override
    public void stop() {
        super.stop();
        goal.stop();
    }

    @Override
    public String toString() {
        return String.format("SupplementedGoal[%s]", goal);
    }

    @Override
    public Goal get() {
        return goal;
    }
}
