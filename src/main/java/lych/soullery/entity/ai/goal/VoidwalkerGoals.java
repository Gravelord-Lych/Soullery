package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.ISpellCastable.SpellType;
import lych.soullery.entity.monster.voidwalker.*;
import lych.soullery.entity.projectile.EtherealArrowEntity;
import lych.soullery.util.BoundingBoxUtils;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public final class VoidwalkerGoals {
    private VoidwalkerGoals() {}

    public static class FindTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public FindTargetGoal(AbstractVoidwalkerEntity voidwalker, Class<T> targetType, boolean mustSee) {
            super(voidwalker, targetType, mustSee);
        }

        public FindTargetGoal(AbstractVoidwalkerEntity voidwalker, Class<T> targetType, boolean mustSee, boolean mustReach) {
            super(voidwalker, targetType, mustSee, mustReach);
        }

        public FindTargetGoal(AbstractVoidwalkerEntity voidwalker, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<? super LivingEntity> predicate) {
            super(voidwalker, targetType, randomInterval, mustSee, mustReach, predicate == null ? null : predicate::test);
        }

        {
            targetConditions = ((AbstractVoidwalkerEntity) mob).customizeTargetConditions(targetConditions);
        }

        @Override
        public boolean canUse() {
            if (!canMobAttack()) {
                return false;
            }
            return super.canUse();
        }

        protected boolean canMobAttack() {
            return ((AbstractVoidwalkerEntity) mob).canAttack();
        }

        @Override
        public void start() {
            super.start();
            AbstractVoidwalkerEntity voidwalker = (AbstractVoidwalkerEntity) mob;
            if (voidwalker.onSetTarget(target)) {
                voidwalker.setEtherealCooldown(AbstractVoidwalkerEntity.LONG_ETHEREAL_COOLDOWN);
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (((AbstractVoidwalkerEntity) mob).shouldAdjustTarget()) {
                ((AbstractVoidwalkerEntity) mob).setAdjustTarget(false);
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        protected void findTarget() {
            targetConditions = targetConditions.range(getFollowDistance());
            super.findTarget();
        }

        @Override
        protected AxisAlignedBB getTargetSearchArea(double followDistance) {
            return mob.getBoundingBox().inflate(followDistance, followDistance / 2, followDistance);
        }
    }

    /**
     * [VanillaCopy]
     * {@link net.minecraft.entity.ai.goal.NearestAttackableTargetExpiringGoal NearestAttackableTargetExpiringGoal}
     */
    public static class FindTargetExpiringGoal<T extends LivingEntity> extends FindTargetGoal<T> {
        public static final int COOLDOWN = 200;
        public static final int CAN_ATTACK_COOLDOWN = 100;
        private int cooldown = 0;

        public FindTargetExpiringGoal(AbstractVoidwalkerEntity voidwalker, Class<T> targetType, boolean mustSee) {
            super(voidwalker, targetType, mustSee);
        }

        public FindTargetExpiringGoal(AbstractVoidwalkerEntity voidwalker, Class<T> targetType, boolean mustSee, boolean mustReach) {
            super(voidwalker, targetType, mustSee, mustReach);
        }

        public FindTargetExpiringGoal(AbstractVoidwalkerEntity voidwalker, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<? super LivingEntity> predicate) {
            super(voidwalker, targetType, randomInterval, mustSee, mustReach, predicate);
        }

        public int getCooldown() {
            return cooldown;
        }

        public void decrementCooldown() {
            cooldown--;
        }

        @Override
        protected boolean canMobAttack() {
            return true;
        }

        @Override
        public boolean canUse() {
            if (cooldown <= 0 && mob.getRandom().nextBoolean()) {
                findTarget();
                return target != null;
            }
            return false;
        }

        @Override
        public void start() {
            cooldown = COOLDOWN;
            super.start();
        }
    }

    public static class RetreatGoal extends Goal {
        private static final int MAX_TRY_TIME = 5;
        private final AbstractVoidwalkerEntity voidwalker;
        private final int retreatFreq;
        private Vector3d retreatPos;

        public RetreatGoal(AbstractVoidwalkerEntity voidwalker, int retreatFreq) {
            this.voidwalker = voidwalker;
            this.retreatFreq = retreatFreq;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!voidwalker.isLowHealth() || !voidwalker.canBeEtherealToAttack()) {
                return false;
            }
            LivingEntity target = voidwalker.getTarget();
            if (!EntityUtils.isAlive(target) || voidwalker.isLowHealth(target)) {
                return false;
            }
            if (voidwalker.getRandom().nextInt(retreatFreq) == 0) {
                findRetreatTarget(target);
                return retreatPos != null;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            voidwalker.setMoveControlSpeedModifier(1.1);
            voidwalker.setSneakTarget(retreatPos);
            voidwalker.setEtherealCooldown(AbstractVoidwalkerEntity.SHORT_ETHEREAL_COOLDOWN);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        protected void findRetreatTarget(LivingEntity enemy) {
            for (int i = 0; i < MAX_TRY_TIME; i++) {
                Vector3d retreatPos = RandomPositionGenerator.getPosAvoid(voidwalker, 16, 7, enemy.position());
                if (isPositionValid(retreatPos)) {
                    this.retreatPos = retreatPos;
                    break;
                }
            }
        }

        protected boolean isPositionValid(@Nullable Vector3d retreatPos) {
            if (retreatPos == null) {
                return false;
            }
            AxisAlignedBB bb = BoundingBoxUtils.inflate(retreatPos, 4);
            for (LivingEntity entity : voidwalker.level.getEntitiesOfClass(LivingEntity.class, bb)) {
                if (entity instanceof PlayerEntity || entity instanceof MobEntity && ((MobEntity) entity).getTarget() == voidwalker) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class AttackGoal extends MeleeAttackGoal {
        private final VoidwalkerEntity voidwalker;

        public AttackGoal(VoidwalkerEntity voidwalker, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(voidwalker, speedModifier, followingTargetEvenIfNotSeen);
            this.voidwalker = voidwalker;
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity target = voidwalker.getTarget();
            if (target != null) {
                if (voidwalker.getAttackCooldown() == 0 && voidwalker.canBeEtherealToAttack()) {
                    if (farFrom(target) || voidwalker.getRandom().nextInt(50) == 0) {
                        voidwalker.setSneakTarget(target.position());
                        voidwalker.setEtherealCooldown(AbstractVoidwalkerEntity.SHORT_ETHEREAL_COOLDOWN);
                    }
                }
            }
        }

        private boolean farFrom(LivingEntity target) {
            return voidwalker.distanceToSqr(target) >= 4 * 4;
        }

        @Override
        protected double getAttackReachSqr(LivingEntity target) {
            if (voidwalker.getVehicle() != null) {
                float width = voidwalker.getVehicle().getBbWidth();
                return ((width * 2) * (width * 2) + target.getBbWidth()) * getSpecialAttackReachRadiusMultiplierSqr();
            }
            return super.getAttackReachSqr(target) * getSpecialAttackReachRadiusMultiplierSqr();
        }

        private double getSpecialAttackReachRadiusMultiplierSqr() {
            double multiplier = voidwalker.getAttackReachRadiusMultiplier();
            return multiplier * multiplier;
        }
    }

    public static class SpawnRainOfArrowGoal extends UseSpellGoal<VoidArcherEntity> {
        private static final float DEVIATION = 35;

        public SpawnRainOfArrowGoal(VoidArcherEntity mob) {
            super(mob);
        }

        @Override
        public void performSpellCasting() {
            LivingEntity target = mob.getTarget();
            if (!EntityUtils.isAlive(target)) {
                return;
            }
            Random random = mob.getRandom();
            for (int i = 0; i < 4 + mob.getTier().getId() + random.nextInt(3) - (mob.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? 3 : 0); i++) {
                EtherealArrowEntity arrow = new EtherealArrowEntity(mob.level, mob);
                if (mob.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY)) {
                    arrow.setEnhanced(true);
                }
                arrow.setBaseDamage(mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.75);
                double tx = target.getX() - mob.getX();
                double ty = target.getY(0.5) - arrow.getY();
                double tz = target.getZ() - mob.getZ();
                double dis = MathHelper.sqrt(tx * tx + ty * ty + ty * tz);
                arrow.shoot(tx, ty + dis * 0.2, tz, 1.8f, DEVIATION);
                mob.level.addFreshEntity(arrow);
            }
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 300;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        @Override
        protected SpellType getSpell() {
            return mob.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? VoidArcherEntity.RAIN_OF_ENHANCED_ARROW : VoidArcherEntity.RAIN_OF_ARROW;
        }
    }

    public static class BowAttackGoal extends Goal {
        private static final int MAX_TRY_TIME = 5;
        private final VoidArcherEntity archer;
        private final double speedModifier;
        private final IntSupplier attackIntervalMin;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public BowAttackGoal(VoidArcherEntity archer, double speedModifier, int attackIntervalMin, float attackRadius) {
            this(archer, speedModifier, () -> attackIntervalMin, attackRadius);
        }

        public BowAttackGoal(VoidArcherEntity archer, double speedModifier, IntSupplier attackIntervalMin, float attackRadius) {
            this.archer = archer;
            this.speedModifier = speedModifier;
            this.attackIntervalMin = attackIntervalMin;
            this.attackRadiusSqr = attackRadius * attackRadius;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityUtils.isAlive(archer.getTarget()) && isHoldingBow();
        }

        protected boolean isHoldingBow() {
            return archer.isHolding(item -> item instanceof BowItem);
        }

        @Override
        public boolean canContinueToUse() {
            if (archer.isEthereal() || !EntityUtils.isAlive(archer.getTarget())) {
                return false;
            }
            return (canUse() || !archer.getNavigation().isDone()) && isHoldingBow();
        }

        @Override
        public void start() {
            super.start();
            archer.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            archer.setAggressive(false);
            seeTime = 0;
            attackTime = -1;
            archer.stopUsingItem();
        }

        @Override
        public void tick() {
            LivingEntity target = this.archer.getTarget();
            if (target != null) {
                double dis = archer.distanceToSqr(target.getX(), target.getY(), target.getZ());
                if (dis <= 1) {
                    tryRetreat(target);
                }
                boolean canSee = archer.getSensing().canSee(target);
                boolean seen = seeTime > 0;
                if (canSee != seen) {
                    seeTime = 0;
                }

                if (canSee) {
                    seeTime++;
                } else {
                    seeTime--;
                }

                if (!(dis > attackRadiusSqr) && seeTime >= 20) {
                    archer.getNavigation().stop();
                    strafingTime++;
                } else {
                    archer.getNavigation().moveTo(target, speedModifier);
                    strafingTime = -1;
                }

                if (strafingTime >= 20) {
                    if (archer.getRandom().nextDouble() < 0.3) {
                        strafingClockwise = !strafingClockwise;
                    }
                    if (archer.getRandom().nextDouble() < 0.3) {
                        strafingBackwards = !strafingBackwards;
                    }
                    strafingTime = 0;
                }

                if (strafingTime > -1) {
                    if (dis > attackRadiusSqr * 0.75) {
                        strafingBackwards = false;
                    } else if (dis < attackRadiusSqr * 0.25) {
                        strafingBackwards = true;
                    }
                    if (canSee) {
                        archer.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
                    }
                    archer.lookAt(target, 30.0F, 30.0F);
                } else {
                    archer.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }

                if (archer.isUsingItem()) {
                    int ticksUsingItem = archer.getTicksUsingItem();
                    if (ticksUsingItem >= 20) {
                        archer.stopUsingItem();
                        archer.performRangedAttack(target, BowItem.getPowerForTime(ticksUsingItem));
                        attackTime = attackIntervalMin.getAsInt();
                    }
                } else if (--attackTime <= 0 && seeTime >= -60) {
                    archer.startUsingItem(ProjectileHelper.getWeaponHoldingHand(archer, item -> item instanceof BowItem));
                }
            }
        }

        private void tryRetreat(LivingEntity from) {
            for (int i = 0; i < MAX_TRY_TIME; i++) {
                Vector3d retreatPos = RandomPositionGenerator.getPosAvoid(archer, 10, 6, from.position());
                if (retreatPos != null && archer.setSneakTarget(retreatPos)) {
                    archer.setEtherealCooldown(AbstractVoidwalkerEntity.SHORT_ETHEREAL_COOLDOWN);
                }
            }
        }
    }

    public static class FollowVoidwalkerGoal<T extends AbstractVoidwalkerEntity> extends Goal {
        private static final double FOLLOW_RANGE = 24;
        private static final double MIN_DISTANCE = 6;
        private final VoidDefenderEntity defender;
        private final Class<T> followType;
        private final int randomInterval;
        @Nullable
        private Vector3d target;

        public FollowVoidwalkerGoal(VoidDefenderEntity defender, Class<T> followType, int randomInterval) {
            this.defender = defender;
            this.followType = followType;
            this.randomInterval = randomInterval;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            List<T> voidwalkers = defender.getNearbyVoidwalkers(followType, FOLLOW_RANGE);
            if (voidwalkers.isEmpty() || defender.getRandom().nextInt(randomInterval) > 0) {
                return false;
            }
            if (hasCloseVoidwalker(voidwalkers)) {
                return false;
            }
            T voidwalker = findTargetToFollow(voidwalkers);
            if (EntityUtils.isAlive(voidwalker)) {
                target = voidwalker.position();
                return true;
            }
            return false;
        }

        private boolean hasCloseVoidwalker(List<T> voidwalkers) {
            return voidwalkers.stream().min(Comparator.comparingDouble(defender::distanceToSqr)).filter(v -> defender.distanceToSqr(v) < MIN_DISTANCE * MIN_DISTANCE).isPresent();
        }

        @Override
        public void start() {
            super.start();
            Objects.requireNonNull(target);
            if (defender.getEtherealCooldown() == 0 && defender.getRandom().nextBoolean()) {
                if (defender.setSneakTarget(target)) {
                    defender.setEtherealCooldown(AbstractVoidwalkerEntity.LONG_ETHEREAL_COOLDOWN);
                    target = null;
                }
            } else {
                defender.getNavigation().moveTo(target.x, target.y, target.z, 1);
                if (defender.getNavigation().isDone()) {
                    target = null;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return target != null;
        }

        @Nullable
        private T findTargetToFollow(List<T> voidwalkers) {
            return voidwalkers.stream().filter(v -> defender.distanceToSqr(v) > MIN_DISTANCE * MIN_DISTANCE).min(this::compare).orElse(null);
        }

        private int compare(T v1, T v2) {
            int dl1 = getDistanceLevel(defender.distanceToSqr(v1));
            int dl2 = getDistanceLevel(defender.distanceToSqr(v2));
            if (dl1 != dl2) {
                return dl1 > dl2 ? 1 : -1;
            }
            return Float.compare(v1.getHealth(), v2.getHealth());
        }

        private static int getDistanceLevel(double distanceSqr) {
            if (distanceSqr > 16 * 16) {
                return 3;
            }
            if (distanceSqr > 10 * 10) {
                return 2;
            }
            return 1;
        }
    }

    public static class HealOthersGoal extends Goal {
        private final AbstractVoidwalkerEntity voidwalker;
        private final double healRange;
        private final int healInterval;
        private AbstractVoidwalkerEntity healTarget;
        private int healCooldown;

        public HealOthersGoal(AbstractVoidwalkerEntity voidwalker, double healRange, int healInterval) {
            this.voidwalker = voidwalker;
            this.healRange = healRange;
            this.healInterval = healInterval;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            List<AbstractVoidwalkerEntity> voidwalkers = voidwalker.getNearbyVoidwalkers(AbstractVoidwalkerEntity.class, healRange);
            if (voidwalkers.isEmpty()) {
                return false;
            }
            healTarget = findHealTarget(voidwalkers);
            return EntityUtils.isAlive(healTarget);
        }

        @Override
        public void start() {
            super.start();
            healCooldown = healInterval;
        }

        @Override
        public void tick() {
            super.tick();
            if (!EntityUtils.isAlive(healTarget)) {
                return;
            }
            if (healCooldown > 0) {
                healCooldown--;
            }
            voidwalker.getLookControl().setLookAt(healTarget, 30, 30);
            voidwalker.getNavigation().moveTo(healTarget, 0.8);
            if (healCooldown == 0) {
                if (voidwalker.getBoundingBox().inflate(0.5).intersects(healTarget.getBoundingBox()) || voidwalker.getNavigation().isDone()) {
                    voidwalker.swing(Hand.OFF_HAND);
                    voidwalker.doHealTarget(healTarget);
                    healCooldown = healInterval;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return EntityUtils.isAlive(healTarget) && voidwalker.canHeal(healTarget);
        }

        @Override
        public void stop() {
            super.stop();
            healCooldown = 0;
        }

        @Nullable
        private AbstractVoidwalkerEntity findHealTarget(List<AbstractVoidwalkerEntity> voidwalkers) {
            return voidwalkers.stream().filter(voidwalker::canHeal).min(Comparator.comparing(LivingEntity::getHealth)).orElse(null);
        }
    }

    public static class VoidwalkerRandomWalkingGoal extends RandomWalkingGoal {
        public VoidwalkerRandomWalkingGoal(AbstractVoidwalkerEntity voidwalker, double speedModifier) {
            super(voidwalker, speedModifier);
        }

        public VoidwalkerRandomWalkingGoal(AbstractVoidwalkerEntity voidwalker, double speedModifier, int interval) {
            super(voidwalker, speedModifier, interval);
        }

        public VoidwalkerRandomWalkingGoal(AbstractVoidwalkerEntity voidwalker, double speedModifier, int interval, boolean checkNoActionTime) {
            super(voidwalker, speedModifier, interval, checkNoActionTime);
        }

        @Nullable
        @Override
        protected Vector3d getPosition() {
            Vector3d position = super.getPosition();
            if (position != null && mob.isPassenger() && position.distanceToSqr(mob.position()) <= 3 * 3) {
                return null;
            }
            return position;
        }
    }
}
