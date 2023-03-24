package lych.soullery.util;

import com.google.common.collect.Iterables;
import lych.soullery.Soullery;
import lych.soullery.api.ItemSEContainer;
import lych.soullery.api.capability.APICapabilities;
import lych.soullery.api.capability.ISoulEnergyStorage;
import lych.soullery.item.SEGemItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;

public final class SoulEnergies {
    public static final Marker SENERGY = MarkerManager.getMarker("SoulEnergy");
    public static final int DEFAULT_CAPACITY = 20000;
    public static final int EXCHANGE_RATE_OF_FE = 4;
    public static final ResourceLocation SOUL_ENERGY_LEVEL = Soullery.prefix("soul_energy_level");

    private static final int MAX_SE_STORE_HUE = 200;
    private static final int MIN_SE_STORE_HUE = 180;
    private static final double SE_STORE_SATURATION = 0.8;
    private static final double MAX_SE_STORE_VALUE = 0.95;
    private static final double MIN_SE_STORE_VALUE = 0.7;

    private SoulEnergies() {}

    public static int toFE(int se) {
        return se * EXCHANGE_RATE_OF_FE;
    }

    public static int toSE(int fe) {
        return fe / EXCHANGE_RATE_OF_FE;
    }

    public static Capability<ISoulEnergyStorage> get() {
        return APICapabilities.SOUL_ENERGY;
    }

    public static LazyOptional<ISoulEnergyStorage> of(CapabilityProvider<?> provider) {
        return provider.getCapability(get());
    }

    /**
     * Transfer SE of two energy storages.
     * @param from The energy storage which is going to extract SE.
     * @param to The energy storage which is going to receive SE.
     * @param maxTransfer The maximum SE which will be transferred.
     * @return The actual SE which was transferred.
     */
    public static int transfer(CapabilityProvider<?> from, CapabilityProvider<?> to, int maxTransfer) {
        return transfer(from, to, maxTransfer, false);
    }

    /**
     * Transfer SE of two energy storages.
     * @param from The energy storage which is going to extract SE.
     * @param to The energy storage which is going to receive SE.
     * @param maxTransfer The maximum SE which will be transferred.
     * @param simulate If TRUE, the extraction will only be simulated.
     * @return The actual SE which was transferred.
     */
    public static int transfer(CapabilityProvider<?> from, CapabilityProvider<?> to, int maxTransfer, boolean simulate) {
        ISoulEnergyStorage fromStorage = of(from).orElseThrow(() -> new IllegalArgumentException("Source ISoulEnergyStorage not present"));
        ISoulEnergyStorage toStorage = of(to).orElseThrow(() -> new IllegalArgumentException("Target ISoulEnergyStorage not present"));
        int expectTransfer = fromStorage.extractSoulEnergy(maxTransfer, simulate);
        int actualTransfer = toStorage.receiveSoulEnergy(expectTransfer, simulate);
        fromStorage.receiveSoulEnergy(expectTransfer - actualTransfer, simulate);
        return actualTransfer;
    }

    /**
     * Transfer FE from a <b>SE</b> storage.
     * @param from The energy storage which is going to extract FE.
     * @param to The energy storage which is going to receive SE.
     * @param maxTransfer The maximum <b>SE</b> which will be transferred.
     * @return The actual <b>FE</b> which was transferred.
     */
    public static int transferFE(CapabilityProvider<?> from, CapabilityProvider<?> to, int maxTransfer) {
        return transferFE(from, to, maxTransfer, false);
    }

    /**
     * Transfer FE from a <b>SE</b> storage.
     * @param from The energy storage which is going to extract FE.
     * @param to The energy storage which is going to receive SE.
     * @param maxTransfer The maximum <b>SE</b> which will be transferred.
     * @param simulate If TRUE, the transfer will only be simulated.
     * @return The actual <b>FE</b> which was transferred.
     */
    public static int transferFE(CapabilityProvider<?> from, CapabilityProvider<?> to, int maxTransfer, boolean simulate) {
        ISoulEnergyStorage fromStorage = of(from).orElseThrow(() -> new IllegalArgumentException("Source ISoulEnergyStorage not present"));
//      Forge Energy storage.
        IEnergyStorage toStorage = to.getCapability(CapabilityEnergy.ENERGY).orElseThrow(() -> new IllegalArgumentException("Target IEnergyStorage not present"));
        int expectTransfer = toFE(fromStorage.extractSoulEnergy(maxTransfer, simulate));
        int actualTransfer = toStorage.receiveEnergy(expectTransfer, simulate);
        fromStorage.receiveSoulEnergy(expectTransfer - actualTransfer, simulate);
        return actualTransfer;
    }

    /**
     * Transfer SE from a <b>FE</b> storage.
     * @param from The energy storage which is going to extract SE.
     * @param to The energy storage which is going to receive FE.
     * @param maxTransfer The maximum <b>FE</b> which will be transferred.
     * @return The actual <b>SE</b> which was transferred.
     */
    public static int transferSE(CapabilityProvider<?> from, CapabilityProvider<?> to, int maxTransfer) {
        return transferSE(from, to, maxTransfer, false);
    }

