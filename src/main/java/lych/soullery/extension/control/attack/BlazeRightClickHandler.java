package lych.soullery.extension.control.attack;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

public class BlazeRightClickHandler implements TargetNotNeededRightClickHandler<BlazeEntity> {
    private int cooldown;

    @Override
    public void tick(BlazeEntity operatingMob, ServerPlayerEntity player) {
        TargetNotNeededRightClickHandler.super.tick(operatingMob, player);
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public void handleRightClick(BlazeEntity operatingBlaze, ServerPlayerEntity player) {
        handleRightClick(operatingBlaze, operatingBlaze.getLookAngle(), 16 * 16);
    }

    @Override
    public void handleRightClick(BlazeEntity operatingBlaze, LivingEntity target, ServerPlayerEntity player) {
        handleRightClick(operatingBlaze, EntityUtils.centerOf(operatingBlaze).vectorTo(EntityUtils.centerOf(target)), operatingBlaze.distanceToSqr(target));
    }

    private void handleRightClick(BlazeEntity operatingBlaze, Vector3d direction, double distSqr) {
        if (cooldown > 0) {
            return;
        }
        if (direction.lengthSqr() < 4 * 4) {
            direction = direction.normalize().scale(4);
        }
        double x = direction.x;
        double y = direction.y;
        double z = direction.z;
        float deviation = MathHelper.sqrt(MathHelper.sqrt(distSqr)) * 0.35f;
        SmallFireballEntity fireball = new SmallFireballEntity(operatingBlaze.level, operatingBlaze, x + operatingBlaze.getRandom().nextGaussian() * deviation, y, z + operatingBlaze.getRandom().nextGaussian() * deviation);
        fireball.setPos(fireball.getX(), operatingBlaze.getY(0.5) + 0.5, fireball.getZ());
        operatingBlaze.level.addFreshEntity(fireball);
        if (!operatingBlaze.isSilent()) {
            operatingBlaze.level.levelEvent(null, Constants.WorldEvents.BLAZE_SHOOT_SOUND, operatingBlaze.blockPosition(), 0);
        }
        cooldown = 5;
    }
}
