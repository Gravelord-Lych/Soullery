package lych.soullery.gui.container.slot;

import lych.soullery.item.SoulContainerItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SoulContainerSlot extends Slot {
    public SoulContainerSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && stack.getItem() instanceof SoulContainerItem;
    }
}
