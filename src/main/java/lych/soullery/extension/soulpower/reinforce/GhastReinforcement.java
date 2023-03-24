package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class GhastReinforcement extends AttributiveReinforcement {
    private static final float DAMAGE_REDUCTION_AMOUNT = 0.1f;
    private static final double GRAVITY_REDUCTION_AMOUNT = 0.01;
    private static final UUID GRAVITY_MODIFIER_UUID = UUID.fromString("E5969713-AA51-3DA9-EB7D-E1B490C6ADBF");

    public GhastReinforcement() {
        super(EntityType.GHAST);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        int level = getLevel(entity.getMainHandItem());
        if (level > 0) {
            event.setAmount(event.getAmount() * (1 - level * DAMAGE_REDUCTION_AMOUNT));
        }
    }

    @Override
    protected Table<EquipmentSlotType, Attribute, AttributeModifier> getAttributeModifiers(ItemAttributeModifierEvent event, int level) {
        return ImmutableTable.of(EquipmentSlotType.MAINHAND, ForgeMod.ENTITY_GRAVITY.get(), getModifier(level));
    }

    private static AttributeModifier getModifier(int level) {
        return new AttributeModifier(GRAVITY_MODIFIER_UUID, "Ghast reinforcement gravity modifier", -GRAVITY_REDUCTION_AMOUNT * level, AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof ToolItem;
    }
}
