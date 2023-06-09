package lych.soullery.item;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;

public interface ISkillPerformable {
    boolean perform(ItemStack stack, ServerPlayerEntity player);

    default void performed(ItemStack stack, ServerPlayerEntity player) {
        player.swing(Hand.MAIN_HAND);
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
    }
}
