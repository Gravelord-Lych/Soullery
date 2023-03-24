package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.DefaultMeleeHandler;
import lych.soullery.extension.control.attack.MeleeHandler;
import lych.soullery.extension.control.attack.RangedTargetFinder;
import lych.soullery.extension.control.attack.TargetFinder;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class AggressiveFlyerMindOperator extends FlyerMindOperator {
    public AggressiveFlyerMindOperator(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public AggressiveFlyerMindOperator(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super MobEntity> initMeleeHandler() {
        return DefaultMeleeHandler.INSTANCE;
    }

    @Override
    protected TargetFinder<? super MobEntity> initTargetFinder() {
        return RangedTargetFinder.IMPROVED;
    }
}
