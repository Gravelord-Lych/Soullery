package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.Soullery;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.world.event.SoulDragonFight;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class ChargingPlayerPhase extends AbstractPhase {
    private Vector3d targetLocation;
    private int timeSinceCharge;

    public ChargingPlayerPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            Soullery.LOGGER.warn(SoulDragonFight.MARKER, "Aborting charge player as no target was set.");
            dragon.setPhase(PhaseType.DEFAULT);
        } else if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
            dragon.setPhase(PhaseType.DEFAULT);
        } else {
            double distanceSqr = targetLocation.distanceToSqr(dragon.getX(), dragon.getY(), dragon.getZ());
            if (distanceSqr < 10 * 10 || distanceSqr > 150 * 150 || dragon.horizontalCollision || dragon.verticalCollision) {
                ++timeSinceCharge;
            }
        }
    }

    @Override
    public void begin() {
        targetLocation = null;
        timeSinceCharge = 0;
    }

    public void setTarget(Vector3d targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public float getFlySpeed() {
        return 3;
    }

    @Override
    @Nullable
    public Vector3d getFlyTargetLocation() {
        return targetLocation;
    }

    @Override
    public PhaseType<ChargingPlayerPhase> getPhase() {
        return PhaseType.CHARGING_PLAYER;
    }
}
