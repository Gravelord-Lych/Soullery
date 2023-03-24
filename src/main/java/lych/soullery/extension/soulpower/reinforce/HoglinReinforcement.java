package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class HoglinReinforcement extends AttributiveReinforcement {
    private static final double ATTACK_KNOCKBACK_MULTIPLIER = 0.8;
    private static final UUID ATTACK_KNOCKBACK_MODIFIER_UUID = UUID.fromString("4FC7BE3E-E159-8C9F-4C0D-3481475E7DCC");

    public HoglinReinforcement() {
        super(EntityType.HOGLIN);
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        return ImmutableTable.of(EquipmentSlotType.MAINHAND, Attributes.ATTACK_KNOCKBACK, getModifier(level));
    }

    private static AttributeModifier getModifier(int level) {
        return new AttributeModifier(ATTACK_KNOCKBACK_MODIFIER_UUID, "Hoglin reinforcement attack knockback modifier", level * ATTACK_KNOCKBACK_MULTIPLIER, AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }
}
