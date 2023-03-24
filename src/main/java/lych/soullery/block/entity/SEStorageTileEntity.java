package lych.soullery.block.entity;

import lych.soullery.Soullery;
import lych.soullery.api.ItemSEContainer;
import lych.soullery.api.capability.APICapabilities;
import lych.soullery.api.capability.ISoulEnergyStorage;
import lych.soullery.block.ModBlockStateProperties;
import lych.soullery.gui.container.SEStorageContainer;
import lych.soullery.util.ProgressArray;
import lych.soullery.util.SoulEnergies;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.DoubleSupplier;

public class SEStorageTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    public static final int MAX_SOUL_ENERGY_LEVEL = 5;
    public static final int CAPACITY = SoulEnergies.DEFAULT_CAPACITY * 10;
    public static final int CAPACITY_II = CAPACITY * 4;
    public static final int CAPACITY_III = CAPACITY * 9;
    private static final int MAX_TRANSFER_PER_TICK = 200;

    private final Inventory inventory = new Inventory(1);
    private final IIntArray seProgress = new ProgressArray();
    private final int capacity;
    private final int storageTier;
    private int energy;
    private int tickCount;
    private final LazyOptional<ISoulEnergyStorage> storage = LazyOptional.of(() -> new ISoulEnergyStorage() {
        @Override
        public int receiveSoulEnergy(int maxReceive, boolean simulate) {
            int energy = getSoulEnergyStored();
            int diff = Math.min(getMaxSoulEnergyStored() - energy, maxReceive);
            if (!simulate) {
                SEStorageTileEntity.this.energy += diff;
                if (diff != 0) {
                    setChanged();
                }
            }
            return diff;
        }

        @Override
        public int extractSoulEnergy(int maxExtract, boolean simulate) {
            int diff = Math.min(getSoulEnergyStored(), maxExtract);
            if (!simulate) {
                SEStorageTileEntity.this.energy -= diff;
                if (diff != 0) {
                    setChanged();
                }
            }
            return diff;
        }

        @Override
        public int forceReceiveSoulEnergy(int maxReceive, boolean simulate) {
            return receiveSoulEnergy(maxReceive, simulate);
        }

        @Override
        public int forceExtractSoulEnergy(int maxExtract, boolean simulate) {
            return extractSoulEnergy(maxExtract, simulate);
        }

        @Override
        public int getSoulEnergyStored() {
            return Math.max(0, Math.min(getMaxSoulEnergyStored(), energy));
        }

        @Override
        public int getMaxSoulEnergyStored() {
            return getCapacity();
        }

        @Override
        public int getMaxExtract() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public int getMaxReceive() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    });

    public SEStorageTileEntity(TileEntityType<? extends SEStorageTileEntity> type, int capacity, int storageTier) {
        super(type);
        this.capacity = capacity;
        this.storageTier = storageTier;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Soullery.prefixMsg("gui", "soul_energy_storage") + (storageTier == 1 ? "" : "_l" + storageTier));
    }

    @Nullable
    @Override
    public Container createMenu(int sycID, PlayerInventory inventory, PlayerEntity player) {
        return level == null ? null : new SEStorageContainer(sycID, worldPosition, inventory, level, seProgress, IWorldPosCallable.create(level, worldPosition));
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            tickCount++;
            seProgress.set(0, getSoulEnergyStored());
            seProgress.set(1, getCapacity());
            int se = getSoulEnergyStored();
            updateState(se);
            handleSETransfer();
            spawnParticles(se);
        }
    }

    private void updateState(int energyStored) {
        if (level != null) {
            for (int i = 0; i <= MAX_SOUL_ENERGY_LEVEL; i++) {
                if (energyStored >= getCapacity() * i / MAX_SOUL_ENERGY_LEVEL && energyStored < getCapacity() * (i + 1) / MAX_SOUL_ENERGY_LEVEL) {
                    int currentLevel = getBlockState().getValue(ModBlockStateProperties.SOUL_ENERGY_LEVEL);
                    if (currentLevel != i) {
                        level.setBlock(worldPosition, getBlockState().getBlock().defaultBlockState().setValue(ModBlockStateProperties.SOUL_ENERGY_LEVEL, i), Constants.BlockFlags.DEFAULT);
                    }
                }
            }
        }
    }

    private void handleSETransfer() {
        ItemStack stack = getItemInside();
        if (stack.getItem() instanceof ItemSEContainer && ((ItemSEContainer) stack.getItem()).isTransferable(stack)) {
            switch (ItemSEContainer.getMode(stack)) {
                case IN:
                    SoulEnergies.transfer(this, stack, getMaxTransferPerTick());
                    break;
                case OUT:
                    SoulEnergies.transfer(stack, this, getMaxTransferPerTick());
                    break;
                default:
            }
        }
    }

    private void spawnParticles(int energyStored) {
        if (level != null) {
            Random random = level.random;
            if (random.nextDouble() < 2.0E-07 * energyStored) {
                DoubleSupplier supplier = () -> random.nextBoolean() ? random.nextDouble() * 0.5 + 0.5 : -random.nextDouble() + 0.5;
                ((ServerWorld) level).sendParticles(ParticleTypes.SOUL, worldPosition.getX() + supplier.getAsDouble(), worldPosition.getY() + supplier.getAsDouble(), worldPosition.getZ() + supplier.getAsDouble(), 1, 0.02, 0.2, 0.02, 0.1);
            }
        }
    }

    public int getSoulEnergyStored() {
        return energy;
    }

    public void setSoulEnergyStored(int energy) {
        this.energy = energy;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == APICapabilities.SOUL_ENERGY ? storage.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        ItemStack stack = getItemInside().copy();
        compoundNBT.put("ItemStack", stack.serializeNBT());
        compoundNBT.putInt("Energy", energy);
        return super.save(compoundNBT);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        inventory.addItem(ItemStack.of(compoundNBT.getCompound("ItemStack")));
        energy = compoundNBT.getInt("Energy");
    }

    private int getMaxTransferPerTick() {
        return MAX_TRANSFER_PER_TICK * storageTier * storageTier;
    }

    public int getCapacity() {
        return capacity;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getItemInside() {
        return inventory.getItem(0);
    }

    public int getStorageTier() {
        return storageTier;
    }

    public int getTickCount() {
        return tickCount;
    }
}
