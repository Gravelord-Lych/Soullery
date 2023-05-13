package lych.soullery.capability;

import lych.soullery.api.capability.APICapabilities;
import lych.soullery.api.capability.IControlledMobData;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ControlledMobDataProvider<T extends MobEntity> implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    private final T mob;
    private final ServerWorld level;
    private IControlledMobData<T> cap;

    public ControlledMobDataProvider(T mob, ServerWorld level) {
        this.mob = mob;
        this.level = level;
    }

    @NotNull
    @Override
    public <O> LazyOptional<O> getCapability(@NotNull Capability<O> cap, @Nullable Direction side) {
        return cap == APICapabilities.CONTROLLED_MOB ? LazyOptional.of(this::getOrCreateCap).cast() : LazyOptional.empty();
    }

    private IControlledMobData<T> getOrCreateCap() {
        if (cap == null) {
            cap = new ControlledMobData<>(mob, level);
        }
        return cap;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return getOrCreateCap().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        getOrCreateCap().deserializeNBT(nbt);
    }
}
