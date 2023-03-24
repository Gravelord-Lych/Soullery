package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.EntityUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CreeperReinforcement extends AggressiveReinforcement {
    private static final double EXPLOSION_DAMAGE_PROBABILITY = 0.166666666666667;
    private static final double MAX_EXPLOSION_DAMAGE_PROBABILITY = 0.5;

    public CreeperReinforcement() {
        super(EntityType.CREEPER);
    }

    @Override
    public boolean isItemPosSuitable(ItemStack stack) {
        return super.isItemPosSuitable(stack) || stack.getItem() instanceof BowItem;
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {
        int count = 0;
        for (LivingEntity entity : attacker.level.getEntitiesOfClass(LivingEntity.class, attacker.getBoundingBox().inflate(1 + level))) {
            if (canDoAreaOfEffectDamageTo(entity, attacker, target)) {
                if (entity.hurt(EntityUtils.livingAttack(attacker).setExplosion(), event.getAmount() / 2)) {
                    count++;
                    int fireLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
                    fireLevel = Math.max(fireLevel, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack));
                    if (fireLevel > 0) {
                        entity.setSecondsOnFire(fireLevel * 2);
                    }
                }
            }
        }
        if (count > 0 && attacker.level instanceof ServerWorld) {
            ((ServerWorld) attacker.level).sendParticles(ParticleTypes.EXPLOSION,
                    target.getRandomX(1),
                    target.getY(0.4 + target.getRandom().nextDouble() * 0.2),
                    target.getRandomZ(1),
                    1,
                    0,
                    0,
                    0,
                    0);
        }
//      Creeper Reinforcement has a strong synergy with multiple reinforcements, so add a small nerf to it.
        mayHurtSelf(attacker, count);
    }

    private static boolean canDoAreaOfEffectDamageTo(LivingEntity entity, LivingEntity attacker, LivingEntity target) {
        return entity != attacker && entity != target && entity != attacker.getVehicle() && !attacker.getPassengers().contains(entity);
    }

    private static void mayHurtSelf(LivingEntity attacker, int count) {
        if (EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(attacker) && attacker.getRandom().nextDouble() < Math.min(MAX_EXPLOSION_DAMAGE_PROBABILITY, EXPLOSION_DAMAGE_PROBABILITY * count)) {
            attacker.hurt(DamageSource.explosion((LivingEntity) null), 1);
        }
    }

    @Override
    protected boolean allowsDamageSource(DamageSource source) {
        return (super.allowsDamageSource(source) || source.isProjectile()) && !source.isExplosion() && !source.isMagic();
    }

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}
}
