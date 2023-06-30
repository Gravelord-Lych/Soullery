package lych.soullery.advancements.criterion;

import com.google.gson.JsonObject;
import lych.soullery.util.DefaultValues;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public abstract class InstantCriterionTrigger extends AbstractCriterionTrigger<InstantCriterionTrigger.Instance> {
    @Override
    protected Instance createInstance(JsonObject object, EntityPredicate.AndPredicate predicate, ConditionArrayParser parser) {
        return new Instance(getId(), predicate);
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, DefaultValues.alwaysTrue());
    }

    public static class Instance extends CriterionInstance {
        public Instance(ResourceLocation id, EntityPredicate.AndPredicate predicate) {
            super(id, predicate);
        }
    }
}
