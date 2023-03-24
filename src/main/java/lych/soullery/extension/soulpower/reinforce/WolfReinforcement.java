package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class WolfReinforcement extends AggressiveReinforcement {
    private static final Int2FloatMap DAMAGE_MULTIPLIER_MAP = EntityUtils.floatChoiceBuilder().range(1).value(1.2f).range(2).value(1.4f).range(3).value(1.6f).build();
    private static final int MAX_INTERVAL = 60;

    public WolfReinforcement() {
        super(EntityType.WOLF);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {
        if (attacker.getLastHurtMob() == target && attacker.tickCount - attacker.getLastHurtMobTimestamp() <= MAX_INTERVAL) {
            event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER_MAP.get(level));
            if (attacker instanceof PlayerEntity) {
                ((PlayerEntity) attacker).magicCrit(target);
            }
        }
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}
}
