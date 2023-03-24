package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChickenReinforcement extends Reinforcement {
    private static final float FALL_DAMAGE_REDUCTION_AMOUNT = 0.18f;

    public ChickenReinforcement() {
        super(EntityType.CHICKEN);
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        ItemStack stack = entity.getItemInHand(Hand.MAIN_HAND);
        int level = getLevel(stack);
        if (level > 0) {
            event.setDamageMultiplier(event.getDamageMultiplier() * (1 - level * FALL_DAMAGE_REDUCTION_AMOUNT));
        }
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }
}
