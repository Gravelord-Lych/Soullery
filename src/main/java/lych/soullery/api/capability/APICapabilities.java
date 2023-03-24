package lych.soullery.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class APICapabilities {
    @CapabilityInject(ISoulEnergyStorage.class)
    public static Capability<ISoulEnergyStorage> SOUL_ENERGY = null;
}
