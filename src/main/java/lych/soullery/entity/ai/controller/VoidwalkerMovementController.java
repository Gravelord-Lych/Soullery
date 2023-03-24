package lych.soullery.entity.ai.controller;

import lych.soullery.entity.iface.IEtherealable;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Objects;

public class VoidwalkerMovementController<T extends MobEntity & IEtherealable> extends MovementController {
    private final T mob;

    public VoidwalkerMovementController(T mob) {
        super(mob);
        this.mob = mob;
        speedModifier = 1;
    }

    @Override
    public void tick() {
        if (mob.isEthereal()) {
            Vector3d sneakTarget = mob.getSneakTarget();
            Objects.requireNonNull(sneakTarget);
            operation = Action.WAIT;
            double tx = sneakTarget.x - mob.getX();
            double ty = sneakTarget.y - mob.getY();
            double tz = sneakTarget.z - mob.getZ();
            Vector3d vec = new Vector3d(tx, ty, tz);
            double length = vec.length();
            if (length < mob.getSizeForCalculation()) {
                operation = Action.WAIT;
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.5));
                mob.onReachedSneakTarget(sneakTarget);
                mob.setSneakTarget(null);
                if (!mob.isInAir()) {
                    mob.floatToAir(new BlockPos(sneakTarget));
                }
            } else {
                updateBodyAngles(tx, ty, tz);
                mob.setDeltaMovement(vec.scale(speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED) / length));
            }
        } else {
            super.tick();
        }
    }

    private void updateBodyAngles(double tx, double ty, double tz) {
        AbstractVoidwalkerEntity.updateBodyAngles(tx, ty, tz, mob, this::rotlerp);
    }

    public void setSpeedModifier(double speedModifier) {
        this.speedModifier = speedModifier;
    }
}