    /**
     * Transfer SE from a <b>FE</b> storage.
     * @param from The energy storage which is going to extract SE.
     * @param to The energy storage which is going to receive FE.
     * @param maxTransfer The maximum <b>FE</b> which will be transferred.
     * @param simulate If TRUE, the transfer will only be simulated.
     * @return The actual <b>SE</b> which was transferred.
     */
    public static int transferSE(CapabilityProvider<?> from, CapabilityProvider<?> to, int maxTransfer, boolean simulate) {
        IEnergyStorage fromStorage = from.getCapability(CapabilityEnergy.ENERGY).orElseThrow(() -> new IllegalArgumentException("Source IEnergyStorage not present"));
//      Forge Energy storage.
        ISoulEnergyStorage toStorage = of(to).orElseThrow(() -> new IllegalArgumentException("Target ISoulEnergyStorage not present"));
        int expectTransfer = toSE(fromStorage.extractEnergy(maxTransfer, simulate));
        int actualTransfer = toStorage.receiveSoulEnergy(expectTransfer, simulate);
        fromStorage.receiveEnergy(expectTransfer - actualTransfer, simulate);
        return actualTransfer;
    }

    public static void addSEToolTip(ItemStack stack, List<ITextComponent> tips) {
        addSEToolTip(stack, tips, stackIn -> of(stackIn).map(ISoulEnergyStorage::getSoulEnergyStored).orElse(0), stackIn -> of(stackIn).map(ISoulEnergyStorage::getMaxSoulEnergyStored).orElse(0));
    }

    public static void addSEToolTip(ItemStack stack, List<ITextComponent> tips, ToIntFunction<ItemStack> energyGetter, ToIntFunction<ItemStack> maxEnergyGetter) {
        checkStack(stack);
        of(stack).ifPresent(ses -> tips.add(formatSE(energyGetter.applyAsInt(stack), maxEnergyGetter.applyAsInt(stack))));
    }

    public static IFormattableTextComponent formatSE(int energy, int maxEnergy) {
        return new StringTextComponent(String.format("%d SE / %d SE", energy, maxEnergy)).withStyle(TextFormatting.DARK_AQUA);
    }

    public static double getDurabilityForDisplay(ItemStack stack) {
        checkStack(stack);
        MutableDouble durability = new MutableDouble();
        of(stack).ifPresent(ses -> durability.setValue(((double) ses.getSoulEnergyStored()) / ses.getMaxSoulEnergyStored()));
        return MathHelper.clamp(1 - durability.doubleValue(), 0, 1);
    }

    public static int getRGBDurabilityForDisplay(ItemStack stack) {
        return getRGBDurabilityForDisplay(stack, SoulEnergies::getDurabilityForDisplay);
    }

    public static int getRGBDurabilityForDisplay(ItemStack stack, ToDoubleFunction<ItemStack> durabilityGetter) {
        checkStack(stack);
        final double maxH = MAX_SE_STORE_HUE / 360.0;
        final double minH = MIN_SE_STORE_HUE / 360.0;
        return MathHelper.hsvToRgb((float) (minH + (maxH - minH) * MathHelper.clamp(1 - durabilityGetter.applyAsDouble(stack), 0, 1)), (float) SE_STORE_SATURATION, (float) MathHelper.lerp(MathHelper.clamp(1 - durabilityGetter.applyAsDouble(stack), 0, 1), MIN_SE_STORE_VALUE, MAX_SE_STORE_VALUE));
    }

    public static float getSELevel(ItemStack stack) {
        checkStack(stack);
        MutableFloat level = new MutableFloat();
        of(stack).ifPresent(ses -> level.setValue(((float) ses.getSoulEnergyStored()) / ses.getMaxSoulEnergyStored()));
        return level.floatValue();
    }

    public static void checkStack(ItemStack stack) {
        checkStack(stack, () -> new UnsupportedOperationException("stack.getItem() should be ItemSEContainer"));
    }

    public static <X extends Throwable> void checkStack(ItemStack stack, Supplier<X> throwableSupplier) throws X {
        Objects.requireNonNull(stack, "ItemStack should be non-null");
        if (!(stack.getItem() instanceof ItemSEContainer)) {
            throw throwableSupplier.get();
        }
    }

    public static ItemStack getSEContainer(PlayerEntity player) {
        return getSEContainer(player, (stack, item) -> true);
    }

    public static ItemStack getSEContainer(PlayerEntity player, SEGemItem matchedGem) {
        return getSEContainer(player, (stack, item) -> item == matchedGem);
    }

    public static ItemStack getSEContainer(PlayerEntity player, BiPredicate<ItemStack, ItemSEContainer> gemPredicate) {
        return getSEContainers(player, gemPredicate).stream().findFirst().orElse(ItemStack.EMPTY);
    }

