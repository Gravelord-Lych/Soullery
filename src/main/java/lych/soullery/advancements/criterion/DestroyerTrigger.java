package lych.soullery.advancements.criterion;

import lych.soullery.Soullery;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.util.ResourceLocation;

public class DestroyerTrigger extends InstantCriterionTrigger {
    private static final ResourceLocation ID = Soullery.prefix("destroyer");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static InstantCriterionTrigger.Instance create() {
        return new InstantCriterionTrigger.Instance(ID, EntityPredicate.AndPredicate.ANY);
    }
}
