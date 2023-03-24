package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CaveSpiderReinforcement extends AggressiveReinforcement {
    private static final Int2IntMap POISON_DURATION_MAP = EntityUtils.intChoiceBuilder().range(1).value(40).range(2).value(70).range(3).value(100).build();
    private static final int POISON_AMPLIFIER = 1;

    public CaveSpiderReinforcement() {
        super(EntityType.CAVE_SPIDER);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        target.addEffect(new EffectInstance(Effects.POISON, POISON_DURATION_MAP.get(level), POISON_AMPLIFIER));
    }
}
