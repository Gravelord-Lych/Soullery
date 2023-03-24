package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.IHasOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.function.Predicate;

public class CopyOwnerTargetGoal<T extends MobEntity & IHasOwner<? extends MobEntity>> extends TargetGoal {
    private final T mob;
    private final EntityPredicate copyOwnerTargeting;

    public CopyOwnerTargetGoal(T mob) {
        this(mob, new EntityPredicate().allowUnseeable().ignoreInvisibilityTesting());
    }

    public CopyOwnerTargetGoal(T mob, double range, Predicate<? super LivingEntity> selector) {
        this(mob, new EntityPredicate().allowUnseeable().ignoreInvisibilityTesting().range(range).selector(selector::test));
    }

    public CopyOwnerTargetGoal(T mob, EntityPredicate predicate) {
        super(mob, false);
        this.mob = mob;
        this.copyOwnerTargeting = predicate;
    }

    @Override
    public boolean canUse() {
        return mob.getOwner() != null && mob.getOwner().getTarget() != null && canAttack(mob.getOwner().getTarget(), copyOwnerTargeting);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void start() {
        mob.setTarget(mob.getOwner().getTarget());
        super.start();
    }
}
