package lych.soullery.extension.key;

import net.minecraft.entity.player.ServerPlayerEntity;

@FunctionalInterface
public interface IInvokable {
    void onKeyPressed(ServerPlayerEntity player);
}
