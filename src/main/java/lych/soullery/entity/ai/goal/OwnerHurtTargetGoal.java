package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.IHasOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;

import static lych.soullery.entity.ai.goal.OwnerHurtByTargetGoal.checkAndGet;

public class OwnerHurtTargetGoal extends TargetGoal {
    private final IHasOwner<?> mobWithOwner;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public OwnerHurtTargetGoal(IHasOwner<?> mobWithOwner) {
        super(checkAndGet(mobWithOwner), false);
        this.mobWithOwner = mobWithOwner;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = mobWithOwner.getOwner();
        if (owner == null) {
            return false;
        } else {
            ownerLastHurt = owner.getLastHurtMob();
            int timestamp = owner.getLastHurtMobTimestamp();
            return timestamp != this.timestamp && canAttack(ownerLastHurt, EntityPredicate.DEFAULT);
        }
    }

    @Override
    public void start() {
        mob.setTarget(ownerLastHurt);
        LivingEntity owner = mobWithOwner.getOwner();
        if (owner != null) {
            timestamp = owner.getLastHurtMobTimestamp();
        }
        super.start();
    }
}
