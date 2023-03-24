package lych.soullery.entity.ai.goal;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

public class CopyPassengerTargetGoal extends TargetGoal {
    public CopyPassengerTargetGoal(MobEntity mob, boolean mustSee) {
        super(mob, mustSee);
    }

    public CopyPassengerTargetGoal(MobEntity mob, boolean mustSee, boolean mustReach) {
        super(mob, mustSee, mustReach);
    }

    @Override
    public boolean canUse() {
        return mob.isVehicle() && mob.getControllingPassenger() instanceof MobEntity && ((MobEntity) mob.getControllingPassenger()).getTarget() != null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void start() {
        mob.setTarget(((MobEntity) mob.getControllingPassenger()).getTarget());
        super.start();
    }
}
