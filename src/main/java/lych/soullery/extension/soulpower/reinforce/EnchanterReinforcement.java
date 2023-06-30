package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EnchanterReinforcement extends ToolReinforcement {
    public EnchanterReinforcement() {
        super(ModEntities.ENCHANTER);
    }

    @Override
    protected void onHarvest(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.HarvestCheck event) {}

    @Override
    protected void onDig(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.BreakSpeed event) {}

    @Override
    protected void onBreak(ItemStack stack, PlayerEntity player, BlockState state, int level, BlockEvent.BreakEvent event) {
        event.setExpToDrop(Math.max(player.getRandom().nextInt(4 - level) == 0 ? 2 : 0, event.getExpToDrop() * (1 + level)));
    }

    @SubscribeEvent
    public void onLivingDropExperience(LivingExperienceDropEvent event) {
        int level = getLevel(event.getAttackingPlayer().getMainHandItem());
        if (level > 0) {
            event.setDroppedExperience(event.getDroppedExperience() * (1 + level));
        }
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return super.isItemPosSuitable(stack) || stack.getItem() instanceof SwordItem || stack.getItem() instanceof ShootableItem;
    }
}
