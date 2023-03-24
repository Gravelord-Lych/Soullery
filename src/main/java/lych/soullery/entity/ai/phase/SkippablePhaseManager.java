package lych.soullery.entity.ai.phase;

import lych.soullery.util.IIdentifiableEnum;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class SkippablePhaseManager<E extends Enum<E> & ISkippablePhase & IIdentifiableEnum> extends PhaseManager<E> {
    public SkippablePhaseManager(Supplier<? extends Random> randomSupplier, Supplier<? extends E[]> values) {
        super(randomSupplier, values);
        Objects.requireNonNull(this.randomSupplier, "SkippablePhaseManager requires a non-null randomSupplier");
    }

    @Override
    public void nextPhase() {
        nextPhaseDirectly();
        if (shouldSkip(getPhase())) {
            nextPhase();
        }
    }

    private void nextPhaseDirectly() {
        int maxPhase = getValues().length - 1;
        if (getPhaseId() >= maxPhase) {
            setPhaseId(0);
        } else {
            setPhaseId(getPhaseId() + 1);
        }
    }

    private boolean shouldSkip(E nextPhase) {
        double skipProbability = getSkipProbability(nextPhase);
        if (skipProbability <= 0) {
            return false;
        }
        if (skipProbability >= 1) {
            return true;
        }
        return getRandom().nextDouble() < skipProbability;
    }

    private double getSkipProbability(E phase) {
        return MathHelper.clamp(phase.getSkipProbability(), 0, 1);
    }
}
