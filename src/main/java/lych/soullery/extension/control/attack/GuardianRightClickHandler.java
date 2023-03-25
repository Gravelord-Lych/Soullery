package lych.soullery.extension.control.attack;

import lych.soullery.Soullery;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.mixin.GuardianEntityAccessor;
import lych.soullery.util.EntityEventConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

public class GuardianRightClickHandler implements TargetNeededRightClickHandler<GuardianEntity> {
    private int cooldown = 10;
    private LivingEntity cachedTarget;

    @Override
    public void handleRightClick(GuardianEntity operatingGuardian, LivingEntity target, ServerPlayerEntity player, CompoundNBT data) {
        if (cooldown == 0) {
            cachedTarget = target;
            cooldown = -((operatingGuardian.getAttackDuration() << 1) / 3);
            ((GuardianEntityAccessor) operatingGuardian).callSetActiveAttackTarget(cachedTarget.getId());
            if (!operatingGuardian.isSilent()) {
                operatingGuardian.level.broadcastEntityEvent(operatingGuardian, EntityEventConstants.GUARDIAN_ATTACK);
            }
        }
    }

    @Override
    public void tick(GuardianEntity operatingGuardian, ServerPlayerEntity player, CompoundNBT data) {
        TargetNeededRightClickHandler.super.tick(operatingGuardian, player, data);
        if (cooldown > 0) {
            cooldown--;
        } else if (cooldown < -1) {
            cooldown++;
        } else if (cooldown == -1) {
            attack(operatingGuardian);
            ((GuardianEntityAccessor) operatingGuardian).callSetActiveAttackTarget(0);
            cachedTarget = null;
            cooldown = 40;
        }
    }

    private void attack(GuardianEntity operatingGuardian) {
        float magicDamage = 1;
        if (operatingGuardian.level.getDifficulty() == Difficulty.HARD) {
            magicDamage += 2;
        }
        if (operatingGuardian instanceof ElderGuardianEntity) {
            magicDamage += 2;
        }

        if (cachedTarget != null) {
            cachedTarget.hurt(DamageSource.indirectMagic(operatingGuardian, operatingGuardian), magicDamage);
            cachedTarget.hurt(DamageSource.mobAttack(operatingGuardian), (float) operatingGuardian.getAttributeValue(Attributes.ATTACK_DAMAGE));
        } else {
            Soullery.LOGGER.warn(MindOperator.MARKER, "Unable to find cachedTarget for {}", operatingGuardian.getType().getRegistryName());
        }
    }

    @Override
    public void saveTo(CompoundNBT data) {
        if (cooldown > 0) {
            data.putInt("GuardianAttackCooldown", cooldown);
        }
    }

    @Override
    public void loadFrom(CompoundNBT data) {
        if (data.contains("GuardianAttackCooldown")) {
            cooldown = data.getInt("GuardianAttackCooldown");
        }
    }

    public void reset() {
        cooldown = 10;
        cachedTarget = null;
    }
}
