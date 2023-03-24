package lych.soullery.entity.ai.goal.boss;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.UseSpellGoal;
import lych.soullery.entity.iface.ISpellCastable;
import lych.soullery.entity.monster.SkeletonFollowerEntity;
import lych.soullery.entity.monster.boss.SkeletonKingEntity;
import lych.soullery.entity.projectile.FangsSummonerEntity;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.mixin.IMobEntityMixin;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class SkeletonKingGoals {
    private SkeletonKingGoals() {
        throw new UnsupportedOperationException("Instantiate its static inner classes");
    }

    public static class SummonSkeletonGoal extends UseSpellGoal<SkeletonKingEntity> {
        private static final int CASTING_TIME = 100;
        private static final int CASTING_TIME_T4_OR_ABOVE = 80;

        private static final int SKELETON_COUNT = 2;
        private static final int SKELETON_COUNT_T2 = 3;
        private static final int SKELETON_COUNT_T6 = 4;
        private static final int SKELETON_COUNT_T9 = 6;
        private static final double SKELETON_COUNT_STEP = 0.2;
        private static final int MAX_SKELETON_COUNT = 10;
        private static final Int2IntMap SKELETON_COUNT_MAP = EntityUtils.intChoiceBuilder().range(1).value(SKELETON_COUNT).range(2, 5).value(SKELETON_COUNT_T2).range(6, 8).value(SKELETON_COUNT_T6).range(9).value(SKELETON_COUNT_T9).build();

        private static final double SPAWN_RADIUS = 6;

        public SummonSkeletonGoal(SkeletonKingEntity mob) {
            super(mob);
        }

        @Override
        protected boolean checkExtraStartConditions() {
            if (!super.checkExtraStartConditions()) {
                return false;
            }
            return mob.level.getNearbyEntities(AbstractSkeletonEntity.class, EntityUtils.ALL, mob, mob.getBoundingBox().inflate(12, 8, 12)).size() <= 15 &&
                   mob.level.getNearbyEntities(SkeletonFollowerEntity.class, EntityUtils.ALL, mob, mob.getBoundingBox().inflate(12, 8, 12)).size() <= 10;
        }

        @Override
        protected void performSpellCasting() {
            final int skeletonCount = getCorrectSkeletonCount();
            for (int i = 0; i < skeletonCount; i++) {
                SkeletonFollowerEntity follower = ModEntities.SKELETON_FOLLOWER.create(mob.level);
                Random random = mob.getRandom();
                if (follower != null) {
                    double x = mob.getX() + (random.nextDouble() - random.nextDouble()) * SPAWN_RADIUS;
                    double y = mob.getY() + random.nextInt(2) + 1;
                    double z = mob.getZ() + (random.nextDouble() - random.nextDouble()) * SPAWN_RADIUS;
                    follower.setOwner(mob);
                    follower.moveTo(x, y, z, random.nextFloat() * 360, 0);
                    follower.finalizeSpawn((ServerWorld) mob.level, mob.level.getCurrentDifficultyAt(mob.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
                    ((ServerWorld) mob.level).addFreshEntityWithPassengers(follower);
                    EntityUtils.spawnAnimServerside(follower, (ServerWorld) mob.level);
                }
            }
        }

        private int getCorrectSkeletonCount() {
            if (mob.isCloned()) {
                return 1;
            }
            if (mob.reachedTier(10)) {
                return Math.min((int) (SKELETON_COUNT_T9 + SKELETON_COUNT_STEP * (mob.getTier() - 10)), MAX_SKELETON_COUNT);
            }
            return SKELETON_COUNT_MAP.get(mob.getTier());
        }

        @Override
        protected int getCastingTime() {
            return mob.reachedTier(4) ? CASTING_TIME_T4_OR_ABOVE : CASTING_TIME;
        }

        @Override
        protected int getCastingInterval() {
            return 300;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
    //      TODO: sound
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return SkeletonKingEntity.SUMMON_SKELETON;
        }
    }

    public static class ArrowAttackGoal extends UseSpellGoal<SkeletonKingEntity> {
        private static final int ARROW_COUNT = 4;
        private static final int ARROW_COUNT_T3 = 6;
        private static final double ARROW_COUNT_STEP = 0.32;
        private static final int MAX_ARROW_COUNT = 10;

        private static final double ARROW_DAMAGE = 4;
        private static final double ARROW_DAMAGE_T3 = 5.4;
        private static final double ARROW_DAMAGE_T6 = 6.8;
        private static final double ARROW_DAMAGE_T10 = 8.2;
        private static final double ARROW_DAMAGE_STEP = 0.15;
        private static final double MAX_ARROW_DAMAGE = 10;
        private static final Int2DoubleMap ARROW_DAMAGE_MAP = EntityUtils.doubleChoiceBuilder().range(1, 2).value(ARROW_DAMAGE).range(3, 5).value(ARROW_DAMAGE_T3).range(6, 9).value(ARROW_DAMAGE_T6).build();

        private static final int CHILD_FANG_COUNT = 3;
        private static final int CHILD_FANG_COUNT_T3 = 5;
        private static final int CHILD_FANG_COUNT_T10_OR_ABOVE = 8;

        private static final float FANGS_DAMAGE = 6;
        private static final float FANGS_DAMAGE_T3 = 8;
        private static final float FANGS_DAMAGE_T6 = 10;

        private static final ImmutableSet<EffectInstance> EFFECTS = ImmutableSet.of();
        private static final ImmutableSet<EffectInstance> EFFECTS_T3 = ImmutableSet.of(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 10, 0));
        private static final ImmutableSet<EffectInstance> EFFECTS_T6 = ImmutableSet.of(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 15, 1), new EffectInstance(Effects.WITHER, 20 * 10, 0));
        private static final ImmutableSet<EffectInstance> EFFECTS_T10 = ImmutableSet.of(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 30, 1), new EffectInstance(Effects.WITHER, 20 * 10, 1), new EffectInstance(Effects.WEAKNESS, 20 * 10, 0));
        private static final Int2ObjectMap<ImmutableSet<EffectInstance>> EFFECTS_MAP = EntityUtils.<ImmutableSet<EffectInstance>>choiceBuilder().range(1, 2).value(EFFECTS).range(3, 5).value(EFFECTS_T3).range(6, 9).value(EFFECTS_T6).build();

        private static final float DEVIATION = 40;

        public ArrowAttackGoal(SkeletonKingEntity mob) {
            super(mob);
        }

        @Override
        protected void performSpellCasting() {
            if (mob.getTarget() == null) {
                return;
            }
            int arrowCount = getCorrectArrowCount();
            if (!mob.level.isClientSide()) {
                for (int i = 0; i < arrowCount; i++) {
                    ImmutableSet<EffectInstance> effects = getCorrectArrowsAndFangsEffects();
                    FangsSummonerEntity arrow = new FangsSummonerEntity(mob, mob.level);
                    effects.forEach(arrow::addEffect);
                    arrow.setBaseDamage(getCorrectArrowBaseDamage());
                    arrow.setFangsDamage(getCorrectFangsDamage());
                    arrow.setSpawnChildrenCountPerDirection(getCorrectChildFangCount());

                    LivingEntity target = mob.getTarget();
                    double tx = target.getX() - mob.getX();
                    double ty = target.getY(0.3333333333333333D) - arrow.getY();
                    double tz = target.getZ() - mob.getZ();
                    double dist = MathHelper.sqrt(tx * tx + tz * tz);
                    arrow.shoot(tx, ty + dist * 0.2, tz, 1.6f, DEVIATION);
                    mob.playSound(SoundEvents.SKELETON_SHOOT, 1, 1 / (mob.getRandom().nextFloat() * 0.4f + 0.8f));

                    mob.level.addFreshEntity(arrow);
                }
            }
        }

        private int getCorrectArrowCount() {
            if (mob.reachedTier(10)) {
                return Math.min((int) (ARROW_COUNT_T3 + ARROW_COUNT_STEP * (mob.getTier() - 10)), MAX_ARROW_COUNT);
            }
            return mob.reachedTier(3) ? ARROW_COUNT_T3 : ARROW_COUNT;
        }

        private int getCorrectChildFangCount() {
            if (mob.reachedTier(10)) {
                return CHILD_FANG_COUNT_T10_OR_ABOVE;
            }
            return mob.reachedTier(3) ? CHILD_FANG_COUNT_T3 : CHILD_FANG_COUNT;
        }

        private ImmutableSet<EffectInstance> getCorrectArrowsAndFangsEffects() {
            if (mob.reachedTier(10)) {
                return EFFECTS_T10;
            }
            return EFFECTS_MAP.get(mob.getTier());
        }

        private float getCorrectFangsDamage() {
            return mob.reachedTier(6) ? FANGS_DAMAGE_T6 : mob.reachedTier(3) ? FANGS_DAMAGE_T3 : FANGS_DAMAGE;
        }

        private double getCorrectArrowBaseDamage() {
            if (mob.reachedTier(10)) {
                return Math.min(ARROW_DAMAGE_T10 + (mob.getTier() - 10) * ARROW_DAMAGE_STEP, MAX_ARROW_DAMAGE);
            }
            return ARROW_DAMAGE_MAP.get(mob.getTier());
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
//          TODO: sound
            return SoundEvents.EVOKER_CAST_SPELL;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return SkeletonKingEntity.FANGS;
        }
    }

    public static class HealSelfGoal extends UseSpellGoal<SkeletonKingEntity> {
//      The heal-amount of T1 Skeleton King is 10 (8,875 + 1.125 * 1), not 8.875.
        private static final float HEAL_AMOUNT = 8.875f;
        private static final float HEAL_AMOUNT_STEP = 1.125f;
        private static final float MAX_HEAL_AMOUNT = 50;

        public HealSelfGoal(SkeletonKingEntity mob) {
            super(mob);
        }

        @Override
        protected void performSpellCasting() {
            mob.heal(getCorrectHealAmount());
//          Safe cast, goal is running serverside
            EntityUtils.addParticlesAroundSelfServerside(mob, (ServerWorld) mob.level, ParticleTypes.HEART, 6);
        }

        private float getCorrectHealAmount() {
            return Math.min(HEAL_AMOUNT + HEAL_AMOUNT_STEP * mob.getTier(), MAX_HEAL_AMOUNT);
        }

        @Override
        protected boolean checkExtraStartConditions() {
            return super.checkExtraStartConditions() && mob.getHealth() < mob.getMaxHealth() * 0.8f;
        }

        @Override
        protected int getCastWarmupTime() {
            return 30;
        }

        @Override
        protected int getCastingTime() {
            return 30;
        }

        @Override
        protected int getCastingInterval() {
            return mob.getHealth() <= mob.getMaxHealth() * 0.5f ? 200 : 270;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
//          TODO: sound
            return SoundEvents.EVOKER_CAST_SPELL;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return SkeletonKingEntity.HEAL;
        }
    }

    public static class BoostSkeletonGoal extends UseSpellGoal<SkeletonKingEntity> {
        private static final ImmutableSet<EffectInstance> EFFECTS = ImmutableSet.of(new EffectInstance(Effects.DAMAGE_BOOST, 20 * 20, 0), new EffectInstance(Effects.MOVEMENT_SPEED, 20 * 20, 0));
        private static final ImmutableSet<EffectInstance> EFFECTS_T8 = ImmutableSet.of(new EffectInstance(Effects.DAMAGE_BOOST, 20 * 30, 1), new EffectInstance(Effects.MOVEMENT_SPEED, 20 * 30, 1), new EffectInstance(Effects.FIRE_RESISTANCE, 20 * 30, 0), new EffectInstance(Effects.DAMAGE_RESISTANCE, 20 * 30, 0));

        public BoostSkeletonGoal(SkeletonKingEntity mob) {
            super(mob);
        }

        @Override
        protected void performSpellCasting() {
            for (AbstractSkeletonEntity skeleton : Iterables.concat(Collections.singleton(mob), getSkeletonsNearby())) {
                if (skeleton.getHealth() < skeleton.getMaxHealth() * 0.5) {
                    skeleton.addEffect(new EffectInstance(Effects.HARM, 1, 0));
                }
                getCorrectEffects().stream().map(effect -> Pair.of(skeleton, skeleton.addEffect(effect))).filter(Pair::getSecond).forEach(pair -> EntityUtils.addParticlesAroundSelfServerside(pair.getFirst(), (ServerWorld) mob.level, ParticleTypes.HAPPY_VILLAGER, 10));
            }
        }

        @Override
        protected boolean checkExtraStartConditions() {
            if (!(mob.reachedTier(5) && super.checkExtraStartConditions())) {
                return false;
            }
            return mob.getHealth() <= mob.getMaxHealth() * 0.25 || getSkeletonsNearby().size() >= 4 || mob.level.getNearbyEntities(SkeletonFollowerEntity.class, EntityUtils.ALL, mob, mob.getBoundingBox().inflate(12, 8, 12)).size() >= 2;
        }

        private List<AbstractSkeletonEntity> getSkeletonsNearby() {
            return mob.level.getNearbyEntities(AbstractSkeletonEntity.class, EntityUtils.ALL, mob, mob.getBoundingBox().inflate(12, 8, 12));
        }

        private ImmutableSet<EffectInstance> getCorrectEffects() {
            return mob.reachedTier(8) ? EFFECTS_T8 : EFFECTS;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 225;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
//          TODO: sound
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return SkeletonKingEntity.BOOST_SKELETON;
        }
    }

    public static class CloneSkeletonGoal extends UseSpellGoal<SkeletonKingEntity> {
        public CloneSkeletonGoal(SkeletonKingEntity mob) {
            super(mob);
        }

        @Override
        protected boolean checkExtraStartConditions() {
            if (!(mob.reachedTier(10) && super.checkExtraStartConditions())) {
                return false;
            }
            if (mob.isCloned()) {
                return false;
            }
            return getSkeletonsNearby().size() >= 2;
        }

        private List<AbstractSkeletonEntity> getSkeletonsNearby() {
            return mob.level.getNearbyEntities(AbstractSkeletonEntity.class, EntityUtils.ALL, mob, mob.getBoundingBox().inflate(12, 8, 12));
        }

        @Override
        protected void performSpellCasting() {
            ServerWorld world = (ServerWorld) mob.level;
            cloneSkeletons(world);
            SkeletonKingEntity.Cloned cloned = (SkeletonKingEntity.Cloned) completelyClone(mob, ModEntities.CLONED_SKELETON_KING);
            if (cloned != null) {
                cloned.setOwner(mob);
                spawnClonedSkeleton(world, mob.getRandom(), mob, cloned);
            }
        }

        private void cloneSkeletons(ServerWorld world) {
            for (AbstractSkeletonEntity skeleton : getSkeletonsNearby()) {
                if (skeleton instanceof SkeletonKingEntity) {
                    return;
                }
                Random random = skeleton.getRandom();
                AbstractSkeletonEntity clonedSkeleton = completelyClone(skeleton);
                if (clonedSkeleton != null) {
                    copyEquipments(skeleton, clonedSkeleton);
                    spawnClonedSkeleton(world, random, skeleton, clonedSkeleton);
                }
            }
        }

        private static void copyEquipments(AbstractSkeletonEntity skeleton, AbstractSkeletonEntity clonedSkeleton) {
            if (!(clonedSkeleton instanceof SkeletonKingEntity)) {
                for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
                    ItemStack stack = skeleton.getItemBySlot(slotType);
                    if (!stack.isEmpty()) {
                        clonedSkeleton.setItemSlot(slotType, stack.copy());
                        clonedSkeleton.setDropChance(slotType, ((IMobEntityMixin) skeleton).callGetEquipmentDropChance(slotType));
                    }
                }
            }
        }

        private static void spawnClonedSkeleton(ServerWorld world, Random random, AbstractSkeletonEntity originalSkeleton, AbstractSkeletonEntity clonedSkeleton) {
            Vector3d randomVec = new Vector3d(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1).normalize();
            clonedSkeleton.moveTo(originalSkeleton.getX() + randomVec.x, originalSkeleton.getY(), originalSkeleton.getZ() + randomVec.z, originalSkeleton.yRot, originalSkeleton.xRot);
            if (originalSkeleton.isPassenger()) {
                Objects.requireNonNull(clonedSkeleton.getVehicle());
                clonedSkeleton.getVehicle().copyPosition(clonedSkeleton);
                clonedSkeleton.startRiding(clonedSkeleton.getVehicle(), true);
            }
            clonedSkeleton.finalizeSpawn(world, world.getCurrentDifficultyAt(clonedSkeleton.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
            clonedSkeleton.setHealth(1);
            EntityUtils.getAttribute(clonedSkeleton, Attributes.MAX_HEALTH).setBaseValue(1);
            clonedSkeleton.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 20 * 2, 4));
            world.addFreshEntity(clonedSkeleton);
            EntityUtils.spawnAnimServerside(clonedSkeleton, world);
        }

        @SuppressWarnings("unchecked")
        @Nullable
        private static <T extends MobEntity> T completelyClone(T original) {
            return completelyClone(original, (EntityType<? extends T>) original.getType());
        }

        @Nullable
        private static <T extends MobEntity> T completelyClone(T original, EntityType<? extends T> type) {
            T cloned = type.create(original.level);

            if (cloned == null) {
                return null;
            }

            cloned.setBaby(original.isBaby());
            cloned.setNoAi(original.isNoAi());
            cloned.setLeftHanded(original.isLeftHanded());

            if (original.hasCustomName()) {
                cloned.setCustomName(original.getCustomName());
                cloned.setCustomNameVisible(original.isCustomNameVisible());
            }

            if (original.isPersistenceRequired()) {
                cloned.setPersistenceRequired();
            }

            cloned.setInvulnerable(original.isInvulnerable());
            cloned.setCanPickUpLoot(original.canPickUpLoot());

            for (EffectInstance effect : original.getActiveEffects()) {
                cloned.addEffect(effect);
            }

            if (original.isPassenger() && original.getVehicle() instanceof MobEntity) {
                Entity vehicle = original.getVehicle();
//              Recursive call
                Entity clonedVehicle = completelyClone((MobEntity) vehicle);
                if (clonedVehicle != null) {
                    cloned.startRiding(clonedVehicle);
                }
            }

            return cloned;
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 300;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
//          TODO: sound
            return SoundEvents.EVOKER_CAST_SPELL;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return SkeletonKingEntity.CLONE_SKELETON;
        }
    }
}
