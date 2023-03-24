package lych.soullery.extension.key;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface IConditionalInvokable extends IInvokable {
    @Override
    default void onKeyPressed(ServerPlayerEntity player) {
        if (canUse(player)) {
            doAccept(player);
        }
    }

    boolean canUse(ServerPlayerEntity player);

    void doAccept(ServerPlayerEntity player);
}
