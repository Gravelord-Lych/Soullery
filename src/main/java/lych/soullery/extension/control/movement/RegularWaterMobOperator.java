package lych.soullery.extension.control.movement;

import lych.soullery.extension.control.ControllerType;
import lych.soullery.extension.control.DefaultMindOperator;
import lych.soullery.extension.control.attack.MeleeHandler;
import lych.soullery.extension.control.attack.NoMeleeHandler;
import lych.soullery.extension.control.attack.NoTargetFinder;
import lych.soullery.extension.control.attack.TargetFinder;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class RegularWaterMobOperator extends DefaultMindOperator {
    public RegularWaterMobOperator(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public RegularWaterMobOperator(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MovementHandler<? super MobEntity> initMovementHandler() {
        return DefaultMovementHandler.WATER;
    }

    @Override
    protected TargetFinder<? super MobEntity> initTargetFinder() {
        return NoTargetFinder.INSTANCE;
    }

    @Override
    protected MeleeHandler<? super MobEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }
}
