package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.DefaultMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.util.Telepathy;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class BlazeOperator extends MindOperator<BlazeEntity> {
    private static final TargetFinder<? super BlazeEntity> FINDER = new TelepathicTargetFinder(48, Math.PI / 6, Telepathy.DEFAULT_ANGLE_WEIGHT);

    public BlazeOperator(ControllerType<BlazeEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public BlazeOperator(ControllerType<BlazeEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super BlazeEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super BlazeEntity> initMovementHandler() {
        return DefaultMovementHandler.NORMAL;
    }

    @Override
    protected TargetFinder<? super BlazeEntity> initTargetFinder() {
        return FINDER;
    }

    @Override
    protected RightClickHandler<? super BlazeEntity> initRightClickHandler() {
        return new BlazeRightClickHandler();
    }
}
