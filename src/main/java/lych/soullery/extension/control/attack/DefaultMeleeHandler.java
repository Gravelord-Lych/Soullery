package lych.soullery.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;

public enum DefaultMeleeHandler implements MeleeHandler<MobEntity> {
    INSTANCE;

    @Override
    public void handleMeleeAttack(MobEntity operatingMob, LivingEntity target, ServerPlayerEntity player) {
        operatingMob.swing(Hand.MAIN_HAND);
        operatingMob.doHurtTarget(target);
    }
}
