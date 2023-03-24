package lych.soullery.world.gen.surface;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.SoulSandValleySurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class SoulLandSurfaceBuilder extends SoulSandValleySurfaceBuilder {
    public SoulLandSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    protected BlockState getPatchBlockState() {
        return Blocks.SOUL_SOIL.defaultBlockState();
    }
}
