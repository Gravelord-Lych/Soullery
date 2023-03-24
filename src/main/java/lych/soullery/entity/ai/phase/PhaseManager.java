package lych.soullery.entity.ai.phase;

import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class PhaseManager<E extends Enum<E> & IIdentifiableEnum> {
    private final Supplier<? extends E[]> values;
    protected final Supplier<? extends Random> randomSupplier;
    protected int currentPhase;

    public PhaseManager(Supplier<? extends E[]> values) {
        this(null, values);
    }

    public PhaseManager(@Nullable Supplier<? extends Random> randomSupplier, Supplier<? extends E[]> values) {
        Objects.requireNonNull(values, "Null phase array supplier is unsupported");
        this.values = values;
        this.randomSupplier = randomSupplier == null ? () -> null : randomSupplier;
    }

    public void nextPhase() {
        int maxPhase = getValues().length - 1;
        if (randomSupplier.get() != null) {
            setPhaseId(randomSupplier.get().nextInt(maxPhase + 1));
        } else {
            if (getPhaseId() >= maxPhase) {
                setPhaseId(0);
            } else {
                setPhaseId(getPhaseId() + 1);
            }
        }
    }

    public E getPhase() {
        return getPhase(getPhaseId());
    }

    public E getPhase(int id) {
        E[] values = getValues();
        try {
            return IIdentifiableEnum.byId(values, id);
        } catch (EnumConstantNotFoundException e) {
            throw new IndexOutOfBoundsException(String.format("Cannot get a phase from id %d, phases are %s", getPhaseId(), Arrays.toString(values)));
        }
    }

    public void setPhase(E phase) {
        setPhaseId(phase.ordinal());
    }

    public int getPhaseId() {
        return currentPhase;
    }

    public void setPhaseId(int phaseId) {
        currentPhase = phaseId;
    }

    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("PhaseManager.CurrentPhase", getPhaseId());
        return compoundNBT;
    }

    public void load(CompoundNBT compoundNBT) {
        setPhaseId(compoundNBT.getInt("PhaseManager.CurrentPhase"));
    }

    protected E[] getValues() {
        return Objects.requireNonNull(values.get(), "Null phase array is unsupported");
    }

    protected Random getRandom() {
        return Objects.requireNonNull(randomSupplier.get(), String.format("%s requires a randomSupplier that always returns non-null", getClass().getSimpleName()));
    }
}
