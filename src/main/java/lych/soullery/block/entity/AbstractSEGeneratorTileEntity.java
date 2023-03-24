package lych.soullery.block.entity;

import com.google.common.base.Preconditions;
import lych.soullery.Soullery;
import lych.soullery.api.ItemSEContainer;
import lych.soullery.api.capability.APICapabilities;
import lych.soullery.api.capability.ISoulEnergyStorage;
import lych.soullery.util.InventoryUtils;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;

import static lych.soullery.block.ModBlockStateProperties.IS_GENERATING_SE;

public abstract class AbstractSEGeneratorTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private static final int DEFAULT_CAPACITY = SoulEnergies.DEFAULT_CAPACITY;
    protected static final int MAX_TRANSFER_PER_TICK = 200;

    private final LazyOptional<ISoulEnergyStorage> storage = LazyOptional.of(this::getStorageCapability);
    private final int tier;
    private final Inventory inventory = new Inventory(2);
    private final IIntArray seProgress = new ProgressArray();
    private boolean generating;
    protected int energy;
    protected int tickCount = 1;

    protected AbstractSEGeneratorTileEntity(TileEntityType<? extends AbstractSEGeneratorTileEntity> type, int tier) {
        super(type);
        int maxTier = getMaxTier();
        Preconditions.checkState(maxTier > 0, "Invalid non-positive max tier: " + maxTier);
        Preconditions.checkArgument(tier > 0 && tier <= maxTier, String.format("Invalid tier: %d, Max: %d, Min: %d", tier, maxTier, 0));
        this.tier = tier;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            tickCount++;
            seProgress.set(0, getSoulEnergyStored());
            seProgress.set(1, getCapacity());
            if (shouldGenerateSE()) {
                if (!generating) {
                    generating = true;
                    startGenerating();
                }
                ISoulEnergyStorage storage = SoulEnergies.of(this).orElseThrow(() -> new IllegalStateException("Capability not present"));
                if (tickCount % getGenFrequency() == 0) {
                    storage.forceReceiveSoulEnergy(getSEGeneratedPerTick());
                }
                onGenerationTick();
            } else {
                if (generating) {
                    generating = false;
                    stopGenerating();
                }
            }
            handleSETransfer();
            spawnParticles(getSoulEnergyStored());
        }
    }

    protected abstract int getGenFrequency();

    private void handleSETransfer() {
        ItemStack stack = getSEStorageInside();
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
            if (random.nextDouble() < 1.0E-06 * energyStored) {
                DoubleSupplier supplier = () -> random.nextBoolean() ? random.nextDouble() * 0.5 + 0.5 : -random.nextDouble() + 0.5;
                ((ServerWorld) level).sendParticles(ParticleTypes.SOUL, worldPosition.getX() + supplier.getAsDouble(), worldPosition.getY() + supplier.getAsDouble(), worldPosition.getZ() + supplier.getAsDouble(), 1, 0.02, 0.2, 0.02, 0.1);
            }
        }
    }

    protected void startGenerating() {
        if (level != null) {
            level.setBlock(worldPosition, getBlockState().getBlock().defaultBlockState().setValue(IS_GENERATING_SE, true), Constants.BlockFlags.DEFAULT);
        }
    }

    protected void onGenerationTick() {}

    protected void stopGenerating() {
        if (level != null) {
            level.setBlock(worldPosition, getBlockState().getBlock().defaultBlockState().setValue(IS_GENERATING_SE, false), Constants.BlockFlags.DEFAULT);
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == APICapabilities.SOUL_ENERGY ? storage.cast() : LazyOptional.empty();
    }

    protected ISoulEnergyStorage getStorageCapability() {
        return new Storage();
    }

    protected int getMaxTransferPerTick() {
        return MAX_TRANSFER_PER_TICK;
    }

    public int getSEGeneratedPerTick() {
        return getBaseSEProductAmount() * getTier() * getTier();
    }

    public int getCapacity() {
        return getCapacity(getTier());
    }

    public static int getCapacity(int tier) {
        return DEFAULT_CAPACITY * tier * tier;
    }

    public int getTier() {
        return tier;
    }

    protected abstract boolean shouldGenerateSE();

    protected int getBaseSEProductAmount() {
        return 1;
    }

    protected int getMaxTier() {
        return 2;
    }

    public int getSoulEnergyStored() {
        return energy;
    }

    public void setSoulEnergyStored(int energy) {
        this.energy = energy;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<ItemStack> getItemsInside() {
        return InventoryUtils.getList(inventory);
    }

    public ItemStack getSEStorageInside() {
        return inventory.getItem(0);
    }

    public ItemStack getConsumableInside() {
        return inventory.getItem(1);
    }

    public IIntArray getSEProgress() {
        return seProgress;
    }

    @Nullable
    @Override
    public Container createMenu(int sycID, PlayerInventory inventory, PlayerEntity player) {
        return level == null ? null : createMenu(sycID, worldPosition, inventory, level, seProgress, IWorldPosCallable.create(level, worldPosition));
    }

    protected abstract Container createMenu(int sycID, BlockPos worldPosition, PlayerInventory inventory, World level, IIntArray seProgress, IWorldPosCallable callable);

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Soullery.prefixMsg("gui", getName()) + (getTier() == 1 ? "" : "_l" + getTier()));
    }

    protected abstract String getName();

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        ItemStack storage = getSEStorageInside().copy();
        ItemStack consumable = getConsumableInside().copy();
        compoundNBT.put("SEStorage", storage.serializeNBT());
        compoundNBT.put("Consumable", consumable.serializeNBT());
        compoundNBT.putInt("Energy", energy);
        compoundNBT.putBoolean("Generating", generating);
        return super.save(compoundNBT);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        if (compoundNBT.contains("SEStorage") && compoundNBT.contains("Consumable")) {
            inventory.clearContent();
            inventory.setItem(0, ItemStack.of(compoundNBT.getCompound("SEStorage")));
            inventory.setItem(1, ItemStack.of(compoundNBT.getCompound("Consumable")));
        }
        energy = compoundNBT.getInt("Energy");
        generating = compoundNBT.getBoolean("Generating");
    }

    public boolean isGenerating() {
        return generating;
    }

    protected class Storage implements ISoulEnergyStorage {
        @Override
        public int receiveSoulEnergy(int maxReceive, boolean simulate) {
            int energy = getSoulEnergyStored();
            int diff = Math.min(getMaxReceive(), Math.min(getMaxSoulEnergyStored() - energy, maxReceive));
            if (!simulate) {
                AbstractSEGeneratorTileEntity.this.energy += diff;
                if (diff != 0) {
                    setChanged();
                }
            }
            return diff;
        }

        @Override
        public int extractSoulEnergy(int maxExtract, boolean simulate) {
            int diff = Math.min(getMaxExtract(), Math.min(getSoulEnergyStored(), maxExtract));
            if (!simulate) {
                AbstractSEGeneratorTileEntity.this.energy -= diff;
                if (diff != 0) {
                    setChanged();
                }
            }
            return diff;
        }

        @Override
        public int forceReceiveSoulEnergy(int maxReceive, boolean simulate) {
            int energy = getSoulEnergyStored();
            int diff = Math.min(getMaxSoulEnergyStored() - energy, maxReceive);
            if (!simulate) {
                AbstractSEGeneratorTileEntity.this.energy += diff;
                if (diff != 0) {
                    setChanged();
                }
            }
            return diff;
        }

        @Override
        public int forceExtractSoulEnergy(int maxExtract, boolean simulate) {
            int diff = Math.min(getSoulEnergyStored(), maxExtract);
            if (!simulate) {
                AbstractSEGeneratorTileEntity.this.energy -= diff;
                if (diff != 0) {
                    setChanged();
                }
            }
            return diff;
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
        public int getMaxReceive() {
            return 0;
        }
    }
}
