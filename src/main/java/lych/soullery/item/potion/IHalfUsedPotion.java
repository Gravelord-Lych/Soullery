package lych.soullery.item.potion;

import lych.soullery.extension.ExtraAbility;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public interface IHalfUsedPotion {
    static boolean canChemistApply(Object item, LivingEntity entity) {
        return entity instanceof PlayerEntity && ExtraAbility.CHEMIST.isOn((PlayerEntity) entity) && !(item instanceof IHalfUsedPotion);
    }

    static ItemStack createHalfUsedPotion(ItemStack stack, Item item) {
        ItemStack halfUsed = new ItemStack(item);
        PotionUtils.setPotion(halfUsed, PotionUtils.getPotion(stack));
        return halfUsed;
    }

    static String makeDescriptionId(ItemStack stack, Item item) {
        return item.getDescriptionId(stack);
    }
}
