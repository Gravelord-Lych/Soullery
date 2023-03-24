package lych.soullery.gui.container;

import lych.soullery.gui.container.slot.ExtraAbilityCarrierSlot;
import lych.soullery.item.ExtraAbilityCarrierItem;
import lych.soullery.item.ModItems;
import lych.soullery.util.InventoryUtils;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class ExtraAbilityContainer extends Container {
    public static final int START_X = 26;
    public static final int START_Y = 23;
    public static final int SPACING_X = 62;
    public static final int SPACING_Y = 30;
    public static final int ORDER_NUMBER_SPACING = 6;
    public static final int ORDER_NUMBER_OFFSET = 12;
    private final PlayerEntity player;
    private final IIntArray availableCount = new IntArray(1);

    public ExtraAbilityContainer(int id, PlayerInventory inventory, int availableCount) {
        super(ModContainers.EXA, id);
        ModContainers.addInventory(inventory, 8, 84, this::addSlot);
        this.player = inventory.player;
        this.availableCount.set(0, availableCount);
        ((IPlayerEntityMixin) inventory.player).getExtraAbilityCarrierInventory().setContainer(this);
        for (int i = 0; i < 3; i++) {
            addSlot(new ExtraAbilityCarrierSlot(((IPlayerEntityMixin) inventory.player).getExtraAbilityCarrierInventory(), i * 2, START_X + SPACING_X * i, START_Y, availableCount));
            addSlot(new ExtraAbilityCarrierSlot(((IPlayerEntityMixin) inventory.player).getExtraAbilityCarrierInventory(), i * 2 + 1, START_X + SPACING_X * i, START_Y + SPACING_Y, availableCount));
        }
    }

    @Override
    public void slotsChanged(IInventory inventory) {
        super.slotsChanged(inventory);
        for (ItemStack stack : InventoryUtils.listView(inventory)) {
            if (ExtraAbilityCarrierSlot.isValidCarrier(stack)) {
                ExtraAbilityCarrierItem.getExa(stack).addTo(player);
            }
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
        if (index < 27) {
            if (!moveItemStackTo(stack, 36, 42, false)) {
                if (!moveItemStackTo(stack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        } else if (index < 36) {
            if (!moveItemStackTo(stack, 36, 42, false)) {
                if (!moveItemStackTo(stack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        } else if (index < 42) {
            if (!moveItemStackTo(stack, 27, 36, false)) {
                if (!moveItemStackTo(stack, 0, 27, false)) {
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
    public boolean stillValid(PlayerEntity player) {
        boolean valid = player.getMainHandItem().getItem() == ModItems.EXTRA_ABILITY_WAND;
        if (!valid) {
            ((IPlayerEntityMixin) player).getExtraAbilityCarrierInventory().setContainer(null);
        }
        return valid;
    }

    public int getAvailableCount() {
        return availableCount.get(0);
    }
}
