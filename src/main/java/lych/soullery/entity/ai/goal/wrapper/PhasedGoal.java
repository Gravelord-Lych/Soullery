package lych.soullery.entity.ai.goal.wrapper;

import lych.soullery.entity.ai.goal.IPhaseableGoal;
import lych.soullery.entity.ai.goal.IPhaseableGoal.StopReason;
import lych.soullery.entity.ai.phase.PhaseManager;
import net.minecraft.entity.ai.goal.Goal;

class PhasedGoal<E extends Enum<E>, G extends Goal & IPhaseableGoal> extends Goal implements IGoalWrapper {
    private final PhaseManager<? extends E> manager;
    private boolean shouldBeNextPhase;
    protected final E requiredPhase;
    private final G goal;

    PhasedGoal(PhaseManager<? extends E> manager, E requiredPhase, G goal) {
        this.manager = manager;
        this.requiredPhase = requiredPhase;
        this.goal = goal;
        setFlags(goal.getFlags());
    }

    @Override
    public boolean canUse() {
        if (manager.getPhase() != requiredPhase) {
            return false;
        }
        StopReason reason = goal.getSkipReason();
        if (reason == null) {
            return true;
        }
        if (reason == StopReason.NEXT_PHASE) {
            manager.nextPhase();
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        goal.start();
    }

    @Override
    public void tick() {
        super.tick();
        goal.tick();
    }

    @Override
    public boolean canContinueToUse() {
        if (manager.getPhase() != requiredPhase) {
            shouldBeNextPhase = false;
            return false;
        }
        if (goal.getStopReason() == StopReason.NO_TARGET) {
            shouldBeNextPhase = false;
            return false;
        }
        if (goal.getStopReason() == StopReason.NEXT_PHASE) {
            shouldBeNextPhase = true;
            return false;
        }
        return goal.getStopReason() == null;
    }

    @Override
    public void stop() {
        super.stop();
        goal.stop();
        if (shouldBeNextPhase) {
            manager.nextPhase();
        }
    }

    @Override
    public boolean isInterruptable() {
        return goal.isInterruptable();
    }

    @Override
    public Goal get() {
        return goal;
    }

    @Override
    public String toString() {
        return String.format("PhasedGoal: {Goal: %s, RequiredPhase: %s}", goal, requiredPhase);
    }
}
