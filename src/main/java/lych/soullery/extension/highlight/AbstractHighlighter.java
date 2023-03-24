package lych.soullery.extension.highlight;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractHighlighter implements Highlighter {
    protected final UUID entityUUID;
    private long highlightTicksRemaining;

    public AbstractHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        this.entityUUID = entityUUID;
        this.highlightTicksRemaining = highlightTicksRemaining;
    }

    public AbstractHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        this.entityUUID = entityUUID;
        this.highlightTicksRemaining = compoundNBT.getLong("HighlightTicksRemaining");
    }

    @Nullable
    @Override
    public Color getColor(ServerWorld level, PriorityQueue<Highlighter> queue) {
        if (highlightTicksRemaining >= 0) {
            highlightTicksRemaining--;
        }
        Entity entity = level.getEntity(entityUUID);
        if (entity == null) {
            return Color.BLACK;
        }
        if (highlightTicksRemaining < 0 || !EntityUtils.isAlive(entity)) {
            return null;
        }
        Color color = getColor(level, entity);
        queue.remove(this);
        if (!queue.isEmpty()) {
            color = mix(color, level, entity, queue);
        }
        return color;
    }

    @Nullable
    protected static Color mix(@Nullable Color color, ServerWorld level, Entity entity, Collection<Highlighter> highlighters) {
        ImmutableList.Builder<float[]> builder = ImmutableList.builder();
        if (color != null) {
            builder.add(Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null));
        }
        ImmutableList<float[]> list = builder
                .addAll(highlighters.stream()
                        .map(highlighter -> highlighter.getMixColor(level, entity))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();
        if (list.isEmpty()) {
            return null;
        }
        return reduce(list);
    }

    protected static Color reduce(List<float[]> colors) {
        float[] hsb = colors.stream().reduce((a1, a2) -> new float[]{getHue(a1[0], a2[0]), (a1[1] + a2[1]) / 2, (a1[2] + a2[2]) / 2}).orElseThrow(() -> new IllegalArgumentException("Colors cannot be empty"));
        return asColor(hsb);
    }

    protected static float getHue(float h1, float h2) {
        if (Float.isNaN(h1) && Float.isNaN(h2)) {
            return 0;
        }
        if (Float.isNaN(h1)) {
            return h2;
        }
        if (Float.isNaN(h2)) {
            return h1;
        }
        return (h1 + h2) / 2;
    }

    @NotNull
    protected static Color asColor(float[] hsb) {
        return new Color(Color.HSBtoRGB(Float.isFinite(hsb[0]) ? hsb[0] : 0, hsb[1], hsb[2]));
    }

    @Override
    public long getHighlightTicks() {
        return highlightTicksRemaining;
    }

    @Override
    public void setHighlightTicks(long highlightTicks) {
        this.highlightTicksRemaining = highlightTicks;
    }

    @Nullable
    protected abstract Color getColor(ServerWorld level, Entity entity);

    @Override
    public final float @Nullable [] getMixColor(ServerWorld level, Entity entity) {
        float[] defaultColor = getDefaultColor(level, entity);
        if (defaultColor == null) {
            return null;
        }
        defaultColor[1] /= 2;
        return defaultColor;
    }

    protected float @Nullable [] getDefaultColor(ServerWorld level, Entity entity) {
        Color color = getColor(level, entity);
        if (color == null) {
            return null;
        }
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

    @Override
    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putLong("HighlightTicksRemaining", highlightTicksRemaining);
        addAdditionalSaveData(compoundNBT);
        return compoundNBT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractHighlighter)) return false;
        AbstractHighlighter that = (AbstractHighlighter) o;
        return highlightTicksRemaining == that.highlightTicksRemaining && Objects.equals(entityUUID, that.entityUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityUUID, highlightTicksRemaining);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("entityUUID", entityUUID)
                .add("highlightTicksRemaining", highlightTicksRemaining);
    }
}
