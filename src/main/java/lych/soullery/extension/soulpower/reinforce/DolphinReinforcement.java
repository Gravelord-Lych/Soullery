package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import lych.soullery.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class DolphinReinforcement extends AttributiveReinforcement {
    private static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("F0F6F4D1-45BA-192B-9201-BAB609D4CE15");
    private static final double SWIM_SPEED_MULTIPLIER = 0.333333333333333;

    public DolphinReinforcement() {
        super(EntityType.DOLPHIN);
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        ImmutableTable.Builder<EquipmentSlotType, Attribute, AttributeModifier> builder = ImmutableTable.builder();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ArmorItem) {
            EquipmentSlotType slot = ((ArmorItem) stack.getItem()).getSlot();
            builder.put(slot, ForgeMod.SWIM_SPEED.get(), createModifier(level));
        } else {
            builder.put(EquipmentSlotType.MAINHAND, ForgeMod.SWIM_SPEED.get(), createModifier(level));
        }
        return builder.build();
    }

    private static AttributeModifier createModifier(int level) {
        return new AttributeModifier(SWIM_SPEED_MODIFIER_UUID, "Dolphin reinforcement swim speed modifier", Utils.round(level * SWIM_SPEED_MULTIPLIER, 2), AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }
}
