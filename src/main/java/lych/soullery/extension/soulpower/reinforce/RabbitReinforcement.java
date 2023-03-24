package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import lych.soullery.entity.ModAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class RabbitReinforcement extends AttributiveReinforcement {
    private static final double BASE_AMOUNT = 0.1;
    private static final UUID JUMP_STRENGTH_MODIFIER_UUID = UUID.fromString("15014595-E571-795F-2B8B-071DF124C0F3");

    public RabbitReinforcement() {
        super(EntityType.RABBIT);
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        return ImmutableTable.of(EquipmentSlotType.FEET, ModAttributes.JUMP_STRENGTH.get(), getJumpStrengthModifier(level));
    }

    private static AttributeModifier getJumpStrengthModifier(int level) {
        return new AttributeModifier(JUMP_STRENGTH_MODIFIER_UUID, "Rabbit reinforcement jump strength modifier", BASE_AMOUNT * level, AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.FEET;
    }
}
