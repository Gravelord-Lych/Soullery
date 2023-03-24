package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class VindicatorReinforcement extends AggressiveReinforcement {
    private static final RangedInteger PLAYER_ITEM_BREAK_RANGE = RangedInteger.of(32, 64);
    private static final double BASE_DESTROY_PROBABILITY = 0.1;

    public VindicatorReinforcement() {
        super(EntityType.VINDICATOR);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {
        if (attacker.getRandom().nextDouble() < BASE_DESTROY_PROBABILITY * level && target.canChangeDimensions()) {
            ItemStack targetItem = target.getMainHandItem();
            if (targetItem.isDamageableItem()) {
                if (target instanceof PlayerEntity) {
                    targetItem.hurtAndBreak(PLAYER_ITEM_BREAK_RANGE.randomValue(attacker.getRandom()), target, p -> p.broadcastBreakEvent(Hand.MAIN_HAND));
                } else {
                    targetItem.shrink(1);
                }
            }
        }
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}
}