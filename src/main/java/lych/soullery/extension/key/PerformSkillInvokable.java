package lych.soullery.extension.key;

import lych.soullery.item.ISkillPerformable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public enum PerformSkillInvokable implements IInvokable {
    INSTANCE;

    @Override
    public void onKeyPressed(ServerPlayerEntity player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ISkillPerformable && ((ISkillPerformable) stack.getItem()).perform(stack, player)) {
            ((ISkillPerformable) stack.getItem()).performed(stack, player);
        }
    }
}
