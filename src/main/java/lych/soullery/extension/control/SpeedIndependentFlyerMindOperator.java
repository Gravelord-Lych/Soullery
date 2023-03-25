package lych.soullery.extension.control;

import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.movement.SpeedIndependentFlyerMovementHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class SpeedIndependentFlyerMindOperator extends FlyerMindOperator {
    public SpeedIndependentFlyerMindOperator(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public SpeedIndependentFlyerMindOperator(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MovementHandler<? super MobEntity> initMovementHandler() {
        return SpeedIndependentFlyerMovementHandler.INSTANCE;
    }

    @Override
    protected void removeFrom(PlayerEntity player, MobEntity mob) {
        super.removeFrom(player, mob);
        mob.setNoGravity(false);
    }
}
