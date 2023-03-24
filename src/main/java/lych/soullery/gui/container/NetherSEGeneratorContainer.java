package lych.soullery.gui.container;

import com.google.common.collect.ImmutableList;
import lych.soullery.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherSEGeneratorContainer extends AbstractSEGeneratorContainer {
    public NetherSEGeneratorContainer(int id, BlockPos pos, PlayerInventory inventory, World world, IIntArray seProgress, IWorldPosCallable access) {
        super(ModContainers.NETHER_SEGEN, id, pos, inventory, world, seProgress, access);
    }

    @Override
    protected Iterable<Block> getBlocks() {
        return ImmutableList.of(ModBlocks.NETHER_SEGEN, ModBlocks.NETHER_SEGEN_II);
    }
}
