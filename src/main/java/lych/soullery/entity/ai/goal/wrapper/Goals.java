package lych.soullery.entity.ai.goal.wrapper;

import com.google.common.base.Preconditions;
import lych.soullery.entity.ai.goal.IPhaseableGoal;
import lych.soullery.entity.ai.phase.PhaseManager;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public final class Goals {
    private Goals() {}

    public static <G extends Goal> GoalWrapper<?> of(G goal) {
        return goal instanceof IPhaseableGoal ? new PhaseableGoalWrapper(new PhaseableGoal(goal)) : new GoalWrapper<>(goal);
    }

    public static Goal getWrappedGoal(Goal goal) {
        if (goal instanceof PrioritizedGoal) {
            return getWrappedGoal(((PrioritizedGoal) goal).getGoal());
        }
        return goal instanceof IGoalWrapper ? ((IGoalWrapper) goal).getWrappedGoal() : goal;
    }

    public static class GoalWrapper<G extends Goal> implements IGoalWrapper {
        final G goal;

        private GoalWrapper(G goal) {
            this.goal = goal;
        }

        public <E extends Enum<E>> GoalWrapper<?> phased(PhaseManager<? extends E> manager, E phase) {
            throw new UnsupportedOperationException(String.format("Unsupported goal %s for PhasedGoal", goal));
        }

        public GoalWrapper<?> executeIf(BooleanSupplier condition) {
            return of(new SupplementedGoal(goal, condition));
        }

        public GoalWrapper<?> executeIf(BooleanSupplier canUse, BooleanSupplier canContinueToUse) {
            return of(new SupplementedGoal(goal, canUse, canContinueToUse));
        }

        @Override
        public Goal get() {
            return goal;
        }

        public PrioritizedGoal getAsPrioritized(int priority) {
            return new PrioritizedGoal(priority, goal);
        }

        public PrioritizedGoal getAsPrioritized(IntSupplier prioritySupplier) {
            return new MutablePrioritizedGoal(prioritySupplier, goal);
        }
    }

    private static class PhaseableGoalWrapper extends GoalWrapper<PhaseableGoal> {
        private PhaseableGoalWrapper(PhaseableGoal goal) {
            super(goal);
        }

        @Override
        public <E extends Enum<E>> GoalWrapper<?> phased(PhaseManager<? extends E> manager, E phase) {
            return of(new PhasedGoal<>(manager, phase, goal));
        }
    }

    private static class PhaseableGoal extends Goal implements IPhaseableGoal, IGoalWrapper {
        private final IPhaseableGoal phaseableGoal;
        private final Goal goal;

        private PhaseableGoal(Goal goal) {
            Preconditions.checkArgument(goal instanceof IPhaseableGoal, "Unsupported goal: " + goal);
            this.goal = goal;
            this.phaseableGoal = (IPhaseableGoal) goal;
            setFlags(goal.getFlags());
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            return phaseableGoal.getSkipReason();
        }

        @Override
        public boolean isInterruptable() {
            return goal.isInterruptable();
        }

        @Override
        public void start() {
            goal.start();
        }

        @Override
        public void tick() {
            goal.tick();
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            return phaseableGoal.getStopReason();
        }

        @Override
        public void stop() {
            goal.stop();
        }

        @Override
        public String toString() {
            return String.format("PhaseableGoal[%s]", goal);
        }

        @Override
        public Goal get() {
            return goal;
        }
    }
}
