package lych.soullery.entity.monster.raider;

import lych.soullery.entity.ai.goal.BombardGoal;
import lych.soullery.entity.projectile.RisingMortarShellEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class RedstoneMortarEntity extends AbstractRedstoneTurretEntity {
    public RedstoneMortarEntity(EntityType<? extends AbstractRedstoneTurretEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractRedstoneTurretEntity.createTurretAttributes()
                .add(Attributes.FOLLOW_RANGE, 80);
    }

    @Override
    protected void addRangedAttackGoal() {
        goalSelector.addGoal(1, new BombardGoal(this, 0, this::getAttackInterval, this::getAttackRadius, this::getAttackTimes, this::getAttackIntervalOfPerAttack));
    }

    @Override
    protected boolean mustSeeTarget() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && equals(source.getEntity())) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        bombard(target.position());
    }

    public void bombard(Vector3d targetPos) {
        targetPos = offset(targetPos);
        RisingMortarShellEntity shell = new RisingMortarShellEntity(targetPos, this, level);
        shell.setBurning(isElite() && random.nextDouble() < 0.1);
        shell.setPos(shell.getX(), getY(1), shell.getZ());
        playSound(SoundEvents.DISPENSER_LAUNCH, 3, 0.6f / (getRandom().nextFloat() * 0.4f + 0.8f));
        level.addFreshEntity(shell);
    }

    private Vector3d offset(Vector3d targetPos) {
        targetPos = targetPos.add(random.nextGaussian() * 2, random.nextGaussian() * 2, random.nextGaussian() * 2);
        return targetPos;
    }

    @Override
    protected int getAttackTimes() {
        return isElite() ? 3 : 1;
    }

    @Override
    protected float getAttackRadius() {
        return 80;
    }

    @Override
    protected int getAttackIntervalOfPerAttack() {
        return 10;
    }

    @Override
    protected int getAttackInterval() {
        return isElite() ? 100 : 80;
    }

    @Override
    protected int getMaxLife() {
        return (int) (super.getMaxLife() * 1.5f);
    }
}
