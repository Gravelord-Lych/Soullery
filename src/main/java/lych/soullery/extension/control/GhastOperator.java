package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.FlyerMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.util.Telepathy;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class GhastOperator extends MindOperator<GhastEntity> {
    private static final TargetFinder<? super GhastEntity> FINDER = new TelepathicTargetFinder(60, Math.PI / 6, Telepathy.DEFAULT_ANGLE_WEIGHT);

    public GhastOperator(ControllerType<GhastEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public GhastOperator(ControllerType<GhastEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super GhastEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super GhastEntity> initMovementHandler() {
        return FlyerMovementHandler.INSTANCE;
    }

    @Override
    protected RightClickHandler<? super GhastEntity> initRightClickHandler() {
        return new GhastRightClickHandler();
    }

    @Override
    protected TargetFinder<? super GhastEntity> initTargetFinder() {
        return FINDER;
    }
}
