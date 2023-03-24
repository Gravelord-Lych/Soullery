package lych.soullery.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

public interface IPhaseableGoal {
    /**
     * Replaced method {@link Goal#canUse()}.
     */
    @Nullable
    StopReason getSkipReason();

    /**
     * Replaced method {@link Goal#canContinueToUse()}.
     */
    @Nullable
    StopReason getStopReason();

    enum StopReason {
        NO_TARGET,
        NEXT_PHASE
    }
}
