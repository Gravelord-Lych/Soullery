package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class TurtleReinforcement extends AttributiveReinforcement {
    private static final double GRAVITY_ADDITION_AMOUNT = 0.01;
    private static final double SPEED_REDUCTION_AMOUNT = -0.15;
    private static final Int2FloatMap DAMAGE_MULTIPLIER_MAP = EntityUtils.floatChoiceBuilder().range(1).value(0.7f).range(2).value(0.45f).range(3).value(0.3f).range(4).value(0.2f).build();
    private static final UUID[] GRAVITY_MODIFIER_UUID = new UUID[]{UUID.fromString("50868DF0-6FE9-B6AD-7958-D5CCF39058A8"), UUID.fromString("4BBB267D-C6CD-CD81-B577-08107070EE4A"), UUID.fromString("D30F9367-5462-EB4E-180C-3E1F55594EDE"), UUID.fromString("8E8D8453-840F-C640-3567-AB322C9968B9")};
    private static final UUID[] SPEED_MODIFIER_UUID = new UUID[]{UUID.fromString("DF883062-24D2-F4FF-1EC1-63B4332822CB"), UUID.fromString("9A39E4D2-E5BB-63FF-AAC7-4C8884462C1A"), UUID.fromString("4183B6A4-481D-4321-6FEB-7682587A7F31"), UUID.fromString("CF38BDAD-E37D-DBFC-EC9D-6A70C089EE4F")};

    public TurtleReinforcement() {
        super(EntityType.TURTLE);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        int level = getTotalLevel(entity.getArmorSlots());
        if (level > 0) {
            event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER_MAP.get(level));
        }
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        EquipmentSlotType slot = ((ArmorItem) event.getItemStack().getItem()).getSlot();
        ImmutableTable.Builder<EquipmentSlotType, Attribute, AttributeModifier> builder = ImmutableTable.builder();
        return builder.put(slot, ForgeMod.ENTITY_GRAVITY.get(), getGravityModifier(slot)).put(slot, Attributes.MOVEMENT_SPEED, getSpeedModifier(slot)).build();
    }

    private static AttributeModifier getGravityModifier(EquipmentSlotType type) {
        return new AttributeModifier(GRAVITY_MODIFIER_UUID[type.getIndex()], "Turtle reinforcement gravity modifier", GRAVITY_ADDITION_AMOUNT, AttributeModifier.Operation.ADDITION);
    }

    private static AttributeModifier getSpeedModifier(EquipmentSlotType type) {
        return new AttributeModifier(SPEED_MODIFIER_UUID[type.getIndex()], "Turtle reinforcement speed modifier", SPEED_REDUCTION_AMOUNT, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
