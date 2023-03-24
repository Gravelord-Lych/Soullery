package lych.soullery.capability;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import lych.soullery.api.ItemSEContainer;
import lych.soullery.api.capability.ISoulEnergyStorage;
import lych.soullery.api.capability.APICapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class ItemSoulEnergyProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    public static final String SOUL_ENERGY_TAG = "SoulEnergy.Energy";
    private final ISoulEnergyStorage storage;

    public ItemSoulEnergyProvider(ItemSEContainer container, Supplier<ItemStack> stack) {
        this.storage = new ItemSoulEnergyStorage(container, stack);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == APICapabilities.SOUL_ENERGY ? LazyOptional.of(() -> storage).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {}

    private static class ItemSoulEnergyStorage implements ISoulEnergyStorage {
        private static final String CAPACITY = "SoulEnergy.Capacity";
        private static final String MAX_RECEIVE = "SoulEnergy.MaxReceive";
        private static final String MAX_EXTRACT = "SoulEnergy.MaxExtract";

        @NotNull
        private final Supplier<ItemStack> stack;

        public ItemSoulEnergyStorage(ItemSEContainer container, Supplier<ItemStack> stack) {
            this.stack = Suppliers.memoize(stack::get);
            this.stack.get().getOrCreateTag().putInt(CAPACITY, container.getCapacity(this.stack.get()));
            this.stack.get().getOrCreateTag().putInt(MAX_RECEIVE, container.getMaxReceive(this.stack.get()));
            this.stack.get().getOrCreateTag().putInt(MAX_EXTRACT, container.getMaxExtract(this.stack.get()));
        }

        @Override
        public int receiveSoulEnergy(int maxReceive, boolean simulate) {
            Preconditions.checkArgument(maxReceive >= 0, "MaxExtract should not be negative");
            if (!canReceive()) {
                return 0;
            }
            int energyReceived = Math.min(getMaxSoulEnergyStored() - getSoulEnergyStored(), Math.min(getMaxReceive(), maxReceive));
            if (!simulate) {
                actuallyReceive(energyReceived);
            }
            return energyReceived;
        }

        protected void actuallyReceive(int energyReceived) {
            stack.get().getOrCreateTag().putInt(SOUL_ENERGY_TAG, stack.get().getOrCreateTag().getInt(SOUL_ENERGY_TAG) + energyReceived);
        }

        @Override
        public int extractSoulEnergy(int maxExtract, boolean simulate) {
            Preconditions.checkArgument(maxExtract >= 0, "MaxExtract should not be negative");
            if (!canExtract()) {
                return 0;
            }
            int energyExtracted = Math.min(getSoulEnergyStored(), Math.min(getMaxExtract(), maxExtract));
            if (!simulate) {
                actuallyExtract(energyExtracted);
            }
            return energyExtracted;
        }

        protected void actuallyExtract(int energyExtracted) {
            stack.get().getOrCreateTag().putInt(SOUL_ENERGY_TAG, stack.get().getOrCreateTag().getInt(SOUL_ENERGY_TAG) - energyExtracted);
        }

        @Override
        public int forceReceiveSoulEnergy(int maxReceive, boolean simulate) {
            Preconditions.checkArgument(maxReceive >= 0, "MaxReceive should not be negative");
            int energyReceived = Math.min(getMaxSoulEnergyStored() - getSoulEnergyStored(), maxReceive);
            if (!simulate) {
                actuallyReceive(energyReceived);
            }
            return energyReceived;
        }

        @Override
        public int forceExtractSoulEnergy(int maxExtract, boolean simulate) {
            Preconditions.checkArgument(maxExtract >= 0, "MaxExtract should not be negative");
            int energyExtracted = Math.min(getSoulEnergyStored(), maxExtract);
            if (!simulate) {
                actuallyExtract(energyExtracted);
            }
            return energyExtracted;
        }

        @Override
        public int getSoulEnergyStored() {
            if (stack.get().hasTag()) {
                int energy = Objects.requireNonNull(stack.get().getTag()).getInt(SOUL_ENERGY_TAG);
                return Math.max(0, Math.min(getMaxSoulEnergyStored(), energy));
            }
            return 0;
        }

        @Override
        public int getMaxSoulEnergyStored() {
            if (!stack.get().hasTag()) {
                return 0;
            }
            return stack.get().getTag().getInt(CAPACITY);
        }

        @Override
        public int getMaxReceive() {
            if (!stack.get().hasTag()) {
                return 0;
            }
            return stack.get().getTag().getInt(MAX_RECEIVE);
        }

        @Override
        public int getMaxExtract() {
            if (!stack.get().hasTag()) {
                return 0;
            }
            return stack.get().getTag().getInt(MAX_EXTRACT);
        }
    }
}
