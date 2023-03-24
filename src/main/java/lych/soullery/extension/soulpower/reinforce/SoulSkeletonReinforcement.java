package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.api.event.ArrowSpawnEvent;
import lych.soullery.entity.ModEntities;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SoulSkeletonReinforcement extends AggressiveReinforcement {
    public SoulSkeletonReinforcement() {
        super(ModEntities.SOUL_SKELETON);
    }

    @Override
    public boolean isItemPosSuitable(ItemStack stack) {
        return super.isItemPosSuitable(stack) || stack.getItem() instanceof BowItem;
    }

    @Override
    protected boolean allowsDamageSource(DamageSource source) {
        return super.allowsDamageSource(source) || source.isProjectile();
    }

    @SubscribeEvent
    public void onArrowSpawn(ArrowSpawnEvent event) {
        ItemStack bow = event.getBow();
        int level = getLevel(bow);
        if (level > 0) {
            ((IEntityMixin) event.getArrow()).setOnSoulFire(true);
        }
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {
        if (!target.fireImmune()) {
            ((IEntityMixin) target).setOnSoulFire(true);
        }
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
