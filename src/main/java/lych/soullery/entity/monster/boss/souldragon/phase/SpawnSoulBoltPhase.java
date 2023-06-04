package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.functional.SoulBoltEntity;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.util.PositionCalculators;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class SpawnSoulBoltPhase extends AttackPhase {
    private static final double SCALE = 7;
    private Vector3d position;
    private Vector3d srcVector;
    private Vector3d nextVector;

    public SpawnSoulBoltPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void begin() {
        super.begin();
        Vector3d vector = dragon.getViewVector(1);
        position = new Vector3d(calcHeadX(vector), 0, calcHeadZ(vector));
        srcVector = vector;
        updateNextPosition();
    }

    @Override
    protected void doAttack() {
        double x = position.x + nextVector.x;
        double z = position.z + nextVector.z;
        double y = PositionCalculators.heightmap((int) x, (int) z, level);
        SoulBoltEntity bolt = ModEntities.SOUL_BOLT.create(level);
        if (bolt != null) {
            bolt.setOwner(dragon);
            bolt.setDamage(10);
            bolt.setKnockbackStrength(0.75);
            bolt.moveTo(x, y, z);
            level.addFreshEntity(bolt);
        }
        updateNextPosition();
    }

    private void updateNextPosition() {
        nextVector = srcVector.scale(SCALE).scale(dragon.getAttackStep());
    }

    @Override
    public PhaseType<?> getPhase() {
        return PhaseType.SPAWN_SOUL_BOLT;
    }

    @Override
    protected int getMaxCharge(LivingEntity target) {
        return 5;
    }

    @Override
    protected int getMaxAttackStep(LivingEntity target) {
        return 10;
    }
}
