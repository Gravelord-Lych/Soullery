package lych.soullery.extension.control;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public final class DefaultController extends Controller<MobEntity> {
    public DefaultController(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public DefaultController(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    public void startControlling(MobEntity mob, GoalSelector goalSelector, GoalSelector targetSelector) {}
}
