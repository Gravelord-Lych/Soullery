package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

public class WanderingTraderReinforcement extends ToolReinforcement {
    private static final double BASE_DROP_PROBABILITY = 0.01;

    public WanderingTraderReinforcement() {
        super(EntityType.WANDERING_TRADER);
    }

    @Override
    protected void onHarvest(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.HarvestCheck event) {}

    @Override
    protected void onDig(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.BreakSpeed event) {}

    @Override
    protected void onBreak(ItemStack stack, PlayerEntity player, BlockState state, int level, BlockEvent.BreakEvent event) {
        if (player.getRandom().nextDouble() < BASE_DROP_PROBABILITY * level) {
            Vector3d pos = Vector3d.atCenterOf(event.getPos());
            ItemEntity emerald = new ItemEntity(player.level, pos.x, pos.y, pos.z);
            emerald.spawnAtLocation(new ItemStack(Items.EMERALD));
        }
    }
}
