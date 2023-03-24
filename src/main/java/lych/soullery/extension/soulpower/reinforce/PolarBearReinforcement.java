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

public class PolarBearReinforcement extends AttributiveReinforcement {
    private static final double ADDITIONAL_ATTACK_SPEED_AMOUNT = 0.8;
    private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("70236135-2185-A82C-7902-D12F96E42663");

    public PolarBearReinforcement() {
        super(EntityType.POLAR_BEAR);
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        return ImmutableTable.of(EquipmentSlotType.MAINHAND, Attributes.ATTACK_SPEED, getModifier(level));
    }

    private static AttributeModifier getModifier(int level) {
        return new AttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Polar bear reinforcement attack speed modifier", ADDITIONAL_ATTACK_SPEED_AMOUNT * level, AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }
}
