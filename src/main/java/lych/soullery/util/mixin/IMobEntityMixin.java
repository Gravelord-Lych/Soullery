package lych.soullery.util.mixin;

import net.minecraft.inventory.EquipmentSlotType;

public interface IMobEntityMixin {
    float callGetEquipmentDropChance(EquipmentSlotType slotType);
}
