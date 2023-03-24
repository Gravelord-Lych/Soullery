package lych.soullery.util.mixin;

import net.minecraft.item.ItemStack;

public interface IItemMixin {
    boolean isSoulFoil(ItemStack stack);

    default int getMaxReinforcementCount(ItemStack stack) {
        return 3;
    }
}
