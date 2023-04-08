package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.movement.NoMovementHandler;
import lych.soullery.util.Telepathy;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class ShulkerOperator extends MindOperator<ShulkerEntity> {
    private static final TargetFinder<? super ShulkerEntity> FINDER = new TelepathicTargetFinder(16, Math.PI / 2, Telepathy.DEFAULT_ANGLE_WEIGHT);

    public ShulkerOperator(ControllerType<ShulkerEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public ShulkerOperator(ControllerType<ShulkerEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super ShulkerEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super ShulkerEntity> initMovementHandler() {
        return NoMovementHandler.INSTANCE;
    }

    @Override
    protected RightClickHandler<? super ShulkerEntity> initRightClickHandler() {
        return new ShulkerRightClickHandler();
    }

    @Override
    protected TargetFinder<? super ShulkerEntity> initTargetFinder() {
        return FINDER;
    }
}
