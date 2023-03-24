package lych.soullery.extension.control.attack;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

public class GhastRightClickHandler implements TargetNotNeededRightClickHandler<GhastEntity> {
    @Nullable
    private LivingEntity target;
    private int attackTime;
    private int cooldown = 20;

    @Override
    public void handleRightClick(GhastEntity operatingGhast, ServerPlayerEntity player) {
        if (cooldown != 0) {
            return;
        }
        cooldown = -1;
        attackTime = 10;
        operatingGhast.setCharging(true);
        if (!operatingGhast.isSilent()) {
            operatingGhast.level.levelEvent(null, Constants.WorldEvents.GHAST_WARN_SOUND, operatingGhast.blockPosition(), 0);
        }
    }

    @Override
    public void handleRightClick(GhastEntity operatingMob, LivingEntity target, ServerPlayerEntity player) {
        if (attackTime == 0 && cooldown == 0 && EntityUtils.isAlive(target)) {
            this.target = target;
        }
        TargetNotNeededRightClickHandler.super.handleRightClick(operatingMob, target, player);
    }

    @Override
    public void tick(GhastEntity operatingGhast, ServerPlayerEntity player) {
        TargetNotNeededRightClickHandler.super.tick(operatingGhast, player);
        if (cooldown >= 0) {
            if (cooldown > 0) {
                cooldown--;
            }
            return;
        }
        if (attackTime > 0) {
            attackTime--;
        } else {
            Vector3d viewVector = operatingGhast.getViewVector(1);
            double x, y, z;
            if (EntityUtils.isAlive(target)) {
                x = target.getX() - (operatingGhast.getX() + viewVector.x * 4.0D);
                y = target.getY(0.5) - (0.5 + operatingGhast.getY(0.5));
                z = target.getZ() - (operatingGhast.getZ() + viewVector.z * 4.0D);
                target = null;
            } else {
                x = viewVector.x;
                y = viewVector.y;
                z = viewVector.z;
            }
            World world = operatingGhast.level;
            if (!operatingGhast.isSilent()) {
                world.levelEvent(null, Constants.WorldEvents.GHAST_SHOOT_SOUND, operatingGhast.blockPosition(), 0);
            }
            FireballEntity fireball = new FireballEntity(world, operatingGhast, x, y, z);
            fireball.explosionPower = operatingGhast.getExplosionPower();
            fireball.setPos(operatingGhast.getX() + viewVector.x * 4, operatingGhast.getY(0.5) + 0.5, fireball.getZ() + viewVector.z * 4);
            world.addFreshEntity(fireball);
            operatingGhast.setCharging(false);
            cooldown = 40;
        }
    }
}
