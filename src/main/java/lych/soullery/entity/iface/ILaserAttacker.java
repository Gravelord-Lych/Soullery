package lych.soullery.entity.iface;

import com.google.common.base.Preconditions;
import lych.soullery.extension.laser.LaserAttackResult;
import lych.soullery.extension.laser.LaserData;
import lych.soullery.extension.laser.LaserSource;
import lych.soullery.util.Lasers;
import lych.soullery.util.mixin.ICustomLaserUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(value = Dist.CLIENT, _interface = ICustomLaserUser.class)
public interface ILaserAttacker extends IRangedAttackMob, ICustomLaserUser {
    LaserData getLaserData(LivingEntity target);

    float getLaserDamage(LivingEntity target);

    Vector3d getAttackerPosition();

    Vector3d getTargetPosition(LivingEntity target);

    boolean isAttacking();

    void setAttacking(boolean attacking);

    void playLaserSound();

    @Deprecated
    @Override
    default void performRangedAttack(LivingEntity target, float power) {
        performLaserAttack(target, getTargetPosition(target), power);
    }

    @SuppressWarnings("UnusedReturnValue")
    default LaserAttackResult performLaserAttack(LivingEntity target, Vector3d trueTargetPosition, float power) {
        Preconditions.checkState(this instanceof MobEntity);
        World world = ((MobEntity) this).level;
        Vector3d position = getAttackerPosition();
        LaserSource source = getLaserData(target).create(position, world);
        Random random = ((MobEntity) this).getRandom();
        double deviation = getAttackDeviation(target);
        Vector3d offset = new Vector3d(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1).scale(deviation);
        Vector3d truePosition = trueTargetPosition.add(offset);
        LaserAttackResult result = source.attackAndStopIfHit(truePosition, getBreakThreshold(target, truePosition, power), this);
        float damage = getLaserDamage(target);
        if (damage > 0) {
            Lasers.hurtHitEntities(result, (MobEntity) this, damage);
        }
        if (world instanceof ServerWorld) {
            renderLaser(target, result);
        }
        postLaserAttack(target, result);
        playLaserSound();
        return result;
    }

    default void renderLaser(LivingEntity target, LaserAttackResult result) {
        Lasers.renderLaser(result, (Entity) this, getLaserRenderTickCount(target));
    }

    /**
     * @return The max distance to the target that will cause the laser to end. If negative, infinite or NaN,
     *         the return value will be ignored
     */
    default double getBreakThreshold(LivingEntity target, Vector3d truePosition, float power) {
        return Double.NaN;
    }

    default double getAttackDeviation(LivingEntity target) {
        return 0.5;
    }

    default int getLaserRenderTickCount(LivingEntity target) {
        return 20;
    }

    default void onLaserAttack(LivingEntity target, int attackTime) {}

    default void postLaserAttack(LivingEntity target, LaserAttackResult result) {}
}
