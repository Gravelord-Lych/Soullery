package lych.soullery.extension.highlight;

import lych.soullery.api.shield.ISharedShield;
import lych.soullery.api.shield.IShieldUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MonsterViewHighlighter extends AbstractHighlighter {
    public MonsterViewHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public MonsterViewHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected Color getColor(ServerWorld level, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            float hue = MathHelper.lerp(living.getHealth() / living.getMaxHealth(), 0, 0.05f);
            if (living.getAbsorptionAmount() > 0) {
                hue += Math.min(MathHelper.lerp(living.getAbsorptionAmount() / living.getMaxHealth(), 0, 0.05f), 0.1f);
            }
            if (living instanceof IShieldUser && ((IShieldUser) living).isShieldValid()) {
                List<ISharedShield> shields = ((IShieldUser) living).getAllShields();
                if (!shields.isEmpty()) {
                    float totalHealth = (float) shields.stream().mapToDouble(ISharedShield::getHealth).sum();
                    Objects.requireNonNull(((IShieldUser) living).getMainShield(), "Main shield must not be null!");
                    float passiveDefense = ((IShieldUser) living).getMainShield().getPassiveDefense();
                    hue = periodicallyLerp(Math.max(-0.2f, 1 - totalHealth / passiveDefense), 0.5833333f, hue);
                }
            }
            float saturation = 0.8f;
            float brightness = MathHelper.lerp(living.getHealth() / living.getMaxHealth(), 0.5f, 0.9f);
            return new Color(Color.HSBtoRGB(hue, saturation, brightness));
        }
        return Color.RED;
    }

    private static float periodicallyLerp(float amount, float min, float max) {
        float value = MathHelper.lerp(amount, min, max + 1);
        if (value >= 1) {
            value--;
        }
        return value;
    }

    @Override
    protected float @Nullable [] doGetMixColor(ServerWorld level, Entity entity) {
        float[] mixColor = super.doGetMixColor(level, entity);
        if (mixColor != null) {
            mixColor[0] = Float.NaN;
            mixColor[2] = Math.max(0, mixColor[2] * 2 - 1);
        }
        return mixColor;
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.MONSTER_VIEW;
    }

    @Override
    public int getPriority() {
        return 2000;
    }
}
