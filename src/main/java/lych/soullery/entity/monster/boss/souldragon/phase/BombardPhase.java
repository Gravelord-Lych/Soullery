package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.entity.projectile.SoulballEntity;
import lych.soullery.network.SoulDragonNetwork;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.PacketDistributor;

public class BombardPhase extends AttackPhase {
    public BombardPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    protected void doAttack() {
        for (int i = 0; i < 2; i++) {
            Vector3d vector = dragon.getViewVector(1);
            double x = calcHeadX(vector);
            double y = calcHeadY();
            double z = calcHeadZ(vector);
            double tx = attackTarget.getX() - x + dragon.getRandom().nextGaussian();
            double ty = attackTarget.getY(0.5) - y + dragon.getRandom().nextGaussian();
            double tz = attackTarget.getZ() - z + dragon.getRandom().nextGaussian();
            SoulDragonNetwork.INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), new SoulDragonNetwork.Message(SoulDragonNetwork.MessageType.PLAY_SHOOT_SOUND, dragon.blockPosition(), dragon.getId(), dragon.isSilent(), dragon.isPurified()));
            SoulballEntity soulball = new SoulballEntity(dragon, tx, ty, tz, level);
            soulball.moveTo(x, y, z, 0, 0);
            soulball.setUsedForBombard(true);
            soulball.setPurified(dragon.isPurified());
            level.addFreshEntity(soulball);
        }
    }

    @Override
    public double getWeightMultiplier(LivingEntity target) {
        double distance = dragon.distanceTo(target);
        return Math.min(1, 1 + (distance - 20) * 0.07);
    }

    @Override
    protected int getMaxCharge(LivingEntity target) {
        return 10;
    }

    @Override
    protected int getMaxAttackStep(LivingEntity target) {
        return dragon.getRandom().nextInt(4) + 6;
    }

    @Override
    public PhaseType<BombardPhase> getPhase() {
        return PhaseType.BOMBARD;
    }
}
