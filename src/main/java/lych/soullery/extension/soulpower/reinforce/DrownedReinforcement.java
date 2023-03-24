package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DrownedReinforcement extends ZombieReinforcement {
    private static final float UNDERWATER_DAMAGE_MULTIPLIER = 0.26666667f;
    private static final float RAIN_DAMAGE_MULTIPLIER = 0.16666667f;

    public DrownedReinforcement() {
        super(EntityType.DROWNED);
    }

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {
        if (attacker.isInWater()) {
            event.setAmount(event.getAmount() * (1 + level * UNDERWATER_DAMAGE_MULTIPLIER));
        } else if (attacker.isInWaterOrRain()) {
            event.setAmount(event.getAmount() * (1 + level * RAIN_DAMAGE_MULTIPLIER));
        }
    }
}
