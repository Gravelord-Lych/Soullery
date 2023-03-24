package lych.soullery.extension.control.rotation;

import lych.soullery.util.EntityUtils;
import lych.soullery.util.Vectors;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public enum DefaultRotationHandler implements RotationHandler<MobEntity> {
    INSTANCE;

    private static final float ROTATION_CONSTANT = 10;

    @Override
    public void handleRotation(MobEntity operatingMob, ServerPlayerEntity player, float rotationDelta) {
        operatingMob.yRot = player.yRot + rotationDelta;
        operatingMob.yHeadRot = player.yHeadRot + rotationDelta;
        operatingMob.xRot = player.xRot;
        operatingMob.yBodyRot = player.yBodyRot + rotationDelta;
        EntityUtils.normalizeYRot(operatingMob);
        EntityUtils.normalizeYHeadRot(operatingMob);
        EntityUtils.normalizeYBodyRot(operatingMob);
    }

    @Override
    public float handleRotationOffset(MobEntity operatingMob, ServerPlayerEntity player, double scroll) {
        float rotationDelta = (float) (scroll * ROTATION_CONSTANT);
        Vector3d lookAngle = operatingMob.getLookAngle();
        lookAngle = Vectors.rotateTo(lookAngle, Math.toRadians(rotationDelta), true);
        operatingMob.lookAt(EntityAnchorArgument.Type.EYES, lookAngle);
//        operatingMob.yRot += rotationDelta;
//        operatingMob.yHeadRot += rotationDelta;
//        operatingMob.yBodyRot += rotationDelta;
//        normalizeYRot(operatingMob);
//        normalizeYHeadRot(operatingMob);
//        normalizeYBodyRot(operatingMob);
        return rotationDelta;
    }

}
