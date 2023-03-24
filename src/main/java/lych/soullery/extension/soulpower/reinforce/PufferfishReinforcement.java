package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;

import java.util.function.Predicate;

public class PufferfishReinforcement extends FishReinforcement {
    private static final double BASE_HURT_RANGE = 0.2;
    private static final float DAMAGE = 0.6666667f;

    public PufferfishReinforcement() {
        super(EntityType.PUFFERFISH);
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {
        super.onLivingTick(stack, entity, level);
        level = Math.min(MAX_LEVEL, level);
        for (LivingEntity otherEntity : entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(BASE_HURT_RANGE * level), EntityPredicates.NO_SPECTATORS.and(getAttackPredicate(entity)))) {
            otherEntity.hurt(DamageSource.thorns(entity), DAMAGE * level);
        }
    }

    private static Predicate<Entity> getAttackPredicate(LivingEntity entity) {
        return otherEntity -> (entity instanceof IMob) != (otherEntity instanceof IMob);
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }
}
