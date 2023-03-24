package lych.soullery.entity.ai.goal;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Can use for not only {@link CreatureEntity creatures} but also {@link MobEntity mobs}
 */
public class BetterHurtByTargetGoal extends TargetGoal {
    public static final EntityPredicate HURT_BY_TARGETING = new EntityPredicate().allowUnseeable().ignoreInvisibilityTesting();
    private boolean alertSameType;
    private int timestamp;
    private final EntityPredicate predicate;
    private final Class<?>[] toIgnoreDamage;
    private Class<?>[] toIgnoreAlert;

    public BetterHurtByTargetGoal(MobEntity mob, Class<?>... toIgnoreDamage) {
        this(mob, HURT_BY_TARGETING, toIgnoreDamage);
    }

    public BetterHurtByTargetGoal(MobEntity mob, EntityPredicate predicate, Class<?>... toIgnoreDamage) {
        super(mob, true);
        this.toIgnoreDamage = toIgnoreDamage;
        this.predicate = predicate;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int timestamp = mob.getLastHurtByMobTimestamp();
        LivingEntity hurtBy = mob.getLastHurtByMob();
        if (timestamp != this.timestamp && hurtBy != null) {
            if (hurtBy.getType() == EntityType.PLAYER && this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                for (Class<?> toIgnore : toIgnoreDamage) {
                    if (toIgnore.isAssignableFrom(hurtBy.getClass())) {
                        return false;
                    }
                }
                return canAttack(hurtBy, predicate);
            }
        } else {
            return false;
        }
    }

    public BetterHurtByTargetGoal setAlertOthers(Class<?>... toIgnoreAlert) {
        this.alertSameType = true;
        this.toIgnoreAlert = toIgnoreAlert;
        return this;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        if (this.alertSameType) {
            this.alertOthers();
        }

        super.start();
    }

    protected void alertOthers() {
        double d0 = this.getFollowDistance();
        AxisAlignedBB bb = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
        List<MobEntity> list = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), bb);
        Iterator<MobEntity> iterator = list.iterator();

        while (true) {
            MobEntity toAlert;
            while (true) {
                if (!iterator.hasNext()) {
                    return;
                }

                toAlert = iterator.next();
                if (this.mob != toAlert && toAlert.getTarget() == null && (!(this.mob instanceof TameableEntity) || ((TameableEntity) this.mob).getOwner() == ((TameableEntity) toAlert).getOwner()) && this.mob.getLastHurtByMob() != null && !toAlert.isAlliedTo(this.mob.getLastHurtByMob())) {
                    if (this.toIgnoreAlert == null) {
                        break;
                    }

                    boolean shouldAlert = true;

                    for (Class<?> toIgnore : this.toIgnoreAlert) {
                        if (toAlert.getClass() == toIgnore) {
                            shouldAlert = false;
                            break;
                        }
                    }

                    if (shouldAlert) {
                        break;
                    }
                }
            }

            this.alertOther(toAlert, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(MobEntity p_220793_1_, LivingEntity p_220793_2_) {
        p_220793_1_.setTarget(p_220793_2_);
    }
}
