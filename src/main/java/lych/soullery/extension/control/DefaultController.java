package lych.soullery.extension.control;

import lych.soullery.entity.ai.goal.BrainAdaptedNearestAttackableTargetGoal;
import lych.soullery.util.mixin.IBrainMixin;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.PigEntity;
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
    public void startControlling(MobEntity mob, GoalSelector goalSelector, GoalSelector targetSelector) {
        boolean brainValid = ((IBrainMixin<?>) mob.getBrain()).isValidBrain();
        if (brainValid) {
            targetSelector.addGoal(2, new BrainAdaptedNearestAttackableTargetGoal<>(mob, PigEntity.class, 0, false, false, a -> true));
        } else {
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, PigEntity.class, 0, false, false, a -> true));
        }
        handleBrainedMonster(mob, goalSelector, brainValid);
    }
}
