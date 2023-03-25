package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.DefaultMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.util.Telepathy;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class EvokerOperator extends MindOperator<EvokerEntity> {
    private static final TargetFinder<MobEntity> FINDER = new TelepathicTargetFinder(12, Math.PI / 8, Telepathy.HIGH_ANGLE_WEIGHT);

    public EvokerOperator(ControllerType<EvokerEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public EvokerOperator(ControllerType<EvokerEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super EvokerEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super EvokerEntity> initMovementHandler() {
        return DefaultMovementHandler.SPEED_LIMITED;
    }

    @Override
    protected TargetFinder<? super EvokerEntity> initTargetFinder() {
        return FINDER;
    }

    @Override
    protected RightClickHandler<? super EvokerEntity> initRightClickHandler() {
        return new EvokerRightClickHandler();
    }
}
