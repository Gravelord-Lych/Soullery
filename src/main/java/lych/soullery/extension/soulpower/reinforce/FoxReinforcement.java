package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class FoxReinforcement extends AttributiveReinforcement {
    private static final int REACH_DISTANCE_ADDITION = 1;
    private static final UUID REACH_DISTANCE_UUID = UUID.fromString("8E35AAD1-7079-DD84-67D8-215A959F918F");

    public FoxReinforcement() {
        super(EntityType.FOX);
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        return ImmutableTable.of(EquipmentSlotType.MAINHAND, ForgeMod.REACH_DISTANCE.get(), getModifier(level));
    }

    private static AttributeModifier getModifier(int level) {
        return new AttributeModifier(REACH_DISTANCE_UUID, "Fox reinforcement reach distance modifier", REACH_DISTANCE_ADDITION * level, AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }
}
