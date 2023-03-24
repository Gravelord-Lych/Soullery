package lych.soullery.extension.soulpower.buff;

import com.google.common.collect.ImmutableMap;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

import java.util.Map;
import java.util.UUID;

public enum UltrareachBuff implements AttributiveBuff {
    INSTANCE;

    private static final UUID REACH_DISTANCE_MODIFIER_UUID = UUID.fromString("25642659-CFE9-E8F3-8367-AD3308983F68");
    private static final AttributeModifier REACH_DISTANCE_MODIFIER = new AttributeModifier(REACH_DISTANCE_MODIFIER_UUID, "Ultrareach", ExtraAbilityConstants.ULTRAREACH_AMOUNT, AttributeModifier.Operation.ADDITION);

    @Override
    public Map<Attribute, AttributeModifier> getModifiers() {
        return ImmutableMap.of(ForgeMod.REACH_DISTANCE.get(), REACH_DISTANCE_MODIFIER);
    }
}
