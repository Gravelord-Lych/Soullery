package lych.soullery.gui.container.slot;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.item.ExtraAbilityCarrierItem;
import lych.soullery.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ExtraAbilityCarrierSlot extends Slot {
    private final int currentSlot;
    private final int maxAvailableSlot;

    public ExtraAbilityCarrierSlot(IInventory inventory, int index, int x, int y, int maxAvailableSlot) {
        super(inventory, index, x, y);
        this.currentSlot = index;
        this.maxAvailableSlot = maxAvailableSlot;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && isValidCarrier(stack) && currentSlot < maxAvailableSlot;
    }

    public static boolean isValidCarrier(ItemStack stack) {
        return stack.getItem() == ModItems.EXTRA_ABILITY_CARRIER && ExtraAbilityCarrierItem.getExa(stack) != null;
    }

    @Override
    public ItemStack onTake(PlayerEntity player, ItemStack stack) {
        IExtraAbility exa = ExtraAbilityCarrierItem.getExa(stack);
        if (exa == null) {
            throw new AssertionError();
        }
        exa.removeFrom(player);
        return super.onTake(player, stack);
    }
}
