package lych.soullery.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

public class SoulContainerItem extends SoulPieceItem {
    public SoulContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (getType(stack) != null) {
            return Rarity.EPIC;
        }
        return super.getRarity(stack);
    }
}
