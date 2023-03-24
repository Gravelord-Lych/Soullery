package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public enum InitialArmorBuff implements PlayerBuff {
    INSTANCE;

    private static final UUID INITIAL_ARMOR_MODIFIER_UUID = UUID.fromString("7D0582A4-090A-72EF-8620-36B7F636DC05");
    private static final UUID INITIAL_ARMOR_TOUGHNESS_MODIFIER_UUID = UUID.fromString("E2D39E77-1B4E-97EE-4B58-148E98E090B2");

    private static final AttributeModifier INITIAL_ARMOR_MODIFIER = new AttributeModifier(INITIAL_ARMOR_MODIFIER_UUID, "Initial armor", ExtraAbilityConstants.INITIAL_ARMOR_AMOUNT, Operation.ADDITION);
    private static final AttributeModifier INITIAL_ARMOR_TOUGHNESS_MODIFIER = new AttributeModifier(INITIAL_ARMOR_TOUGHNESS_MODIFIER_UUID, "Initial armor toughness", ExtraAbilityConstants.INITIAL_ARMOR_TOUGHNESS_AMOUNT, Operation.ADDITION);

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {
        EntityUtils.getAttribute(player, Attributes.ARMOR).addPermanentModifier(INITIAL_ARMOR_MODIFIER);
        EntityUtils.getAttribute(player, Attributes.ARMOR_TOUGHNESS).addPermanentModifier(INITIAL_ARMOR_TOUGHNESS_MODIFIER);
    }

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {
        EntityUtils.getAttribute(player, Attributes.ARMOR).removeModifier(INITIAL_ARMOR_MODIFIER);
        EntityUtils.getAttribute(player, Attributes.ARMOR_TOUGHNESS).removeModifier(INITIAL_ARMOR_TOUGHNESS_MODIFIER);
    }

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {}
}
