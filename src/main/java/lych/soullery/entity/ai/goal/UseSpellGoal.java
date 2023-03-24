package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.ISpellCastable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public abstract class UseSpellGoal<T extends MobEntity & ISpellCastable> extends Goal {
    protected final T mob;
    private int attackWarmupDelay;
    private int nextAttackTickCount;

    protected UseSpellGoal(T mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target != null && target.isAlive()) {
            if (mob.isCastingSpell()) {
                return false;
            }
            if (mob.tickCount >= nextAttackTickCount) {
                return checkExtraStartConditions();
            }
            return false;
        }
        return false;
    }

    protected boolean checkExtraStartConditions() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && attackWarmupDelay > 0;
    }

    @Override
    public void start() {
        attackWarmupDelay = getCastWarmupTime();
        mob.setSpellCastingTickCount(getCastingTime());
        nextAttackTickCount = mob.tickCount + getCastingInterval();
        SoundEvent sound = getSpellPrepareSound();
        if (sound != null) {
            mob.playSound(sound, 1, 1);
        }
        mob.setCastingSpell(getSpell());
    }

    @Override
    public void tick() {
        attackWarmupDelay--;
        if (attackWarmupDelay == 0) {
            performSpellCasting();
            mob.playSound(mob.getCastingSoundEvent(), 1, 1);
        }
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    protected abstract void performSpellCasting();

    protected int getCastWarmupTime() {
        return 20;
    }

    protected abstract int getCastingTime();

    protected abstract int getCastingInterval();

    @Nullable
    protected abstract SoundEvent getSpellPrepareSound();

    protected abstract ISpellCastable.SpellType getSpell();
}
