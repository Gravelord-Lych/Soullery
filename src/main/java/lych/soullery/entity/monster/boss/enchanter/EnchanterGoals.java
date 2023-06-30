package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.entity.ai.goal.IPhaseableGoal;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public final class EnchanterGoals {
    private EnchanterGoals() {}

    public static class HideGoal extends EnchanterGoal {
        private static final RangedInteger RANDOM_INTERVAL_RANGE = RangedInteger.of(20, 30);
        private int randomInterval = 5;
        private int summonCount;

        public HideGoal(EnchanterEntity enchanter) {
            super(enchanter);
        }

        @Override
        public void start() {
            super.start();
            enchanter.setEthereallyInvulnerable(true);
        }

        @Override
        public void tick() {
            super.tick();
            enchanter.getLookControl().setLookAt(target, 60, 60);
            if (randomInterval > 0) {
                randomInterval--;
            } else {
                if (enchanter.countEAS() <= 20) {
                    EnchantedArmorStandEntity eas = enchanter.summonEAS(enchanter.getEASTypePickerList().findRandomTypePicker(target), target);
                    eas.swing(Hand.MAIN_HAND);
                    eas.playSound(ModSoundEvents.ENCHANTER_SUMMON.get(), 0.6f, 1);
                }
                randomInterval = RANDOM_INTERVAL_RANGE.randomValue(enchanter.getRandom());
                summonCount++;
            }
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (!EntityUtils.isAlive(target)) {
                return StopReason.NO_TARGET;
            }
            if (summonCount > 7 && enchanter.getRandom().nextInt(Math.max(1, 16 - summonCount)) == 0) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }

        @Override
        public void stop() {
            super.stop();
            enchanter.setEthereallyInvulnerable(false);
            summonCount = 0;
            randomInterval = 5;
        }
    }

    public static class AttackGoal extends EnchanterGoal {
        private EnchanterSkill skill;
        private int ticks;
        private boolean stopEarly;

        public AttackGoal(EnchanterEntity enchanter) {
            super(enchanter);
        }

        @Override
        public void start() {
            super.start();
            enchanter.setCountHurtTimes(true);
            enchanter.summonEASNearby(2, 6);
            enchanter.playSound(ModSoundEvents.ENCHANTER_APPEAR.get(), 1.2f, 1);
        }

        @Override
        public void tick() {
            super.tick();
            enchanter.getLookControl().setLookAt(target, 90, 90);
            ticks++;
            if (ticks == 60) {
                EnchanterSkill skill = enchanter.getEnchanterSkillList().findRandomSkillOrNull(target);
                if (skill == null) {
                    stopEarly = true;
                    return;
                }
                this.skill = skill;
                target.sendMessage(skill.getSkillText(target).copy().withStyle(Style.EMPTY.withColor(Color.fromRgb(enchanter.getSkillMessageColor()))), Util.NIL_UUID);
            }
            if (ticks == 80) {
                skill.performSkill(enchanter, target);
                enchanter.playSound(skill.getSound(), 1.2f, 1);
            }
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (!EntityUtils.isAlive(target)) {
                return StopReason.NO_TARGET;
            }
            if (stopEarly || ticks > 100) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }

        @Override
        public void stop() {
            super.stop();
            enchanter.setCountHurtTimes(false);
            ticks = 0;
            stopEarly = false;
        }
    }

    public static abstract class EnchanterGoal extends Goal implements IPhaseableGoal {
        protected final EnchanterEntity enchanter;
        protected LivingEntity target;

        protected EnchanterGoal(EnchanterEntity enchanter) {
            this.enchanter = enchanter;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (!EntityUtils.isAlive(enchanter.getTarget())) {
                return StopReason.NO_TARGET;
            }
            this.target = enchanter.getTarget();
            return null;
        }

        @Override
        public final boolean canUse() {
            return false;
        }

        @Override
        public final boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            super.start();
            enchanter.resetHurtTimes();
        }

        @Override
        public void stop() {
            super.stop();
            target = null;
        }
    }
}