    public static NonNullList<ItemStack> getSEContainers(PlayerEntity player) {
        return getSEContainers(player, (stack, item) -> true);
    }

    public static NonNullList<ItemStack> getSEContainers(PlayerEntity player, SEGemItem matchedGem) {
        return getSEContainers(player, (stack, item) -> item == matchedGem);
    }

    public static NonNullList<ItemStack> getSEContainers(PlayerEntity player, BiPredicate<ItemStack, ItemSEContainer> predicate) {
        NonNullList<ItemStack> list = NonNullList.create();
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (isValidStack(stack, predicate)) {
                list.add(stack);
            }
        }
        for (ItemStack stack : Iterables.concat(InventoryUtils.getHotbar(player.inventory), player.inventory.armor)) {
            if (isValidStack(stack, predicate)) {
                list.add(stack);
            }
        }
        for (ItemStack stack : player.inventory.items) {
            if (isValidStack(stack, predicate)) {
                list.add(stack);
            }
        }
        return list.stream().distinct().collect(CollectionUtils.toNonNullList(ItemStack.EMPTY));
    }

    private static boolean isValidStack(ItemStack stack, BiPredicate<ItemStack, ItemSEContainer> predicate) {
        if (!SoulEnergies.of(stack).isPresent()) {
            return false;
        }
        return stack.getItem() instanceof ItemSEContainer && ((ItemSEContainer) stack.getItem()).isTransferable(stack) && predicate.test(stack, (SEGemItem) stack.getItem());
    }

    public static List<ItemStack> checkExtraction(PlayerEntity player, int amount) {
        return checkExtraction(getSEContainers(player), amount);
    }

    public static List<ItemStack> checkExtraction(List<ItemStack> list, int amount) {
        List<ItemStack> storages = list.stream()
                .filter(stack -> SoulEnergies.of(stack).isPresent())
                .collect(Collectors.toList());
        return storages.stream().filter(stack -> of(stack).isPresent()).filter(stack -> of(stack).resolve().get().getMaxExtract() >= amount).collect(Collectors.toList());
    }

    public static boolean cost(PlayerEntity player, int amount) {
        return cost(getSEContainers(player), amount);
    }

    public static boolean cost(PlayerEntity player, int amount, Runnable ifSucceeded, Runnable ifFailed) {
        return cost(player, amount, DefaultValues.dummyConsumer(), ifSucceeded, ifFailed);
    }

    public static boolean cost(PlayerEntity player, int amount, Consumer<? super ItemStack> ifSuccessfullyCostAStack, Runnable ifSucceeded, Runnable ifFailed) {
        return cost(getSEContainers(player), ifSuccessfullyCostAStack, ifSucceeded, ifFailed, new AtomicInteger(amount));
    }

    public static boolean cost(List<? extends ItemStack> list, int amount) {
        return cost(list, amount, DefaultValues.dummyConsumer(), DefaultValues.dummyRunnable(), DefaultValues.dummyRunnable());
    }

    public static boolean cost(List<? extends ItemStack> list, int amount, Runnable ifSucceeded, Runnable ifFailed) {
        return cost(list, amount, DefaultValues.dummyConsumer(), ifSucceeded, ifFailed);
    }

    public static boolean cost(List<? extends ItemStack> list, int amount, Consumer<? super ItemStack> ifSuccessfullyCostAStack, Runnable ifSucceeded, Runnable ifFailed) {
        return cost(list, ifSuccessfullyCostAStack, ifSucceeded, ifFailed, new AtomicInteger(amount));
    }

    private static boolean cost(List<? extends ItemStack> list, Consumer<? super ItemStack> ifSuccessfullyCostAStack, Runnable ifSucceeded, Runnable ifFailed, AtomicInteger amountRemaining) {
        if (list.isEmpty()) {
            ifFailed.run();
            return false;
        }
        if (getExtractableSEOf(list) >= amountRemaining.get()) {
            for (ItemStack stack : list) {
                of(stack).ifPresent(ses -> amountRemaining.addAndGet(-ses.extractSoulEnergy(amountRemaining.get())));
                ifSuccessfullyCostAStack.accept(stack);
            }
            if (amountRemaining.get() != 0) {
                throw new IllegalStateException("AmountRemaining is non zero: " + amountRemaining.get());
            }
            ifSucceeded.run();
            return true;
        }
        ifFailed.run();
        return false;
    }

    public static int getStoredSEOf(List<? extends CapabilityProvider<?>> list) {
        return list.stream().mapToInt(provider -> of(provider).map(ISoulEnergyStorage::getSoulEnergyStored).orElse(0)).sum();
    }

    public static int getExtractableSEOf(List<? extends CapabilityProvider<?>> list) {
        return list.stream().mapToInt(provider -> of(provider).map(ses -> Math.min(ses.getMaxExtract(), ses.getSoulEnergyStored())).orElse(0)).sum();
    }
}
