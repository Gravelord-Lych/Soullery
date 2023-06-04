package lych.soullery.gui.container.slot;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.gui.container.ExtraAbilityContainer;
import lych.soullery.item.ExtraAbilityCarrierItem;
import lych.soullery.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ExtraAbilityCarrierSlot extends Slot {
    private final ExtraAbilityContainer container;
    private final int currentSlot;
    private final int maxAvailableSlot;

    public ExtraAbilityCarrierSlot(ExtraAbilityContainer container, int index, int x, int y, IInventory inventory, int maxAvailableSlot) {
        super(inventory, index, x, y);
        this.container = container;
        this.currentSlot = index;
        this.maxAvailableSlot = maxAvailableSlot;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (super.mayPlace(stack) && isValidCarrier(stack) && currentSlot < maxAvailableSlot) {
            return container.slots.stream()
                    .filter(slot -> slot instanceof ExtraAbilityCarrierSlot)
                    .filter(Slot::hasItem)
                    .map(Slot::getItem)
                    .filter(stackIn -> stackIn.getItem() instanceof ExtraAbilityCarrierItem)
                    .map(ExtraAbilityCarrierItem::getExa)
                    .noneMatch(Predicate.isEqual(ExtraAbilityCarrierItem.getExa(stack)));
        }
        return false;
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
