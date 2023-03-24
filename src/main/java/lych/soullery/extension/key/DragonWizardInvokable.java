package lych.soullery.extension.key;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.entity.GravitationalDragonFireballEntity;
import lych.soullery.extension.ExtraAbility;
import net.minecraft.entity.player.ServerPlayerEntity;

public enum DragonWizardInvokable implements IExtraAbilityRelatedInvokable {
    INSTANCE;

    @Override
    public IExtraAbility requiredAbility() {
        return ExtraAbility.DRAGON_WIZARD;
    }

    @Override
    public int getInvokeResult(ServerPlayerEntity player) {
        GravitationalDragonFireballEntity fireball = new GravitationalDragonFireballEntity(player, player.level);
        fireball.shootFromRotation(player, player.xRot, player.yRot, 0, 0.75f, 1);
        if (player.getLevel().addFreshEntity(fireball)) {
            return SUCCESS;
        }
        return FAILURE;
    }

    @Override
    public int getCooldown(ServerPlayerEntity player) {
        return 100;
    }
}
