package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class HuskReinforcement extends ZombieReinforcement {
    private static final Int2DoubleMap RESTORE_PROBABILITY_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(0.33).range(2).value(0.67).range(3).value(1).build();
    private static final int FOOD_LEVEL = 1;

    public HuskReinforcement() {
        super(EntityType.HUSK);
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        super.onDamage(stack, attacker, target, level, event);
        if (attacker instanceof PlayerEntity && attacker.getRandom().nextDouble() < RESTORE_PROBABILITY_MAP.get(level)) {
            ((PlayerEntity) attacker).getFoodData().eat(FOOD_LEVEL, 0);
        }
    }

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
        return super.isCompatibleWith(reinforcement) && reinforcement != Reinforcements.ZOMBIE;
    }
}
