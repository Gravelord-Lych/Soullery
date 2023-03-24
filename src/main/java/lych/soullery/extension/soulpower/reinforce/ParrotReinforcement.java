package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ParrotReinforcement extends ToolReinforcement {
    private static final float SPEED_COEFFICIENT = 0.04f;

    public ParrotReinforcement() {
        super(EntityType.PARROT);
    }

    @Override
    protected void onHarvest(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.HarvestCheck event) {}

    @Override
    protected void onDig(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.BreakSpeed event) {
        float speed = state.getDestroySpeed(player.level, event.getPos());
        if (event.getPos().getY() != -1 && speed != -1) {
            event.setNewSpeed(event.getNewSpeed() * (1 + speed * SPEED_COEFFICIENT * level));
        }
    }

    @Override
    protected void onBreak(ItemStack stack, PlayerEntity player, BlockState state, int level, BlockEvent.BreakEvent event) {}
}
