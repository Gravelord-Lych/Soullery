package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.util.ModEffectUtils;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public class CowReinforcement extends Reinforcement {
    private static final Int2DoubleMap INVULNERABLE_PROBABILITY_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(0.18).range(2).value(0.36).range(3).value(0.54).build();

    public CowReinforcement() {
        super(EntityType.COW);
    }

    protected CowReinforcement(EntityType<?> type) {
        super(type);
    }

    protected CowReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    protected CowReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }

    @SubscribeEvent
    public void onPotionApply(PotionEvent.PotionApplicableEvent event) {
        if (ModEffectUtils.isHarmful(event.getPotionEffect())) {
            LivingEntity entity = event.getEntityLiving();
            ItemStack stack = entity.getItemInHand(Hand.MAIN_HAND);
            int level = getLevel(stack);
            if (level > 0 && event.getEntityLiving().getRandom().nextDouble() < getInvulnerableProbability(level)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    protected double getInvulnerableProbability(int level) {
        return INVULNERABLE_PROBABILITY_MAP.get(level);
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }
}
