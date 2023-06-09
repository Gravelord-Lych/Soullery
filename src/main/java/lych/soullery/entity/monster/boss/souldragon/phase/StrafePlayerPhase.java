package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.entity.projectile.SoulballEntity;
import lych.soullery.network.SoulDragonNetwork;
import lych.soullery.network.SoulDragonNetwork.Message;
import lych.soullery.network.SoulDragonNetwork.MessageType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.PacketDistributor;

public class StrafePlayerPhase extends AttackPhase {
    public StrafePlayerPhase(SoulDragonEntity dragon) {
        super(dragon);
    }

    @Override
    protected void doAttack() {
        Vector3d vector = dragon.getViewVector(1);
        double x = calcHeadX(vector);
        double y = calcHeadY();
        double z = calcHeadZ(vector);
        double tx = attackTarget.getX() - x;
        double ty = attackTarget.getY(0.5) - y;
        double tz = attackTarget.getZ() - z;
        if (!dragon.isSilent()) {
            SoulDragonNetwork.INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), new Message(MessageType.PLAY_SHOOT_SOUND, dragon.blockPosition(), dragon.getId(), dragon.isSilent(), dragon.isPurified()));
        }
        SoulballEntity soulball = new SoulballEntity(dragon, tx, ty, tz, level);
        soulball.moveTo(x, y, z, 0, 0);
        soulball.setPurified(dragon.isPurified());
        level.addFreshEntity(soulball);
    }

    @Override
    public PhaseType<StrafePlayerPhase> getPhase() {
        return PhaseType.STRAFE_PLAYER;
    }
}
