package lych.soullery.gui.container;

import lych.soullery.api.capability.ISoulEnergyStorage;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.block.ModBlocks;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper.ApplicationStatus;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.gui.container.inventory.SoulReinforcementTableIngredientInventory;
import lych.soullery.gui.container.slot.SEContainerSlot;
import lych.soullery.gui.container.slot.SoulContainerSlot;
import lych.soullery.gui.container.slot.SoulReinforcementResultSlot;
import lych.soullery.item.ExtraAbilityCarrierItem;
import lych.soullery.item.SoulContainerItem;
import lych.soullery.util.SoulEnergies;
import lych.soullery.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class SoulReinforcementTableContainer extends Container {
    private static final int INGREDIENT_X = 14;
    private static final int INGREDIENT_Y = 49;
    private static final int CONTAINER_X = 61;
    private static final int CONTAINER_Y = 49;
    private static final int GEM_X = 81;
    private static final int GEM_Y = 49;
    private static final int RESULT_X = 142;
    private static final int RESULT_Y = 49;
    public static final int PROGRESS = 0;
    public static final int TOTAL = 1;
    public static final int COST = 2;
    public static final int STATUS = 3;
    public static final int OK = 4;
    private final SoulReinforcementTableIngredientInventory ingredientSlots = new SoulReinforcementTableIngredientInventory(this, 3);
    private final Inventory resultSlots = new Inventory(1);
    private final IIntArray seProgress;
    private final IWorldPosCallable access;

    public SoulReinforcementTableContainer(int id, PlayerInventory inventory, IIntArray seProgress) {
        this(id, inventory, seProgress, IWorldPosCallable.NULL);
    }

    public SoulReinforcementTableContainer(int id, PlayerInventory inventory, IIntArray seProgress, IWorldPosCallable access) {
        super(ModContainers.SOUL_REINFORCEMENT_TABLE, id);
        this.seProgress = seProgress;
        this.access = access;
        addSlot(new Slot(ingredientSlots, 0, INGREDIENT_X, INGREDIENT_Y));
        addSlot(new SoulContainerSlot(ingredientSlots, 1, CONTAINER_X, CONTAINER_Y));
        addSlot(new SEContainerSlot(ingredientSlots, 2, GEM_X, GEM_Y));
        addSlot(new SoulReinforcementResultSlot(resultSlots, ingredientSlots, 0, RESULT_X, RESULT_Y));
        ModContainers.addInventory(inventory, 8, 84, this::addSlot);
        addDataSlots(seProgress);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSEProgress() {
        final int length = 158;

        int progress = seProgress.get(PROGRESS);
        int total = seProgress.get(TOTAL);
        return total != 0 && progress != 0 ? progress * length / total : 0;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(access, player, ModBlocks.SOUL_REINFORCEMENT_TABLE);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void slotsChanged(IInventory inventory) {
        super.slotsChanged(inventory);
        access.execute((world, pos) -> onSlotsChanged(world, inventory));
    }

    protected void onSlotsChanged(World world, IInventory inventory) {
        if (!world.isClientSide) {
            ApplicationStatus status = null;
            ItemStack stack = ItemStack.EMPTY;
            int energy;
            int energyCost = 0;

            if (inventory.getItem(1).getItem() instanceof SoulContainerItem) {
                EntityType<?> type = SoulContainerItem.getType(inventory.getItem(1));
                if (type != null) {
                    if (inventory.getItem(0).getItem() instanceof ExtraAbilityCarrierItem) {
                        if (ExtraAbilityCarrierItem.getExa(inventory.getItem(0)) == null) {
                            IExtraAbility exa = ExtraAbility.byEntity(type);
                            if (exa != null) {
                                ItemStack copy = inventory.getItem(0).copy();
                                int count = inventory.getItem(1).getCount();
                                ISoulEnergyStorage ses = SoulEnergies.of(ingredientSlots.getItem(2)).resolve().orElse(null);

                                if (ses != null && count >= exa.getSoulContainerCost()) {
                                    int maxExtract = ses.getMaxExtract();
                                    energy = ses.getSoulEnergyStored();
                                    energyCost = exa.getSECost();

                                    boolean canCostEnergy = energy >= energyCost && energy <= maxExtract;
                                    if (canCostEnergy) {
                                        ExtraAbilityCarrierItem.setExa(copy, exa);
                                        stack = copy;
                                    }
                                }
                            }
                        }
                    } else {
                        Reinforcement reinforcement = Reinforcements.get(type);
                        if (reinforcement != null) {
                            ItemStack copy = inventory.getItem(0).copy();
                            int count = inventory.getItem(1).getCount();
                            int oldLevel = ReinforcementHelper.getReinforcementLevel(copy, reinforcement);
                            int newLevel = reinforcement.getUpgradeableNewLevel(count, oldLevel);

                            if (newLevel > reinforcement.getMaxLevel()) {
                                newLevel = Math.min(newLevel, reinforcement.getMaxLevel());
                            }

                            ISoulEnergyStorage ses = SoulEnergies.of(ingredientSlots.getItem(2)).resolve().orElse(null);
                            if (ses != null) {
                                int maxExtract = ses.getMaxExtract();
                                energy = ses.getSoulEnergyStored();
                                energyCost = Reinforcements.getEnergyCost(reinforcement, oldLevel, newLevel);

                                while (energy < energyCost) {
                                    newLevel--;
                                    if (newLevel <= oldLevel) {
                                        break;
                                    }
                                    energyCost = Reinforcements.getEnergyCost(reinforcement, oldLevel, newLevel);
                                }

                                if (oldLevel >= reinforcement.getMaxLevel()) {
                                    status = ApplicationStatus.LEVEL_TOO_HIGH;
                                } else {
                                    status = ReinforcementHelper.addOrUpgradeReinforcement(copy, reinforcement, newLevel);
                                }

                                boolean canCostEnergy = energy >= energyCost && energy <= maxExtract;
                                if (newLevel > oldLevel && canCostEnergy && status.isOk()) {
                                    stack = copy;
                                }
                            }
                        }
                    }
                }
            }
            resultSlots.setItem(0, stack);

            final int finalEnergyCost = energyCost;
            Optional<ISoulEnergyStorage> optional = SoulEnergies.of(ingredientSlots.getItem(2)).resolve();
            if (optional.isPresent()) {
                ISoulEnergyStorage ses = optional.get();
                seProgress.set(PROGRESS, ses.getSoulEnergyStored());
                seProgress.set(TOTAL, ses.getMaxSoulEnergyStored());
            } else {
                seProgress.set(PROGRESS, 0);
                seProgress.set(TOTAL, 0);
            }

            seProgress.set(STATUS, Utils.getOrDefault(status, -1, Enum::ordinal));
            if (!ingredientSlots.getItem(0).isEmpty() && finalEnergyCost > 0) {
                seProgress.set(COST, finalEnergyCost);
                seProgress.set(OK, stack.isEmpty() ? 0 : 1);
            } else {
                seProgress.set(COST, -1);
                seProgress.set(OK, 0);
            }

            broadcastChanges();
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int ingredientSlotIndex = 0;
        int soulContainerIndex = 1;
        int soulEnergyContainerIndex = 2;
        int resultIndex = 3;
        if (index == ingredientSlotIndex || index == soulContainerIndex || index == soulEnergyContainerIndex) {
            if (!moveItemStackTo(stack, 28 + resultIndex, 37 + resultIndex, false)) {
                if (!moveItemStackTo(stack, 1 + resultIndex, 28 + resultIndex, false)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        } else if (index <= 27 + resultIndex) {
            if (!moveItemStackTo(stack, 0, resultIndex + 1, true)) {
                if (!moveItemStackTo(stack, 28 + resultIndex, 37 + resultIndex, false)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        } else if (index <= 36 + resultIndex) {
            if (!moveItemStackTo(stack, 0, resultIndex + 1, true)) {
                if (!moveItemStackTo(stack, 1 + resultIndex, 28 + resultIndex, false)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        }
        if (stack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, copy);
        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        access.execute((world, pos) -> clearContainer(player, world, ingredientSlots));
    }

    @OnlyIn(Dist.CLIENT)
    public IIntArray getSEProgressArray() {
        return seProgress;
    }
}
