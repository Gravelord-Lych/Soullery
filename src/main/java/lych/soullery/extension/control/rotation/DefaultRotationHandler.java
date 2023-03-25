package lych.soullery.extension.control.rotation;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public enum DefaultRotationHandler implements RotationHandler<MobEntity> {
    INSTANCE;

    private static final float ROTATION_CONSTANT = 10;

    @Override
    public void handleRotation(MobEntity operatingMob, ServerPlayerEntity player, float rotationDelta, CompoundNBT data) {
        operatingMob.yRot = player.yRot + rotationDelta;
        operatingMob.yHeadRot = player.yHeadRot + rotationDelta;
        operatingMob.xRot = player.xRot;
        operatingMob.yBodyRot = player.yBodyRot + rotationDelta;
        EntityUtils.normalizeYRot(operatingMob);
        EntityUtils.normalizeYHeadRot(operatingMob);
        EntityUtils.normalizeYBodyRot(operatingMob);
    }
}
