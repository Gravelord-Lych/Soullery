package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.client.particle.ModParticles;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap.Type;

import javax.annotation.Nullable;
import java.util.Random;

public class LandingPhase extends AbstractPhase {
    private Vector3d targetLocation;

    public LandingPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void doClientTick() {
        Vector3d lookVec = this.dragon.getHeadLookVector(1.0F).normalize();
        lookVec.yRot(-(float) Math.PI / 4F);
        double x = dragon.getHead().getX();
        double y = dragon.getHead().getY(0.5D);
        double z = dragon.getHead().getZ();

        for (int i = 0; i < 8; i++) {
            Random random = dragon.getRandom();
            double rx = x + random.nextGaussian() / 2;
            double ry = y + random.nextGaussian() / 2;
            double rz = z + random.nextGaussian() / 2;
            Vector3d movement = dragon.getDeltaMovement();
            level.addParticle(dragon.isPurified() ? ModParticles.SOUL_DRAGON_BREATH_PURE : ModParticles.SOUL_DRAGON_BREATH, rx, ry, rz, -lookVec.x * 0.08 + movement.x, -lookVec.y * 0.3 + movement.y, -lookVec.z * 0.08 + movement.z);
            lookVec.yRot(0.19634955f);
        }
    }

    @Override
    public void doServerTick() {
        if (targetLocation == null) {
            targetLocation = Vector3d.atBottomCenterOf(level.getHeightmapPos(Type.MOTION_BLOCKING_NO_LEAVES, dragon.getFightCenter()));
        }
        if (targetLocation.distanceToSqr(dragon.getX(), dragon.getY(), dragon.getZ()) < 1) {
            dragon.setPhase(PhaseType.DEFAULT);
        }
    }

    @Override
    public float getFlySpeed() {
        return 1.5F;
    }

    @Override
    public float getTurnSpeed() {
        float f = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(this.dragon.getDeltaMovement())) + 1.0F;
        float f1 = Math.min(f, 40.0F);
        return f1 / f;
    }

    @Override
    public void begin() {
        this.targetLocation = null;
    }

    @Override
    @Nullable
    public Vector3d getFlyTargetLocation() {
        return targetLocation;
    }

    @Override
    public PhaseType<LandingPhase> getPhase() {
        return PhaseType.LANDING;
    }
}
