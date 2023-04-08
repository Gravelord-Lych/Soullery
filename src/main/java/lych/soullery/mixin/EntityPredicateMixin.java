package lych.soullery.mixin;

import lych.soullery.util.mixin.IEntityPredicateMixin;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(EntityPredicate.class)
public abstract class EntityPredicateMixin implements IEntityPredicateMixin {
    @Shadow private double range;

    @Shadow private boolean testInvisible;

    @Shadow private boolean allowNonAttackable;

    @Shadow private boolean allowUnseeable;

    @Shadow private boolean allowSameTeam;

    @Shadow private boolean allowInvulnerable;

    @Shadow private Predicate<LivingEntity> selector;

    @Override
    public EntityPredicate copy() {
        EntityPredicate newPredicate = new EntityPredicate().range(range);
        if (allowInvulnerable) {
            newPredicate.allowInvulnerable();
        }
        if (allowSameTeam) {
            newPredicate.allowSameTeam();
        }
        if (allowUnseeable) {
            newPredicate.allowUnseeable();
        }
        if (allowNonAttackable) {
            newPredicate.allowNonAttackable();
        }
        if (!testInvisible) {
            newPredicate.ignoreInvisibilityTesting();
        }
        newPredicate.selector(selector);
        return newPredicate;
    }
}
