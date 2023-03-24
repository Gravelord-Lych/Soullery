package lych.soullery.entity.ai.goal;

import lych.soullery.entity.iface.ISpellCastable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

import static lych.soullery.entity.iface.ISpellCastable.SpellType.NONE;

public class CastingSpellGoal<T extends MobEntity & ISpellCastable> extends Goal {
    private final T mob;

    public CastingSpellGoal(T mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getSpellCastingTickCount() > 0;
    }

    @Override
    public void start() {
        super.start();
        mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        super.stop();
        mob.setCastingSpell(NONE);
    }

    @Override
    public void tick() {
        if (mob.getTarget() != null) {
            mob.getLookControl().setLookAt(mob.getTarget(), mob.getMaxHeadYRot(), mob.getMaxHeadXRot());
        }
    }
}
