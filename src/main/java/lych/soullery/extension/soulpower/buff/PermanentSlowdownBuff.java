package lych.soullery.extension.soulpower.buff;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public enum PermanentSlowdownBuff implements DamageBuff {
    INSTANCE;

    private static final UUID PERMANENT_SLOWDOWN_UUID = UUID.fromString("284F3078-47BF-26E2-29FE-FD839861C9E3");
    private static final AttributeModifier PERMANENT_SLOWDOWN = new AttributeModifier(PERMANENT_SLOWDOWN_UUID, "Permanent slowdown by Exa", ExtraAbilityConstants.PERMANENT_SLOWDOWN_AMOUNT, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {
        if (event.isSuccessfullyHurt() && event.getEntityLiving() instanceof MobEntity && event.getEntityLiving() instanceof IMob && event.getEntityLiving().canChangeDimensions()) {
            if (EntityUtils.addPermanentModifierIfAbsent(event.getEntityLiving(), Attributes.MOVEMENT_SPEED, PERMANENT_SLOWDOWN)) {
                if (!player.level.isClientSide()) {
                    EntityUtils.addParticlesAroundSelfServerside(event.getEntityLiving(), (ServerWorld) player.level, ParticleTypes.SMOKE, 4 + player.getRandom().nextInt(3));
                }
            }
        }
    }
}
