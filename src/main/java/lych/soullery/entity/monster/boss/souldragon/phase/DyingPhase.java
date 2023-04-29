package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap.Type;
import org.jetbrains.annotations.Nullable;

public class DyingPhase extends AbstractPhase {
    private Vector3d targetLocation;
    private int time;

    public DyingPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void doClientTick() {
        if (time++ % 10 == 0) {
            float rx = (dragon.getRandom().nextFloat() - 0.5f) * 8;
            float ry = (dragon.getRandom().nextFloat() - 0.5f) * 4;
            float rz = (dragon.getRandom().nextFloat() - 0.5f) * 8;
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, dragon.getX() + rx, dragon.getY() + 2 + ry, dragon.getZ() + rz, 0, 0, 0);
        }
    }

    @Override
    public void doServerTick() {
        time++;
        if (targetLocation == null) {
            BlockPos pos = level.getHeightmapPos(Type.MOTION_BLOCKING, dragon.getFightCenter());
            targetLocation = Vector3d.atBottomCenterOf(pos).add(0, 4, 0);
        }
        double distanceSqr = targetLocation.distanceToSqr(dragon.getX(), dragon.getY(), dragon.getZ());

        if (distanceSqr < 6 * 6 || distanceSqr > 150 * 150 || (dragon.horizontalCollision || dragon.verticalCollision)) {
            dragon.setHealth(0);
            targetLocation = null;
        } else {
            dragon.setHealth(1);
        }
    }

    @Override
    public void begin() {
        targetLocation = null;
        time = 0;
    }

    @Nullable
    @Override
    public Vector3d getFlyTargetLocation() {
        return targetLocation;
    }

    @Override
    public float getFlySpeed() {
        return 3;
    }

    @Override
    public PhaseType<DyingPhase> getPhase() {
        return PhaseType.DYING;
    }
}
