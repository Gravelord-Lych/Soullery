package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.iface.ISpellCastable;
import lych.soullery.entity.Soul;
import lych.soullery.entity.monster.raider.DarkEvokerEntity;
import lych.soullery.util.CollectionUtils;
import lych.soullery.util.ModEffectUtils;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static lych.soullery.util.EntityUtils.checkGoalInstantiationServerside;

public final class DarkEvokerGoals {
    private DarkEvokerGoals() {
        throw new UnsupportedOperationException("Instantiate its static inner classes");
    }

    public static class ConvertVillagerGoal extends UseSpellGoal<DarkEvokerEntity> {
        private static final double FIND_VILLAGER_RADIUS = 20;
        private VillagerEntity villager;

        public ConvertVillagerGoal(DarkEvokerEntity mob) {
            super(mob);
            checkGoalInstantiationServerside(mob);
        }

        @Override
        protected boolean checkExtraStartConditions() {
            if (!EntityUtils.isAlive(villager)) {
                villager = mob.level.getNearestEntity(VillagerEntity.class, EntityPredicate.DEFAULT.range(FIND_VILLAGER_RADIUS), mob, mob.getX(), mob.getY(), mob.getZ(), mob.getBoundingBox().inflate(FIND_VILLAGER_RADIUS));
            }
            return villager != null && villager.isAlive();
        }

        @Override
        protected void performSpellCasting() {
            if (villager != null) {
                ZombieVillagerEntity zombieVillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, true);
                if (zombieVillager != null) {
                    EntityUtils.spawnAnimServerside(zombieVillager, (ServerWorld) mob.level);
                }
            }
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 350;
        }

    //  TODO: sound
        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return DarkEvokerEntity.CONVERT;
        }
    }

    public static class InfectEnemiesGoal extends UseSpellGoal<DarkEvokerEntity> {
        private static final double CLEAR_EFFECT_RADIUS = 10;
        private static final float MAX_HEAL_AMOUNT = 20;
        private static final float MIN_HEAL_AMOUNT_IF_HAS_EFFECT = 2;
        private final List<LivingEntity> entities = new ArrayList<>();

        public InfectEnemiesGoal(DarkEvokerEntity mob) {
            super(mob);
            checkGoalInstantiationServerside(mob);
        }

        @Override
        protected boolean checkExtraStartConditions() {
            List<LivingEntity> entities = mob.level.getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(CLEAR_EFFECT_RADIUS));
            entities.removeIf(DarkEvokerEntity::isRaider);
            if (entities.isEmpty()) {
                return false;
            }
            CollectionUtils.refill(this.entities, entities);
            return super.checkExtraStartConditions();
        }

        @Override
        protected void performSpellCasting() {
            float healAmount = Math.min(clearEffect(entities), MAX_HEAL_AMOUNT);
            float health = mob.getHealth();
            mob.heal(healAmount);
            EntityUtils.addParticlesAroundSelfServerside(mob, (ServerWorld) mob.level, ParticleTypes.HEART, (int) (mob.getHealth() - health / 2));
        }

        private float clearEffect(List<? extends LivingEntity> list) {
            int effectValue = 0;
            for (LivingEntity entity : list) {
                if (mob.isAlliedTo(entity) || entity instanceof VexEntity && mob.isAlliedTo(((VexEntity) entity).getOwner()) || entity instanceof IHasOwner<?> && mob.isAlliedTo(((IHasOwner<?>) entity).getOwner())) {
                    continue;
                }
                effectValue += EntityUtils.removeEffect(entity, ModEffectUtils::isBeneficial);
                if (mob.level.getDifficulty() == Difficulty.NORMAL || mob.level.getDifficulty() == Difficulty.HARD) {
                    entity.addEffect(new EffectInstance(Effects.WITHER, 20 * (mob.level.getDifficulty() == Difficulty.HARD ? 15 : 7), 0));
                }
                EntityUtils.addParticlesAroundSelfServerside(entity, (ServerWorld) mob.level, ParticleTypes.HAPPY_VILLAGER, 4 + mob.getRandom().nextInt(3));
            }
            if (effectValue > 0) {
                return Math.max(effectValue / 200f, MIN_HEAL_AMOUNT_IF_HAS_EFFECT);
            }
            return 0;
        }

        @Override
        public void stop() {
            super.stop();
            entities.clear();
        }

        @Override
        protected int getCastingTime() {
            return 30;
        }

        @Override
        protected int getCastingInterval() {
            return 250;
        }

    //  TODO: sound
        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return DarkEvokerEntity.INFECT;
        }
    }

    public static class ReviveRaidersGoal extends UseSpellGoal<DarkEvokerEntity> {
        public ReviveRaidersGoal(DarkEvokerEntity mob) {
            super(mob);
            checkGoalInstantiationServerside(mob);
        }

        @Override
        protected boolean checkExtraStartConditions() {
    //        if (!mob.hasActiveRaid()) {
    //            return false;
    //        }
            if (mob.getSouls().size() < DarkEvokerEntity.MAX_REVIVE - 1) {
                return false;
            }
            return super.checkExtraStartConditions();
        }

        @Override
        protected void performSpellCasting() {
            ServerWorld world = (ServerWorld) mob.level;
            Raid raid = mob.getCurrentRaid();
            List<AbstractRaiderEntity> entities = Soul.reviveAll(world, false, mob.getSouls());
            if (raid != null) {
                for (AbstractRaiderEntity raider : entities) {
                    raid.joinRaid(raid.getGroupsSpawned(), raider, null, true);
                    mob.level.addFreshEntity(raider);
                    EntityUtils.spawnAnimServerside(raider, world);
                    EntityUtils.addParticlesAroundSelfServerside(raider, world, ParticleTypes.SMOKE, 10);
                }
            }
            mob.getSouls().clear();
        }

        @Override
        protected int getCastingTime() {
            return 50;
        }

        @Override
        protected int getCastingInterval() {
            return 500;
        }

    //    TODO: sound
        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return DarkEvokerEntity.REVIVE;
        }
    }

    public static class ShootWitherSkullGoal extends UseSpellGoal<DarkEvokerEntity> {
        public ShootWitherSkullGoal(DarkEvokerEntity mob) {
            super(mob);
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                Vector3d vectorToTarget = mob.getEyePosition(1).vectorTo(EntityUtils.centerOf(target));
                WitherSkullEntity skull = new WitherSkullEntity(mob.level, mob, vectorToTarget.x, vectorToTarget.y, vectorToTarget.z);
                skull.setOwner(mob);
                double dangerousProb = 0;
                switch (mob.level.getDifficulty()) {
                    case NORMAL:
                        dangerousProb = 0.05;
                        break;
                    case HARD:
                        dangerousProb = 0.1;
                        break;
                    default:
                }
                boolean dangerous = !mob.canSee(target) && (mob.level.getDifficulty() == Difficulty.NORMAL || mob.level.getDifficulty() == Difficulty.HARD);
                if (!dangerous && mob.getRandom().nextDouble() < dangerousProb) {
                    dangerous = true;
                }
                skull.setDangerous(dangerous);
                skull.setPos(mob.getX(), mob.getEyeY(), mob.getZ());
                mob.level.addFreshEntity(skull);
            }
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        @Override
        protected int getCastingInterval() {
            return 70;
        }

    //  TODO: sound
        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected ISpellCastable.SpellType getSpell() {
            return DarkEvokerEntity.WITHER_SKULL;
        }
    }
}
