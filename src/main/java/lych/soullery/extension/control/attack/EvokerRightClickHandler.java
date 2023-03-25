package lych.soullery.extension.control.attack;

import lych.soullery.util.DefaultValues;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class EvokerRightClickHandler implements TargetNotNeededRightClickHandler<EvokerEntity> {
    private int cooldown = 10;

    @Override
    public void handleRightClick(EvokerEntity operatingEvoker, ServerPlayerEntity player, CompoundNBT data) {
        if (cooldown > 0) {
            return;
        }
        Vector3d lookAngle = operatingEvoker.getLookAngle();
        spawnFangs(operatingEvoker.getX() + lookAngle.x, operatingEvoker.getY() + lookAngle.y, operatingEvoker.getZ() + lookAngle.z, operatingEvoker, false);
    }

    @Override
    public void handleRightClick(EvokerEntity operatingEvoker, LivingEntity target, ServerPlayerEntity player, CompoundNBT data) {
        if (cooldown > 0) {
            return;
        }
        spawnFangs(target.getX(), target.getY(), target.getZ(), operatingEvoker, operatingEvoker.distanceToSqr(target) < 3 * 3);
    }

    private void spawnFangs(double tx, double ty, double tz, EvokerEntity operatingEvoker, boolean circle) {
        double minY = Math.min(ty, operatingEvoker.getY());
        double maxY = Math.max(ty, operatingEvoker.getY()) + 1;
        float angle = (float)MathHelper.atan2(tz - operatingEvoker.getZ(), tx - operatingEvoker.getX());

        if (circle) {
            for (int i = 0; i < 5; ++i) {
                float newAngle = angle + (float) i * (float) Math.PI * 0.4f;
                createFangs(operatingEvoker.getX() +  MathHelper.cos(newAngle) * 1.5, operatingEvoker.getZ() + MathHelper.sin(newAngle) * 1.5, minY, maxY, newAngle, 0, operatingEvoker);
            }
            for (int i = 0; i < 8; ++i) {
                float newAngle = angle + (float) i * (float) Math.PI * 2 / 8 + 1.2566371f;
                createFangs(operatingEvoker.getX() + MathHelper.cos(newAngle) * 2.5, operatingEvoker.getZ() + MathHelper.sin(newAngle) * 2.5, minY, maxY, newAngle, 3, operatingEvoker);
            }
        } else {
            for (int i = 0; i < 16; ++i) {
                double length = 1.25 * (i + 1);
                createFangs(operatingEvoker.getX() + (double)MathHelper.cos(angle) * length, operatingEvoker.getZ() + (double)MathHelper.sin(angle) * length, minY, maxY, angle, i, operatingEvoker);
            }
        }

        cooldown = 40;
    }

    @Override
    public void tick(EvokerEntity operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        TargetNotNeededRightClickHandler.super.tick(operatingMob, player, data);
        if (cooldown > 0) {
            cooldown--;
        }
    }

    private void createFangs(double x, double z, double minY, double maxY, float angle, int warmupDelayTicks, EvokerEntity evoker) {
        EntityUtils.createFangs(x, z, minY, maxY, angle, warmupDelayTicks, evoker, evoker.level, DefaultValues.dummyConsumer());
    }
}
