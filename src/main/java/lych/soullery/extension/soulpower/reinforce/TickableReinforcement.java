package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class TickableReinforcement extends Reinforcement {
    public TickableReinforcement(EntityType<?> type) {
        super(type);
    }

    public TickableReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public TickableReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (onlyCalculateOnce()) {
            int level = getTotalLevel(getItemStacks(event.getEntityLiving()).values());
            if (level > 0) {
                onLivingTick(ItemStack.EMPTY, event.getEntityLiving(), level);
            }
            return;
        }
        for (ItemStack stack : getItemStacks(event.getEntityLiving()).values()) {
            int level = getLevel(stack);
            if (level > 0) {
                onLivingTick(stack, event.getEntityLiving(), level);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == getTickPhase()) {
            if (onlyCalculateOnce()) {
                int level = getTotalLevel(getItemStacks(event.player).values());
                if (level > 0) {
                    onPlayerTick(ItemStack.EMPTY, event.player, event.side, level);
                }
                return;
            }
            for (ItemStack stack : getItemStacks(event.player).values()) {
                int level = getLevel(stack);
                if (level > 0) {
                    onPlayerTick(stack, event.player, event.side, level);
                }
            }
        }
    }

    public final Map<EquipmentSlotType, ItemStack> getItemStacks(LivingEntity entity) {
        return getAvailableSlots().stream().collect(Collectors.toMap(Function.identity(), entity::getItemBySlot));
    }

    protected TickEvent.Phase getTickPhase() {
        return TickEvent.Phase.END;
    }

    protected abstract void onLivingTick(ItemStack stack, LivingEntity entity, int level);

    protected abstract void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level);

    protected abstract Set<EquipmentSlotType> getAvailableSlots();

    protected boolean onlyCalculateOnce() {
        return false;
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }
}
