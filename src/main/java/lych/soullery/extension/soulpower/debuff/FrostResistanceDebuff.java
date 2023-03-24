package lych.soullery.extension.soulpower.debuff;

import lych.soullery.api.exa.MobDebuff;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum FrostResistanceDebuff implements MobDebuff {
    INSTANCE;

    @Override
    public void doWhenMobJoinWorld(MobEntity mob, World world) {}

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(MobEntity mob, ServerWorld world) {
        PlayerEntity player = mob.level.getNearestPlayer(mob.getX(), mob.getY(), mob.getZ(), ExtraAbilityConstants.FROST_RESISTANCE_SLOWDOWN_RADIUS, EntityPredicates.NO_SPECTATORS.and(entity -> entity instanceof PlayerEntity && ExtraAbility.FROST_RESISTANCE.isOn((PlayerEntity) entity)));
        if (player != null && (mob instanceof IMob || mob == player.getLastHurtMob())) {
            mob.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, ExtraAbilityConstants.FROST_RESISTANCE_MONSTER_EFFECT_DURATION, ExtraAbilityConstants.FROST_RESISTANCE_MONSTER_EFFECT_AMPLIFIER, false, false, false));
        }
    }
}
