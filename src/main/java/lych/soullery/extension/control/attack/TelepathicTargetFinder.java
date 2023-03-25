package lych.soullery.extension.control.attack;

import lych.soullery.util.Telepathy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

public class TelepathicTargetFinder implements TargetFinder<MobEntity> {
    private final double reachDistance;
    private final double attackAngle;
    private final double angleWeight;
    private final boolean metaphysical;

    public TelepathicTargetFinder(double reachDistance, double attackAngle, double angleWeight) {
        this(reachDistance, attackAngle, angleWeight, false);
    }

    public TelepathicTargetFinder(double reachDistance, double attackAngle, double angleWeight, boolean metaphysical) {
        this.reachDistance = reachDistance;
        this.attackAngle = attackAngle;
        this.angleWeight = angleWeight;
        this.metaphysical = metaphysical;
    }

    @Nullable
    @Override
    public LivingEntity findTarget(MobEntity operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        if (metaphysical) {
            return Telepathy.telepathicAttackFirstMetaphysically(LivingEntity.class, operatingMob, reachDistance, attackAngle, angleWeight, entity -> entity != player && entity.attackable());
        }
        return Telepathy.telepathicAttackFirst(LivingEntity.class, operatingMob, reachDistance, attackAngle, angleWeight, entity -> entity != player && entity.attackable());
    }
}
