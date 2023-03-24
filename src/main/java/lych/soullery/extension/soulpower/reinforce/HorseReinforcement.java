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
import net.minecraft.item.ToolItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class HorseReinforcement extends AttributiveReinforcement {
    private static final double SPEED_MULTIPLIER = 0.15;
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("18A42F2D-932E-638D-CC8C-B8DAE00230C5");
    private final boolean special;

    public HorseReinforcement(EntityType<?> type) {
        super(type);
        special = false;
    }

    public HorseReinforcement(ResourceLocation typeName) {
        super(typeName);
        special = false;
    }

    public HorseReinforcement(Supplier<EntityType<?>> type) {
        this(type, false);
    }

    private HorseReinforcement(Supplier<EntityType<?>> type, boolean special) {
        super(type);
        this.special = special;
    }

    public HorseReinforcement setSpecial() {
        return new HorseReinforcement(this::getType, true);
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        return ImmutableTable.of(EquipmentSlotType.MAINHAND, Attributes.MOVEMENT_SPEED, getModifier(level));
    }

    private static AttributeModifier getModifier(int level) {
        return new AttributeModifier(SPEED_MODIFIER_UUID, "Horse reinforcement speed modifier", SPEED_MULTIPLIER * level, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof ToolItem;
    }

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
        return super.isCompatibleWith(reinforcement) && !(reinforcement instanceof HorseReinforcement);
    }
}
