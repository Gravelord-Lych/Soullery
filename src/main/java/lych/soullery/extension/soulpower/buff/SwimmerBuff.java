package lych.soullery.extension.soulpower.buff;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

import java.util.Map;
import java.util.UUID;

public enum SwimmerBuff implements AttributiveBuff {
    INSTANCE;

    private static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("AD6375DA-9674-AC66-B81C-4DCF4665869C");
    private static final AttributeModifier SWIM_SPEED_MODIFIER = new AttributeModifier(SWIM_SPEED_MODIFIER_UUID, "Swimmer", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public Map<Attribute, AttributeModifier> getModifiers() {
        return ImmutableMap.of(ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_MODIFIER);
    }
}
