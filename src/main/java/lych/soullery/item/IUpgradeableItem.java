package lych.soullery.item;

import net.minecraft.item.ItemStack;

public interface IUpgradeableItem {
    boolean canUpgrade(ItemStack stack);

    ItemStack upgraded(ItemStack old);

    default void checkUpgradeable(ItemStack stack) {
        if (!canUpgrade(stack)) {
            throw new UnsupportedOperationException();
        }
    }
}
