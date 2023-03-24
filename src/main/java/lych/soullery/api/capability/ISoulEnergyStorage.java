package lych.soullery.api.capability;

import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Range;

/**
 * A Soul Energy <b>(SE)</b> storage. Similar to {@link IEnergyStorage}
 */
public interface ISoulEnergyStorage {
    default int changeSoulEnergy(int amount) {
        return changeSoulEnergy(amount, false);
    }

    default int forceChangeSoulEnergy(int amount) {
        return forceChangeSoulEnergy(amount, false);
    }

    /**
     * @see IEnergyStorage#receiveEnergy(int, boolean)
     */
    default int receiveSoulEnergy(int maxReceive) {
        return receiveSoulEnergy(maxReceive, false);
    }

    /**
     * @see IEnergyStorage#extractEnergy(int, boolean)
     */
    default int extractSoulEnergy(int maxExtract) {
        return extractSoulEnergy(maxExtract, false);
    }

    @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE)
    default int changeSoulEnergy(int amount, boolean simulate) {
        if (amount >= 0) {
            return receiveSoulEnergy(amount, simulate);
        } else {
            return -extractSoulEnergy(-amount, simulate);
        }
    }

    default int forceChangeSoulEnergy(int amount, boolean simulate) {
        if (amount >= 0) {
            return forceReceiveSoulEnergy(amount, simulate);
        } else {
            return -forceExtractSoulEnergy(-amount, simulate);
        }
    }

    int receiveSoulEnergy(int maxReceive, boolean simulate);

    int extractSoulEnergy(int maxExtract, boolean simulate);

    default int forceReceiveSoulEnergy(int maxReceive) {
        return forceReceiveSoulEnergy(maxReceive, false);
    }

    int forceReceiveSoulEnergy(int maxReceive, boolean simulate);

    default int forceExtractSoulEnergy(int maxExtract) {
        return forceExtractSoulEnergy(maxExtract, false);
    }

    int forceExtractSoulEnergy(int maxExtract, boolean simulate);

    int getSoulEnergyStored();

    int getMaxSoulEnergyStored();

    int getMaxExtract();

    default boolean canExtract() {
        return getMaxExtract() > 0;
    }

    int getMaxReceive();

    default boolean canReceive() {
        return getMaxReceive() > 0;
    }
}
