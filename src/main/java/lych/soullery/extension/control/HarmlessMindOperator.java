package lych.soullery.extension.control;

import lych.soullery.extension.control.attack.MeleeHandler;
import lych.soullery.extension.control.attack.NoMeleeHandler;
import lych.soullery.extension.control.attack.NoTargetFinder;
import lych.soullery.extension.control.attack.TargetFinder;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class HarmlessMindOperator extends DefaultMindOperator {
    public HarmlessMindOperator(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public HarmlessMindOperator(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
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
