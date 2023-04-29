package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class HoveringPhase extends AbstractPhase {
    private Vector3d targetLocation;

    public HoveringPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void doServerTick() {
        if (targetLocation == null) {
            targetLocation = dragon.position();
        }
    }

    @Override
    public void begin() {
        targetLocation = null;
    }

    @Override
    public float getFlySpeed() {
        return 1;
    }

    @Override
    @Nullable
    public Vector3d getFlyTargetLocation() {
        return targetLocation;
    }

    @Override
    public PhaseType<HoveringPhase> getPhase() {
        return PhaseType.HOVERING;
    }
}
