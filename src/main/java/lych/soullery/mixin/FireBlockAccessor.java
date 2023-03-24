package lych.soullery.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {
    @Invoker
    BlockState callGetStateForPlacement(IBlockReader reader, BlockPos pos);
}
