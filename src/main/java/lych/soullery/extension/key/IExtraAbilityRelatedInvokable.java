package lych.soullery.extension.key;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IExtraAbilityRelatedInvokable extends IConditionalInvokable {
    int SUCCESS = 1;
    int FAILURE = 0;

    @Override
    default void doAccept(ServerPlayerEntity player) {
        int cooldown = ((IPlayerEntityMixin) player).getAdditionalCooldowns().getCooldownRemaining(requiredAbility().getRegistryName());
        if (cooldown == 0 && getInvokeResult(player) == SUCCESS) {
            ((IPlayerEntityMixin) player).getAdditionalCooldowns().addCooldown(requiredAbility().getRegistryName(), getCooldown(player));
        }
    }

    @Override
    default boolean canUse(ServerPlayerEntity player) {
        return requiredAbility().isOn(player);
    }

    IExtraAbility requiredAbility();

    int getInvokeResult(ServerPlayerEntity player);

    int getCooldown(ServerPlayerEntity player);
}
