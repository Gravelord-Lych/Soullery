package lych.soullery.item.potion;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.util.NonNullList;

public class HalfUsedSplashPotionItem extends SplashPotionItem implements IHalfUsedPotion {
    public HalfUsedSplashPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {}

    @Override
    public String getDescriptionId(ItemStack stack) {
        return IHalfUsedPotion.makeDescriptionId(stack, Items.SPLASH_POTION);
    }
}
