package lych.soullery.entity.ai.goal.boss;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.UseSpellGoal;
import lych.soullery.entity.monster.SoulSkeletonEntity;
import lych.soullery.entity.monster.boss.SoulSkeletonKingEntity;
import lych.soullery.entity.projectile.SoulArrowEntity;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.util.PositionCalculators;
import lych.soullery.util.WorldUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static lych.soullery.entity.iface.ISpellCastable.SpellType;

public final class SoulSkeletonKingGoals {
    private SoulSkeletonKingGoals() {}

    public static class ShootGoal extends UseSpellGoal<SoulSkeletonKingEntity> {
        private static final float DEVIATION = 40;

        public ShootGoal(SoulSkeletonKingEntity mob) {
            super(mob);
        }

        @Override
        protected boolean checkExtraStartConditions() {
            return super.checkExtraStartConditions() && !mob.canMelee();
        }

        @Override
        protected void performSpellCasting() {
            for (int i = 0; i < 6 + mob.getRandom().nextInt(2); i++) {
                SoulArrowEntity arrow = new SoulArrowEntity(mob.level, mob);
                arrow.setBaseDamage(4);
                LivingEntity target = mob.getTarget();
                double tx = target.getX() - mob.getX();
                double ty = target.getY(0.3333333333333333) - arrow.getY();
                double tz = target.getZ() - mob.getZ();
                double dist = MathHelper.sqrt(tx * tx + tz * tz);
                arrow.shoot(tx, ty + dist * 0.2, tz, 1.6f, DEVIATION);
                mob.playSound(ModSoundEvents.SOUL_SKELETON_KING_SHOOT.get(), 2, 1 / (mob.getRandom().nextFloat() * 0.4f + 0.8f));
                mob.level.addFreshEntity(arrow);
            }
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        @Override
        protected int getCastingInterval() {
            return 40;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        @Override
        protected SpellType getSpell() {
            return SoulSkeletonKingEntity.SHOOT;
        }
    }

    public static class SummonGoal extends UseSpellGoal<SoulSkeletonKingEntity> {
        public SummonGoal(SoulSkeletonKingEntity mob) {
            super(mob);
            EntityUtils.checkGoalInstantiationServerside(mob);
        }

        @Override
        protected boolean checkExtraStartConditions() {
            return super.checkExtraStartConditions() && !mob.canMelee();
        }

        @Override
        protected void performSpellCasting() {
            Random random = mob.getRandom();
            ServerWorld level = (ServerWorld) mob.level;
            for (int i = 0; i < 2; i++) {
                Vector3d pos = Vector3d.atBottomCenterOf(mob.blockPosition()).add(random.nextDouble() * 5 - 2, 0, random.nextDouble() * 5 - 2);
                pos = WorldUtils.calculateSummonPosition3(pos, level, PositionCalculators::smart);
                SoulSkeletonEntity skeleton = ModEntities.SOUL_SKELETON.create(level);
                skeleton.setOwner(mob);
                skeleton.moveTo(pos.x, pos.y, pos.z, random.nextFloat() * 360, 0);
                skeleton.finalizeSpawn(level, level.getCurrentDifficultyAt(skeleton.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
                skeleton.setPurified(mob.isPurified());
                level.addFreshEntityWithPassengers(skeleton);
                EntityUtils.spawnAnimServerside(skeleton, level);
            }
        }

        @Override
        protected int getCastingTime() {
            return 50;
        }

        @Override
        protected int getCastingInterval() {
            return 300;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSoundEvents.SOUL_SKELETON_KING_SUMMON.get();
        }

        @Override
        protected SpellType getSpell() {
            return SoulSkeletonKingEntity.SUMMON;
        }
    }
}
