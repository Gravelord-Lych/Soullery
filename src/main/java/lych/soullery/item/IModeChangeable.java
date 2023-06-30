package lych.soullery.item;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public interface IModeChangeable {
    void changeMode(ItemStack stack, ServerPlayerEntity player, boolean reverse);
}
