package lych.soullery.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class APICapabilities {
    @CapabilityInject(ISoulEnergyStorage.class)
    public static Capability<ISoulEnergyStorage> SOUL_ENERGY = null;
    @CapabilityInject(IControlledMobData.class)
    public static Capability<IControlledMobData<?>> CONTROLLED_MOB = null;
}
