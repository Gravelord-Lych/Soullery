package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SoulRabbitReinforcement extends DefensiveReinforcement {
    public SoulRabbitReinforcement() {
        super(ModEntities.SOUL_RABBIT);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event) {}

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return super.isItemPosSuitable(stack) && ((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.FEET;
    }

    @Override
    protected boolean hasEvents() {
        return false;
    }

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
