package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public abstract class ToolReinforcement extends Reinforcement {
    public ToolReinforcement(EntityType<?> type) {
        super(type);
    }

    public ToolReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public ToolReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @SubscribeEvent
    public void onPlayerDig(PlayerEvent.BreakSpeed event) {
        int level = getLevel(event.getPlayer().getMainHandItem());
        if (level > 0) {
            onDig(event.getPlayer().getMainHandItem(), event.getPlayer(), event.getState(), level, event);
        }
    }

    @SubscribeEvent
    public void onPlayerHarvest(PlayerEvent.HarvestCheck event) {
        int level = getLevel(event.getPlayer().getMainHandItem());
        if (level > 0) {
            onHarvest(event.getPlayer().getMainHandItem(), event.getPlayer(), event.getTargetBlock(), level, event);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        int level = getLevel(event.getPlayer().getMainHandItem());
        if (level > 0) {
            onBreak(event.getPlayer().getMainHandItem(), event.getPlayer(), event.getState(), level, event);
        }
    }

    protected abstract void onHarvest(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.HarvestCheck event);

    protected abstract void onDig(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.BreakSpeed event);

    protected abstract void onBreak(ItemStack stack, PlayerEntity player, BlockState state, int level, BlockEvent.BreakEvent event);

    @Override
    protected boolean hasEvents() {
        return true;
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }
}
