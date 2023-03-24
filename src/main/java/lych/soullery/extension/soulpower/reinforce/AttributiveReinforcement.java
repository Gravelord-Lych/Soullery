package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.Table;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public abstract class AttributiveReinforcement extends Reinforcement {
    public AttributiveReinforcement(EntityType<?> type) {
        super(type);
    }

    public AttributiveReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public AttributiveReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }

    @SubscribeEvent
    public void onCalculateAttributeModifiers(ItemAttributeModifierEvent event) {
        int level = getLevel(event.getItemStack());
        if (level > 0) {
            getAttributeModifiers(event, level).row(event.getSlotType()).forEach(event::addModifier);
        }
    }

    protected abstract Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level);
}
