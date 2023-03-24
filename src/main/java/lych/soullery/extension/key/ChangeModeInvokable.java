package lych.soullery.extension.key;

import lych.soullery.item.IModeChangeable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public enum ChangeModeInvokable implements IInvokable {
    INSTANCE;

    @Override
    public void onKeyPressed(ServerPlayerEntity player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof IModeChangeable) {
            ((IModeChangeable) stack.getItem()).changeMode(stack, player);
        }
    }
}
