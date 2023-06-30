package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.mixin.IAbstractArrowEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public abstract class AggressiveReinforcement extends Reinforcement {
    public AggressiveReinforcement(EntityType<?> type) {
        super(type);
    }

    public AggressiveReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public AggressiveReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    public boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (allowsDamageSource(event.getSource()) && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            ItemStack stack = attacker.getItemInHand(Hand.MAIN_HAND);
            int level = getLevel(stack);
            boolean canApply = true;
            if (event.getSource().getDirectEntity() instanceof AbstractArrowEntity && stack.getItem() instanceof ShootableItem) {
                canApply = isSameBow(stack, (AbstractArrowEntity) event.getSource().getDirectEntity());
            }
            if (level > 0 && canApply) {
                onAttack(stack, attacker, target, level, event);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (allowsDamageSource(event.getSource()) && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            ItemStack stack = attacker.getItemInHand(Hand.MAIN_HAND);
            int level = getLevel(stack);
            boolean canApply = true;
            if (event.getSource().getDirectEntity() instanceof AbstractArrowEntity && stack.getItem() instanceof ShootableItem) {
                canApply = isSameBow(stack, (AbstractArrowEntity) event.getSource().getDirectEntity());
            }
            if (level > 0 && canApply) {
                onHurt(stack, attacker, target, level, event);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (allowsDamageSource(event.getSource()) && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            ItemStack stack = attacker.getItemInHand(Hand.MAIN_HAND);
            int level = getLevel(stack);
            boolean canApply = true;
            if (event.getSource().getDirectEntity() instanceof AbstractArrowEntity && stack.getItem() instanceof ShootableItem) {
                canApply = isSameBow(stack, (AbstractArrowEntity) event.getSource().getDirectEntity());
            }
            if (level > 0 && canApply) {
                onDamage(stack, attacker, target, level, event);
            }
        }
    }

    @SubscribeEvent
    public void postLivingHurt(PostLivingHurtEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (allowsDamageSource(event.getSource()) && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            ItemStack stack = attacker.getItemInHand(Hand.MAIN_HAND);
            int level = getLevel(stack);
            boolean canApply = true;
            if (event.getSource().getDirectEntity() instanceof AbstractArrowEntity && stack.getItem() instanceof ShootableItem) {
                canApply = isSameBow(stack, (AbstractArrowEntity) event.getSource().getDirectEntity());
            }
            if (level > 0 && canApply) {
                postHurt(stack, attacker, target, level, event);
            }
        }
    }

    protected final boolean isSameBow(ItemStack stack, AbstractArrowEntity arrow) {
        ItemStack bow = ((IAbstractArrowEntityMixin) arrow).getRecordedBow();
        if (bow == null) {
            return false;
        }
        return ItemStack.matches(bow, stack);
    }

    protected boolean allowsDamageSource(DamageSource source) {
        return EntityUtils.isMelee(source);
    }

    protected abstract void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event);

    protected abstract void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event);

    protected abstract void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event);

    protected void postHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, PostLivingHurtEvent event) {}

    @Override
    protected boolean hasEvents() {
        return true;
    }
}
