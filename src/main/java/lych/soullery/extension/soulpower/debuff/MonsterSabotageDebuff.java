package lych.soullery.extension.soulpower.debuff;

import lych.soullery.api.exa.MobDebuff;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public enum MonsterSabotageDebuff implements MobDebuff {
    INSTANCE;

    private static final UUID MONSTER_SABOTAGE_UUID = UUID.fromString("53A004B3-95AC-13C7-58E2-0802809E02D5");
    private static final AttributeModifier MONSTER_SABOTAGE = new AttributeModifier(MONSTER_SABOTAGE_UUID, "Monster sabotage", ExtraAbilityConstants.MONSTER_SABOTAGE_AMOUNT, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public void doWhenMobJoinWorld(MobEntity mob, World world) {
        ModifiableAttributeInstance maxHealth = mob.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null || !(mob instanceof IMob)) {
            return;
        }
        if (!maxHealth.hasModifier(MONSTER_SABOTAGE)) {
            maxHealth.addPermanentModifier(MONSTER_SABOTAGE);
        }
    }

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(MobEntity mob, ServerWorld world) {}
}
