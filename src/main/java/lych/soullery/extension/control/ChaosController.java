package lych.soullery.extension.control;

import lych.soullery.entity.ai.goal.BrainAdaptedNearestAttackableTargetGoal;
import lych.soullery.util.mixin.IBrainMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class ChaosController extends Controller<MobEntity> {
    public ChaosController(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public ChaosController(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    public void startControlling(MobEntity mob, GoalSelector goalSelector, GoalSelector targetSelector) {
        boolean brainValid = ((IBrainMixin<?>) mob.getBrain()).isValidBrain();
        if (brainValid) {
            targetSelector.addGoal(-1, new BrainAdaptedNearestAttackableTargetGoal<>(mob, LivingEntity.class, 0, true, false, entity -> entity != getPlayer()));
        } else {
            targetSelector.addGoal(-1, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, 0, true, false, entity -> entity != getPlayer()));
        }
    }

    @Override
    public boolean overrideTargetGoals() {
        return false;
    }

    @Override
    public boolean shouldDisableBrain() {
        return false;
    }

    @Override
    public boolean shouldDisableTargetTasksAdditionally() {
        return true;
    }

    @Override
    public int getPriority() {
        return 2000;
    }
}
