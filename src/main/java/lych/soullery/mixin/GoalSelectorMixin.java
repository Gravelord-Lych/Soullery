package lych.soullery.mixin;

import lych.soullery.util.mixin.IGoalSelectorMixin;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin implements IGoalSelectorMixin {
    @Shadow
    @Final
    private Set<PrioritizedGoal> availableGoals;
    @Nullable
    private GoalSelector alt;
    private MobEntity mob;

    @Override
    public Set<PrioritizedGoal> getAvailableGoals() {
        return availableGoals;
    }

    @Override
    public void setMob(MobEntity mob) {
        this.mob = mob;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void tickAlt(CallbackInfo ci) {
        if (alt != null && shouldUseAlt()) {
            alt.tick();
            ci.cancel();
        }
    }

    @Inject(method = "getRunningGoals", at = @At(value = "HEAD"), cancellable = true)
    private void getAltRunningGoals(CallbackInfoReturnable<Stream<PrioritizedGoal>> cir) {
        if (alt != null && shouldUseAlt()) {
            cir.setReturnValue(alt.getRunningGoals());
        }
    }

    @Inject(method = "enableControlFlag", at = @At(value = "TAIL"))
    private void enableAltControlFlags(Goal.Flag flag, CallbackInfo ci) {
        if (alt != null) {
            alt.enableControlFlag(flag);
        }
    }

    @Inject(method = "disableControlFlag", at = @At(value = "TAIL"))
    private void disableAltControlFlags(Goal.Flag flag, CallbackInfo ci) {
        if (alt != null) {
            alt.disableControlFlag(flag);
        }
    }

    @Inject(method = "setControlFlag", at = @At(value = "TAIL"))
    private void disableAltControlFlags(Goal.Flag flag, boolean value, CallbackInfo ci) {
        if (alt != null) {
            alt.setControlFlag(flag, value);
        }
    }

    @Inject(method = "removeGoal", at = @At(value = "TAIL"))
    private void removeAltGoal(Goal goal, CallbackInfo ci) {
        if (alt != null) {
            alt.removeGoal(goal);
        }
    }

    private boolean shouldUseAlt() {
        if (isAlt()) {
            return false;
        }
        return !((IGoalSelectorMixin) alt).getAvailableGoals().isEmpty();
    }

    @Nullable
    @Override
    public GoalSelector getAlt() {
        return alt;
    }

    @Override
    public void setAlt(GoalSelector alt) {
        this.alt = Objects.requireNonNull(alt);
    }

    @Override
    public void transferGoals() {
        if (alt != null) {
            ((IGoalSelectorMixin) alt).getAvailableGoals().addAll(availableGoals);
        }
    }

    @Override
    public void removeAllAltGoals() {
        if (alt != null) {
            Set<PrioritizedGoal> goals = new HashSet<>(((IGoalSelectorMixin) alt).getAvailableGoals());
            goals.stream().filter(PrioritizedGoal::isRunning).forEach(Goal::stop);
            ((IGoalSelectorMixin) alt).getAvailableGoals().clear();
        }
    }
}
