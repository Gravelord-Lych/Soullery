package lych.soullery.advancements.criterion;

import lych.soullery.Soullery;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.util.ResourceLocation;

public class CraftedSoulContainerTrigger extends InstantCriterionTrigger {
    private static final ResourceLocation ID = Soullery.prefix("crafted_soul_container");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static Instance create() {
        return new Instance(ID, EntityPredicate.AndPredicate.ANY);
    }
}
