package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.MeleeHandler;
import lych.soullery.extension.control.attack.NoMeleeHandler;
import lych.soullery.extension.control.attack.NoTargetFinder;
import lych.soullery.extension.control.attack.TargetFinder;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.movement.SquidMovementHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class SquidOperator extends MindOperator<SquidEntity> {
    public SquidOperator(ControllerType<SquidEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public SquidOperator(ControllerType<SquidEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super SquidEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super SquidEntity> initMovementHandler() {
        return SquidMovementHandler.INSTANCE;
    }

    @Override
    protected TargetFinder<? super MobEntity> initTargetFinder() {
        return NoTargetFinder.INSTANCE;
    }
}
