package lych.soullery.gui.container.slot;

import lych.soullery.api.ItemSEContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SEContainerSlot extends Slot {
    public SEContainerSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && stack.getItem() instanceof ItemSEContainer && ((ItemSEContainer) stack.getItem()).isTransferable(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
