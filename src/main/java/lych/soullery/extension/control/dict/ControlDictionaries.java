package lych.soullery.extension.control.dict;

import lych.soullery.api.IStrongMinded;
import lych.soullery.extension.control.ControllerType;
import lych.soullery.extension.control.ICustomOperable;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.IMob;

public final class ControlDictionaries {
    public static final ControlDictionary CHAOS = DefaultedControlDictionary.withDefault(ControllerType.CHAOS)
            .doNotControlIf(ControlDictionaries::friendly)
            .build();
    public static final ControlDictionary MIND_OPERATOR = DefaultedControlDictionary.withDefault(ControllerType.DEFAULT_MO)
            .specify(EntityType.ENDERMAN, ControllerType.ENDERMAN_MO)
            .specify(EntityType.EVOKER, ControllerType.EVOKER_MO)
            .specify(EntityType.VEX, ControllerType.AGGRESSIVE_FLYER_MO)
            .specify(EntityType.GUARDIAN, ControllerType.GUARDIAN_MO)
            .specify(EntityType.ELDER_GUARDIAN, ControllerType.GUARDIAN_MO)
            .specify(EntityType.GHAST, ControllerType.GHAST_MO)
            .specify(EntityType.PHANTOM, ControllerType.AGGRESSIVE_FLYER_MO)
            .specify(EntityType.PARROT, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .specify(EntityType.BEE, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .specify(EntityType.BAT, ControllerType.FLYER_MO)
            .specify(EntityType.BLAZE, ControllerType.BLAZE_MO)
            .specify(EntityType.CREEPER, ControllerType.CREEPER_MO)
            .specify(EntityType.VILLAGER, ControllerType.HARMLESS_SPEED_LIMITED_MO)
            .specify(EntityType.SQUID, ControllerType.SQUID_MO)
            .specify(EntityType.SHULKER, ControllerType.SHULKER_MO)
            .specify(EntityType.DOLPHIN, ControllerType.SPEED_LIMITED_MO)
            .doNotControl(EntityType.BAT)
            .doNotControlIf(IStrongMinded::isStrongMinded)
            .addCondition(ControlDictionaries::custom, ControllerType.CUSTOM_MO)
            .addCondition(ControlDictionaries::guardian, ControllerType.GUARDIAN_MO)
            .addCondition(ControlDictionaries::watermob, ControllerType.WATER_MOB_MO)
            .addCondition(ControlDictionaries::speedIndependentlyFlyable, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .addCondition(ControlDictionaries::aggressiveFlyable, ControllerType.AGGRESSIVE_FLYER_MO)
            .addCondition(ControlDictionaries::harmlessFlyable, ControllerType.FLYER_MO)
            .addCondition(ControlDictionaries::harmlessUnflyable, ControllerType.HARMLESS_MO)
            .build();
    public static final ControlDictionary SOUL_PURIFIER = DefaultedControlDictionary.withDefault(ControllerType.SOUL_PURIFIER)
            .doNotControlIf(ControlDictionaries::friendly)
            .build();

    private ControlDictionaries() {}

    private static boolean harmlessUnflyable(MobEntity mob) {
        return mob.getAttribute(Attributes.ATTACK_DAMAGE) == null && mob.getAttribute(Attributes.FLYING_SPEED) == null && !(mob instanceof FlyingEntity) && !(mob instanceof IRangedAttackMob);
    }

    private static boolean speedIndependentlyFlyable(MobEntity mob) {
        return mob.getAttribute(Attributes.FLYING_SPEED) != null;
    }

    private static boolean harmlessFlyable(MobEntity mob) {
        return mob instanceof FlyingEntity && mob.getAttribute(Attributes.ATTACK_DAMAGE) == null && friendly(mob) && !(mob instanceof IRangedAttackMob);
    }

    private static boolean aggressiveFlyable(MobEntity mob) {
        return (mob.noPhysics || mob instanceof FlyingEntity) && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null;
    }

    private static boolean guardian(MobEntity mob) {
        return mob instanceof GuardianEntity;
    }

    private static boolean watermob(MobEntity mob) {
        return EntityUtils.isWaterMob(mob);
    }

    private static boolean custom(MobEntity mob) {
        return mob instanceof ICustomOperable<?>;
    }

    private static boolean friendly(MobEntity mob) {
        return !(mob instanceof IMob);
    }
}
