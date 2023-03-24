package lych.soullery.extension.control.dict;

import lych.soullery.extension.control.ControllerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.IMob;

public final class ControlDictionaries {
    public static final ControlDictionary MIND_OPERATOR = DefaultedControlDictionary.withDefault(ControllerType.DEFAULT_MO)
            .specify(EntityType.EVOKER, ControllerType.SPEED_LIMITED_MO)
            .specify(EntityType.ENDERMAN, ControllerType.ENDERMAN_MO)
            .specify(EntityType.VEX, ControllerType.AGGRESSIVE_FLYER_MO)
            .specify(EntityType.GHAST, ControllerType.GHAST_MO)
            .specify(EntityType.PHANTOM, ControllerType.AGGRESSIVE_FLYER_MO)
            .specify(EntityType.PARROT, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .specify(EntityType.BEE, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .specify(EntityType.BAT, ControllerType.FLYER_MO)
            .specify(EntityType.BLAZE, ControllerType.BLAZE_MO)
            .specify(EntityType.VILLAGER, ControllerType.HARMLESS_SPEED_LIMITED_MO)
            .addCondition(ControlDictionaries::speedIndependentlyFlyable, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .addCondition(ControlDictionaries::aggressiveFlyable, ControllerType.AGGRESSIVE_FLYER_MO)
            .addCondition(ControlDictionaries::harmlessFlyable, ControllerType.FLYER_MO)
            .addCondition(ControlDictionaries::harmlessUnflyable, ControllerType.HARMLESS_MO)
            .build();

    private ControlDictionaries() {}

    private static boolean harmlessUnflyable(MobEntity mob) {
        return mob.getAttribute(Attributes.ATTACK_DAMAGE) == null && mob.getAttribute(Attributes.FLYING_SPEED) == null && !(mob instanceof FlyingEntity);
    }

    private static boolean speedIndependentlyFlyable(MobEntity mob) {
        return mob.getAttribute(Attributes.FLYING_SPEED) != null;
    }

    private static boolean harmlessFlyable(MobEntity mob) {
        return mob instanceof FlyingEntity && !(mob instanceof IMob);
    }

    private static boolean aggressiveFlyable(MobEntity mob) {
        return (mob.noPhysics || mob instanceof FlyingEntity) && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null;
    }
}
