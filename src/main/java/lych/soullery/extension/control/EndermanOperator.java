package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.DefaultMovementHandler;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.rotation.DefaultRotationHandler;
import lych.soullery.extension.control.rotation.RotationHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EndermanOperator extends MindOperator<EndermanEntity> {
    private static final TargetFinder<? super EndermanEntity> ALT_FINDER = new TelepathicTargetFinder(12, Math.PI / 4, 6, true);

    public EndermanOperator(ControllerType<EndermanEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public EndermanOperator(ControllerType<EndermanEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super EndermanEntity> initMeleeHandler() {
        return DefaultMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super EndermanEntity> initMovementHandler() {
        return DefaultMovementHandler.NORMAL;
    }

    @Override
    protected RotationHandler<? super EndermanEntity> initRotationHandler() {
        return DefaultRotationHandler.INSTANCE;
    }

    @Override
    protected TargetFinder<? super EndermanEntity> initAlternativeTargetFinder() {
        return ALT_FINDER;
    }

    @Override
    protected RightClickHandler<? super EndermanEntity> initRightClickHandler() {
        return new EndermanRightClickHandler();
    }

    @Nullable
    @Override
    protected LivingEntity findRightClickTarget(EndermanEntity operatingEnderman, ServerPlayerEntity player) {
        LivingEntity target = super.findRightClickTarget(operatingEnderman, player);
        if (target == null) {
            target = alternativeTargetFinder.findTarget(operatingEnderman, player);
        }
        return target;
    }
}
