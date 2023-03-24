package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.mixin.ILivingEntityMixin;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ZoglinReinforcement extends AggressiveReinforcement {
    private static final double BASE_PUSH_STRENGTH = 0.15;

    public ZoglinReinforcement() {
        super(EntityType.ZOGLIN);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

//  Classified discussion due to vanilla hardcode.
    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, stack) == 0 && Reinforcements.HOGLIN.getLevel(stack) == 0) {
            ((ILivingEntityMixin) target).setKnockupStrength(BASE_PUSH_STRENGTH * level);
        }
    }

    @Override
    protected void postHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, PostLivingHurtEvent event) {
        super.postHurt(stack, attacker, target, level, event);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, stack) > 0 || Reinforcements.HOGLIN.getLevel(stack) > 0) {
            ((ILivingEntityMixin) target).setKnockupStrength(BASE_PUSH_STRENGTH * level);
        }
    }

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
//      Prevents too powerful knockback (Enchantment Knockback II + Hoglin III + Zoglin III).
        return super.isCompatibleWith(reinforcement) && reinforcement != Reinforcements.HOGLIN;
    }
}
