package lych.soullery.extension.laser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedMap;
import lych.soullery.util.Lasers;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

public class LaserData {
    public static final LaserData REDSTONE = new Builder().color(Color.RED).build();
    public static final int DEFAULT_DURABILITY = 1000;

    private static final double DEFAULT_SPACING = 0.2f;
    private final Color color;
    private final double spacing;
    private final int durability;
    private final TreeMap<LaserHitPredicate<?>, Integer> predicates = new TreeMap<>();

    private LaserData(Color color, double spacing, int durability, TreeMap<LaserHitPredicate<?>, Integer> customPredicates) {
        this.color = color;
        this.spacing = spacing;
        this.durability = durability;
        initDefaultPredicates(predicates);
        predicates.putAll(customPredicates);
    }

    private static void initDefaultPredicates(Map<LaserHitPredicate<?>, Integer> predicates) {
        predicates.put(Lasers.block(), DEFAULT_DURABILITY);
        predicates.put(Lasers.entity(), DEFAULT_DURABILITY);
        predicates.put(Lasers.fluid(), DEFAULT_DURABILITY / 50);
        predicates.put(Lasers.air(), DEFAULT_DURABILITY / 100);
    }

    public LaserData setColor(Color color) {
        Objects.requireNonNull(color, "Color should be non-null");
        return new LaserData(color, spacing, durability, predicates);
    }

    public LaserData setSpacing(double spacing) {
        Preconditions.checkArgument(spacing > 0, "Invalid spacing: " + spacing);
        return new LaserData(color, spacing, durability, predicates);
    }

    public LaserData setDurability(int durability) {
        Preconditions.checkArgument(durability > 0, "Invalid durability:" + durability);
        return new LaserData(color, spacing, durability, predicates);
    }

    public LaserData modifyPredicates(UnaryOperator<TreeMap<LaserHitPredicate<?>, Integer>> operator) {
        TreeMap<LaserHitPredicate<?>, Integer> predicatesCopy = new TreeMap<>(predicates);
        TreeMap<LaserHitPredicate<?>, Integer> result = operator.apply(predicatesCopy);
        Objects.requireNonNull(result, "Predicates should be non-null");
        return new LaserData(color, spacing, durability, result);
    }

    public LaserSource create(Vector3d vec, World world) {
        LaserSource source = new LaserSource(this, vec, world);
        Objects.requireNonNull(source.src, "LaserSource position should be non-null");
        Objects.requireNonNull(source.level, "World should be non-null");
        return source;
    }

    public Color getColor() {
        return color;
    }

    public double getSpacing() {
        return spacing;
    }

    public int getDurability() {
        return durability;
    }

    public ImmutableSortedMap<LaserHitPredicate<?>, Integer> getPredicates() {
        return ImmutableSortedMap.copyOf(predicates);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("color", color)
                .add("spacing", spacing)
                .add("durability", durability)
                .add("predicates", predicates)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaserData data = (LaserData) o;
        return Double.compare(data.spacing, spacing) == 0 && durability == data.durability && Objects.equals(color, data.color) && Objects.equals(predicates, data.predicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, spacing, durability, predicates);
    }

    public static class Builder {
        private Color color;
        private double spacing = DEFAULT_SPACING;
        private int durability = DEFAULT_DURABILITY;
        private TreeMap<LaserHitPredicate<?>, Integer> predicates = new TreeMap<>();

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder spacing(double spacing) {
            this.spacing = spacing;
            return this;
        }

        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder customPredicatesOverride(TreeMap<LaserHitPredicate<?>, Integer> customPredicates) {
            this.predicates = customPredicates;
            return this;
        }

        public Builder predicate(LaserHitPredicate<?> predicate, int durabilityCostWhenHit) {
            this.predicates.put(predicate, durabilityCostWhenHit);
            return this;
        }

        public LaserData build() {
            LaserData data = new LaserData(color, spacing, durability, predicates);
            Objects.requireNonNull(data.color, "Color should be non-null");
            Preconditions.checkArgument(data.spacing > 0, "Invalid spacing: " + data.spacing);
            Preconditions.checkArgument(data.durability > 0, "Invalid durability:" + data.durability);
            Objects.requireNonNull(data.predicates, "Predicates should be non-null");
            return data;
        }
    }
}
