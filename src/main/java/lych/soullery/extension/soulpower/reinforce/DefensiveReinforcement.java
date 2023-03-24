package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public abstract class DefensiveReinforcement extends Reinforcement {
    public DefensiveReinforcement(EntityType<?> type) {
        super(type);
    }

    public DefensiveReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public DefensiveReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (onlyCalculateOnce()) {
            int level = getTotalLevel(entity.getArmorSlots());
            if (level > 0) {
                onAttack(ItemStack.EMPTY, entity, event.getSource(), event.getAmount(), level, event);
            }
            return;
        }
        for (ItemStack stack : entity.getArmorSlots()) {
            int level = getLevel(stack);
            if (level > 0) {
                onAttack(stack, entity, event.getSource(), event.getAmount(), level, event);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (onlyCalculateOnce()) {
            int level = getTotalLevel(entity.getArmorSlots());
            if (level > 0) {
                onHurt(ItemStack.EMPTY, entity, event.getSource(), event.getAmount(), level, event);
            }
            return;
        }
        for (ItemStack stack : entity.getArmorSlots()) {
            int level = getLevel(stack);
            if (level > 0) {
                onHurt(stack, entity, event.getSource(), event.getAmount(), level, event);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (onlyCalculateOnce()) {
            int level = getTotalLevel(entity.getArmorSlots());
            if (level > 0) {
                onDamage(ItemStack.EMPTY, entity, event.getSource(), event.getAmount(), level, event);
            }
            return;
        }
        for (ItemStack stack : entity.getArmorSlots()) {
            int level = getLevel(stack);
            if (level > 0) {
                onDamage(stack, entity, event.getSource(), event.getAmount(), level, event);
            }
        }
    }

    protected abstract void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event);

    protected abstract void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event);

    protected abstract void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event);

    protected boolean onlyCalculateOnce() {
        return false;
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }
}
