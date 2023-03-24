package lych.soullery.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;

@Immutable
public class ImmutableEffectInstance extends EffectInstance {
    public ImmutableEffectInstance(Effect effect) {
        super(effect);
    }

    public ImmutableEffectInstance(Effect effect, int duration) {
        super(effect, duration);
    }

    public ImmutableEffectInstance(Effect effect, int duration, int amplifier) {
        super(effect, duration, amplifier);
    }

    public ImmutableEffectInstance(Effect effect, int duration, int amplifier, boolean ambient, boolean visible) {
        super(effect, duration, amplifier, ambient, visible);
    }

    public ImmutableEffectInstance(Effect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        super(effect, duration, amplifier, ambient, visible, showIcon);
    }

    public ImmutableEffectInstance(Effect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon, @Nullable EffectInstance hiddenEffect) {
        super(effect, duration, amplifier, ambient, visible, showIcon, hiddenEffect);
    }

    public ImmutableEffectInstance(EffectInstance effectInstance) {
        super(effectInstance);
    }

    public EffectInstance copy() {
        return new EffectInstance(this);
    }

    @Override
    public boolean update(EffectInstance effect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tick(LivingEntity entity, Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyEffect(LivingEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNoCounter(boolean noCounter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.unmodifiableList(super.getCurativeItems());
    }

    @Override
    public void setCurativeItems(List<ItemStack> curativeItems) {
        throw new UnsupportedOperationException();
    }
}
