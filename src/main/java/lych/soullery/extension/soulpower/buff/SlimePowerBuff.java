package lych.soullery.extension.soulpower.buff;

import com.google.common.collect.ImmutableMap;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.UUID;

public enum SlimePowerBuff implements AttributiveBuff {
    INSTANCE;

    private static final UUID STICKY_EFFECT_UUID = UUID.fromString("4C262BA9-F55D-592F-E710-AFD2A0012A6B");
    private static final UUID STICKY_EFFECT_NERF_UUID = UUID.fromString("B92F68C7-461B-4080-1747-47DDCA14D525");
    private static final AttributeModifier STICKY_EFFECT = new AttributeModifier(STICKY_EFFECT_UUID, "Sticky effect", ExtraAbilityConstants.SLIME_POWER_STICKY_EFFECT_ADDITIONAL_KNOCKBACK_STRENGTH, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier STICKY_EFFECT_NERF = new AttributeModifier(STICKY_EFFECT_NERF_UUID, "Sticky effect speed nerf", ExtraAbilityConstants.SLIME_POWER_STICKY_EFFECT_SPEED_NERF, AttributeModifier.Operation.MULTIPLY_BASE);

    @Override
    public Map<Attribute, AttributeModifier> getModifiers() {
        return ImmutableMap.of(Attributes.KNOCKBACK_RESISTANCE, STICKY_EFFECT, Attributes.MOVEMENT_SPEED, STICKY_EFFECT_NERF);
    }
}
