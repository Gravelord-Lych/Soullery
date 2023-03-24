package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.api.event.ArrowSpawnEvent;
import lych.soullery.util.mixin.IAbstractArrowEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public abstract class BowAttackReinforcement extends Reinforcement {
    public BowAttackReinforcement(EntityType<?> type) {
        super(type);
    }

    public BowAttackReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public BowAttackReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof BowItem;
    }

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        ItemStack stack = event.getBow();
        int level = getLevel(stack);
        if (level > 0) {
            onNock(event, level);
        }
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        ItemStack stack = event.getBow();
        int level = getLevel(stack);
        if (level > 0) {
            onLoose(event, level);
        }
    }

    @SubscribeEvent
    public void onArrowSpawn(ArrowSpawnEvent event) {
        ItemStack bow = event.getBow();
        int level = getLevel(bow);
        if (level > 0) {
            onSpawn(event.getArrow(), event.getPlayer(), level, event);
        }
    }

    @SubscribeEvent
    public void onArrowImpact(ProjectileImpactEvent.Arrow event) {
        Entity owner = event.getArrow().getOwner();
        if (owner instanceof LivingEntity) {
            int level = getLevel(((LivingEntity) owner).getMainHandItem());
            if (level > 0 && ((IAbstractArrowEntityMixin) event.getArrow()).getRecordedBow() == ((LivingEntity) owner).getMainHandItem()) {
                onImpact(event.getArrow(), (LivingEntity) owner, level, event);
            }
        }
    }

    protected void onNock(ArrowNockEvent event, int level) {}

    protected void onLoose(ArrowLooseEvent event, int level) {}

    protected abstract void onSpawn(AbstractArrowEntity arrow, PlayerEntity player, int level, ArrowSpawnEvent event);

    protected abstract void onImpact(AbstractArrowEntity arrow, LivingEntity owner, int level, ProjectileImpactEvent.Arrow event);

    @Override
    protected boolean hasEvents() {
        return true;
    }
}
