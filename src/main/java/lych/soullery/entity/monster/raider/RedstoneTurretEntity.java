package lych.soullery.entity.monster.raider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RedstoneTurretEntity extends AbstractRedstoneTurretEntity {
    public RedstoneTurretEntity(EntityType<? extends RedstoneTurretEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractRedstoneTurretEntity.createTurretAttributes()
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        ArrowEntity arrow = new ArrowEntity(level, this);
        double tx = target.getX() - getX();
        double ty = target.getY(0.3333333333333333) - arrow.getY();
        double tz = target.getZ() - getZ();
        double distance = MathHelper.sqrt(tx * tx + tz * tz);
        arrow.setPos(getX(), getY(0.8333333333333333), getZ());
        arrow.shoot(tx, ty + distance * 0.2, tz, 1.6f, (float) (20 - level.getDifficulty().getId() * 4));
        playSound(SoundEvents.DISPENSER_LAUNCH, 1, 1 / (getRandom().nextFloat() * 0.4f + 0.8f));
        level.addFreshEntity(arrow);
    }

    @Override
    protected int getAttackInterval() {
        return isElite() ? 20 : 40;
    }
}
