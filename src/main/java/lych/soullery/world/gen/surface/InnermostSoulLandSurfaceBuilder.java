package lych.soullery.world.gen.surface;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import lych.soullery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.ValleySurfaceBuilder;

public class InnermostSoulLandSurfaceBuilder extends ValleySurfaceBuilder {
    private static final BlockState REFINED_SOUL_SAND = ModBlocks.REFINED_SOUL_SAND.defaultBlockState();
    private static final BlockState REFINED_SOUL_SOIL = ModBlocks.REFINED_SOUL_SOIL.defaultBlockState();
    private static final ImmutableList<BlockState> BLOCKS = ImmutableList.of(REFINED_SOUL_SAND, REFINED_SOUL_SOIL);

    public InnermostSoulLandSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    protected ImmutableList<BlockState> getFloorBlockStates() {
        return BLOCKS;
    }

    @Override
    protected ImmutableList<BlockState> getCeilingBlockStates() {
        return BLOCKS;
    }

    @Override
    protected BlockState getPatchBlockState() {
        return REFINED_SOUL_SAND;
    }
}
