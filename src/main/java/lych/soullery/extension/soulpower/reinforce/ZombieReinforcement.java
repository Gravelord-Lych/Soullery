package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.CollectionUtils;
import lych.soullery.util.ModEffectUtils;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ZombieReinforcement extends AggressiveReinforcement {
    private static final float DAMAGE_MULTIPLIER = 0.1f;
    private static final double BASE_SPREAD_FIRE_PROBABILITY = 0.333333333333333;

    public ZombieReinforcement() {
        this(EntityType.ZOMBIE);
    }

    protected ZombieReinforcement(EntityType<?> type) {
        super(type);
    }

    protected ZombieReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    protected ZombieReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {
        event.setAmount(event.getAmount() * (1 + level * DAMAGE_MULTIPLIER));
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        if (attacker.getRandom().nextDouble() < (BASE_SPREAD_FIRE_PROBABILITY * level)) {
            if (attacker.isOnFire()) {
                target.setSecondsOnFire(2 + level);
            }
            ((IEntityMixin) target).setFireOnSelf(((IEntityMixin) attacker).getFireOnSelf());
        }
        EffectInstance effect = CollectionUtils.getRandom(attacker.getActiveEffects().stream().filter(ModEffectUtils::isHarmful).collect(Collectors.toList()), attacker.getRandom());
        if (effect != null) {
            target.addEffect(new EffectInstance(effect.getEffect(), Math.min(effect.getDuration() / 2, 100) * level, 0));
        }
    }
}
