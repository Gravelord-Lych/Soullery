package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.DefaultMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class CreeperOperator extends MindOperator<CreeperEntity> {
    public CreeperOperator(ControllerType<CreeperEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public CreeperOperator(ControllerType<CreeperEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super CreeperEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super CreeperEntity> initMovementHandler() {
        return DefaultMovementHandler.NORMAL;
    }

    @Override
    protected TargetFinder<? super CreeperEntity> initTargetFinder() {
        return NoTargetFinder.INSTANCE;
    }

    @Override
    protected RightClickHandler<? super CreeperEntity> initRightClickHandler() {
        return CreeperRightClickHandler.INSTANCE;
    }
}
