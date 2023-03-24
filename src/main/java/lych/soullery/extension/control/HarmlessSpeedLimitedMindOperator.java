package lych.soullery.extension.control;

import lych.soullery.extension.control.movement.DefaultMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class HarmlessSpeedLimitedMindOperator extends HarmlessMindOperator {
    public HarmlessSpeedLimitedMindOperator(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public HarmlessSpeedLimitedMindOperator(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MovementHandler<? super MobEntity> initMovementHandler() {
        return DefaultMovementHandler.SPEED_LIMITED;
    }
}
