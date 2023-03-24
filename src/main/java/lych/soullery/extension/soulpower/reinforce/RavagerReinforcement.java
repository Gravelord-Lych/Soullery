package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

public class RavagerReinforcement extends ToolReinforcement {
    private static final int ALL_HARVEST_LEVEL = 1;
    private static final int INSTABREAK_DIRT_LEVEL = 1;
    private static final int NO_DIG_SLOWDOWN_LEVEL = 2;

    public RavagerReinforcement() {
        super(EntityType.RAVAGER);
    }

    @Override
    protected void onHarvest(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.HarvestCheck event) {
        if (level >= ALL_HARVEST_LEVEL) {
            event.setCanHarvest(true);
        }
    }

    @Override
    protected void onDig(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.BreakSpeed event) {
        if (level >= NO_DIG_SLOWDOWN_LEVEL) {
            event.setNewSpeed(Math.max(event.getNewSpeed(), player.inventory.getDestroySpeed(state)));
        }
        if (state.is(BlockTags.LEAVES) || state.getHarvestTool() == ToolType.SHOVEL && level >= INSTABREAK_DIRT_LEVEL) {
            event.setNewSpeed(1919810);
        }
    }

    @Override
    protected void onBreak(ItemStack stack, PlayerEntity player, BlockState state, int level, BlockEvent.BreakEvent event) {}

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
