package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.IHasOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;

public class OwnerHurtByTargetGoal extends TargetGoal {
    private final IHasOwner<?> mobWithOwner;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public OwnerHurtByTargetGoal(IHasOwner<?> mobWithOwner) {
        super(checkAndGet(mobWithOwner), false);
        this.mobWithOwner = mobWithOwner;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    static MobEntity checkAndGet(IHasOwner<?> mob) {
        if (mob.getAsEntity() instanceof MobEntity) {
            return (MobEntity) mob.getAsEntity();
        }
        throw new IllegalArgumentException("Entity is invalid");
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = mobWithOwner.getOwner();
        if (owner == null) {
            return false;
        } else {
            if (!mobWithOwner.isOwnerInTheSameWorld()) {
                return false;
            }
            ownerLastHurtBy = owner.getLastHurtByMob();
            int timestamp = owner.getLastHurtByMobTimestamp();
            return timestamp != this.timestamp && canAttack(ownerLastHurtBy, EntityPredicate.DEFAULT);
        }
    }

    @Override
    public void start() {
        mob.setTarget(ownerLastHurtBy);
        LivingEntity owner = mobWithOwner.getOwner();
        if (owner != null) {
            timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
