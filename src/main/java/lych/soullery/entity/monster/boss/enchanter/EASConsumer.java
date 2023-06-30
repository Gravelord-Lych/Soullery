package lych.soullery.entity.monster.boss.enchanter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public interface EASConsumer {
    default void startApplyingTo(EnchantedArmorStandEntity eas) {
        EASTypes.defaultAttributesUpgrade(eas);
    }

    default void stopApplyingTo(EnchantedArmorStandEntity eas) {
        EASTypes.resetDefaultAttributes(eas);
    }

    void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target);

    void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount);

    default void onEASDie(EnchantedArmorStandEntity eas, DamageSource source) {}
}
