package lych.soullery.entity.monster.boss.enchanter;

import com.google.common.primitives.Ints;
import lych.soullery.util.WeightedRandom;
import lych.soullery.util.selection.Selection;
import lych.soullery.util.selection.Selections;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public abstract class EASTypePicker implements WeightedRandom.Item {
    private final EASType type;
    private final Selection<EASPlacement> placements;
    private final int baseWeight;
    private int weight;

    protected EASTypePicker(EASType type, int baseWeight, EASPlacement onlyPlacement) {
        this(type, baseWeight, WeightedRandom.makeItem(onlyPlacement, 1));
    }

    @SafeVarargs
    protected EASTypePicker(EASType type, int baseWeight, WeightedRandom.ItemImpl<EASPlacement>... placements) {
        this.type = type;
        this.baseWeight = baseWeight;
        this.placements = Selections.selection(placements);
    }

    public Vector3d getRandomPos(EnchanterEntity enchanter, LivingEntity target) {
        if (placements.getAll().isEmpty()) {
            throw new IllegalStateException("PlacementSelection is empty!");
        }
        EASPlacement placement = placements.getRandom(enchanter.getRandom());
        Vector3d pos = placement.calculatePositionToSummon(enchanter, target);
        if (pos != null) {
            return pos;
        }
        return failsafePosition(enchanter, target);
    }

    private static Vector3d failsafePosition(EnchanterEntity enchanter, LivingEntity target) {
        return enchanter.position().add(enchanter.position().vectorTo(target.position()).scale(0.5));
    }

    public void updateWeight(EnchanterEntity enchanter, LivingEntity target) {
        weight = Ints.saturatedCast(Math.round(baseWeight * getWeightMultiplier(enchanter, target)));
    }

    protected abstract double getWeightMultiplier(EnchanterEntity enchanter, LivingEntity target);

    public EASType getType() {
        return type;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
