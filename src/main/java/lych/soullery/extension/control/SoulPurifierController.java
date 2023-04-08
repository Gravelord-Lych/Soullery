package lych.soullery.extension.control;

import lych.soullery.entity.ai.goal.BrainAdaptedNearestAttackableTargetGoal;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.mixin.IBrainMixin;
import lych.soullery.util.mixin.IEntityPredicateMixin;
import lych.soullery.util.mixin.INearestAttackableTargetGoalMixin;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;
import java.util.function.Predicate;

public class SoulPurifierController extends Controller<MobEntity> {
    public SoulPurifierController(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public SoulPurifierController(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    public void startControlling(MobEntity mob, GoalSelector goalSelector, GoalSelector targetSelector) {
        boolean brainValid = ((IBrainMixin<?>) mob.getBrain()).isValidBrain();
        if (brainValid) {
            targetSelector.addGoal(2, new BrainAdaptedNearestAttackableTargetGoal<>(mob, PigEntity.class, 0, false, false, a -> true));
        } else {
            NearestAttackableTargetGoal<?> goal = findTargetGoalTowards(PlayerEntity.class, mob.targetSelector);
            if (goal != null) {
                goal = modifyTargetGoal(goal, MobEntity.class, entity -> entity instanceof IMob);
            } else {
                goal = new NearestAttackableTargetGoal<>(mob, MobEntity.class, 0, true, false, entity -> entity instanceof IMob);
                resetConditions((INearestAttackableTargetGoalMixin<?>) goal);
            }
            targetSelector.addGoal(2, goal);
        }
        handleBrainedMonster(mob, goalSelector, brainValid);
        if (!(EntityUtils.getTarget(mob) instanceof IMob)) {
            EntityUtils.setTarget(mob, null);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends LivingEntity, E extends LivingEntity> NearestAttackableTargetGoal<E> modifyTargetGoal(NearestAttackableTargetGoal<T> goal, Class<E> to, Predicate<? super LivingEntity> predicate) {
        NearestAttackableTargetGoal<E> newGoal = ((INearestAttackableTargetGoalMixin<E>) goal).modifyType(to, predicate);
        resetConditions((INearestAttackableTargetGoalMixin<E>) newGoal);
        return newGoal;
    }

    private static <E extends LivingEntity> void resetConditions(INearestAttackableTargetGoalMixin<E> newGoalM) {
        EntityPredicate targetConditions = newGoalM.getTargetConditions();
        newGoalM.setTargetConditions(((IEntityPredicateMixin) targetConditions).copy().allowSameTeam());
    }
}
