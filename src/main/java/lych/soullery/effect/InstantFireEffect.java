package lych.soullery.effect;

import lych.soullery.extension.fire.Fire;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import org.jetbrains.annotations.Nullable;

public class InstantFireEffect extends CommonEffect {
    private final Fire fire;

    public InstantFireEffect(EffectType category, int color, Fire fire) {
        super(category, color);
        this.fire = fire;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity directAttacker, @Nullable Entity attacker, LivingEntity target, int amplifier, double strengthModifier) {
        target.setSecondsOnFire((int) ((6 + amplifier * 4) * strengthModifier));
        ((IEntityMixin) target).setFireOnSelf(fire);
    }

    @Override
    public void applyEffectTick(LivingEntity target, int amplifier) {
        target.setSecondsOnFire(6 + amplifier * 4);
        ((IEntityMixin) target).setFireOnSelf(fire);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration >= 1;
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }
}
