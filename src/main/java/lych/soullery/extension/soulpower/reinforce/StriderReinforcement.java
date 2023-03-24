package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StriderReinforcement extends Reinforcement {
    public static final int FIRE_RESISTANT_LEVEL = 1;
    public static final int ALL_RESISTANT_LEVEL = 2;
    public static final int NO_EXPIRY_LEVEL = 2;
    public static final int KEEP_INVENTORY_LEVEL = 3;

    public StriderReinforcement() {
        super(EntityType.STRIDER);
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        int level = getLevel(event.getEntityItem().getItem());
        if (level >= NO_EXPIRY_LEVEL) {
            event.setCanceled(true);
            event.setExtraLife(6000);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerInventory inventory = ((PlayerEntity) event.getEntityLiving()).inventory;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                int level = getLevel(stack);
                if (level >= KEEP_INVENTORY_LEVEL) {
                    inventory.removeItemNoUpdate(i);
                    ((IPlayerEntityMixin) event.getEntityLiving()).addSavableItem(stack);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            ((IPlayerEntityMixin) event.getPlayer()).restoreSavableItemsFrom(event.getOriginal());
        }
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }
}
