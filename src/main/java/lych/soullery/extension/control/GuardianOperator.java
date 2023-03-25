package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.DefaultMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.mixin.GuardianEntityAccessor;
import lych.soullery.util.Telepathy;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class GuardianOperator extends MindOperator<GuardianEntity> {
    private static final TargetFinder<MobEntity> FINDER = new TelepathicTargetFinder(16, Math.PI / 3, Telepathy.HIGH_ANGLE_WEIGHT);

    public GuardianOperator(ControllerType<GuardianEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public GuardianOperator(ControllerType<GuardianEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super GuardianEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super GuardianEntity> initMovementHandler() {
        return DefaultMovementHandler.SPEED_LIMITED;
    }

    @Override
    protected RightClickHandler<? super GuardianEntity> initRightClickHandler() {
        return new GuardianRightClickHandler();
    }

    @Override
    protected TargetFinder<? super GuardianEntity> initTargetFinder() {
        return FINDER;
    }

    @Override
    protected void removeFrom(PlayerEntity player, GuardianEntity guardian) {
        super.removeFrom(player, guardian);
        ((GuardianEntityAccessor) guardian).callSetActiveAttackTarget(0);
        ((GuardianRightClickHandler) rightClickHandler).reset();
    }
}
