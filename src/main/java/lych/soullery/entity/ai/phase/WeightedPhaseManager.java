package lych.soullery.entity.ai.phase;

import lych.soullery.util.IIdentifiableEnum;
import lych.soullery.util.WeightedRandom;
import lych.soullery.util.WeightedRandom.Item;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class WeightedPhaseManager<E extends Enum<E> & Item & IIdentifiableEnum> extends PhaseManager<E> {
    public WeightedPhaseManager(Supplier<? extends Random> randomSupplier, Supplier<? extends E[]> values) {
        super(randomSupplier, values);
        Objects.requireNonNull(this.randomSupplier, "WeightedPhaseManager requires a non-null randomSupplier");
    }

    @Override
    public void nextPhase() {
        setPhase(getRandomPhase());
    }

    private E getRandomPhase() {
        Random random = getRandom();
        return WeightedRandom.getRandomItem(random, Arrays.asList(getValues()));
    }
}
