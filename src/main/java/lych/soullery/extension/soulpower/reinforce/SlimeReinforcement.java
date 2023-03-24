package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.EntityUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class SlimeReinforcement extends AggressiveReinforcement {
//  Marker attribute modifier. This reinforcement will be OVERRIDDEN by Knockback enchantment
    private static final UUID SLIME_REINFORCEMENT_KNOCKBACK_RESISTANCE_MODIFIER_UUID = UUID.fromString("525C4F1A-9544-88A4-5720-90444C45BA74");
    private static final AttributeModifier SLIME_REINFORCEMENT_KNOCKBACK_RESISTANCE_MODIFIER = new AttributeModifier(SLIME_REINFORCEMENT_KNOCKBACK_RESISTANCE_MODIFIER_UUID,
            "Slime reinforcement knockback resistance modifier",
            1,
            AttributeModifier.Operation.MULTIPLY_TOTAL);

    public SlimeReinforcement() {
        super(EntityType.SLIME);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, stack) == 0 && !EntityUtils.getAttribute(event.getEntityLiving(), Attributes.KNOCKBACK_RESISTANCE).hasModifier(SLIME_REINFORCEMENT_KNOCKBACK_RESISTANCE_MODIFIER)) {
            EntityUtils.getAttribute(target, Attributes.KNOCKBACK_RESISTANCE).addTransientModifier(SLIME_REINFORCEMENT_KNOCKBACK_RESISTANCE_MODIFIER);
        }
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}

    @SubscribeEvent
    public void onLivingKnockback(LivingKnockBackEvent event) {
        if (EntityUtils.getAttribute(event.getEntityLiving(), Attributes.KNOCKBACK_RESISTANCE).hasModifier(SLIME_REINFORCEMENT_KNOCKBACK_RESISTANCE_MODIFIER)) {
            event.setCanceled(true);
            EntityUtils.getAttribute(event.getEntityLiving(), Attributes.KNOCKBACK_RESISTANCE).removeModifier(SLIME_REINFORCEMENT_KNOCKBACK_RESISTANCE_MODIFIER);
        }
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
        return super.isCompatibleWith(reinforcement) && reinforcement != Reinforcements.HOGLIN && reinforcement != Reinforcements.ZOGLIN;
    }
}
