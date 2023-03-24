package lych.soullery.world.gen.surface;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import java.util.Random;

public class NoFluidSurfaceBuilder<T extends ISurfaceBuilderConfig> extends SurfaceBuilder<T> {
    private final SurfaceBuilder<T> delegate;

    public NoFluidSurfaceBuilder(Codec<T> codec, SurfaceBuilder<T> delegate) {
        super(codec);
        this.delegate = delegate;
    }

    @Override
    public void apply(Random random, IChunk chunk, Biome biome, int x, int z, int height, double surfaceNoiseValue, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, T config) {
        BlockState newFluid = redirectFluid(random, chunk, biome, x, z, height, surfaceNoiseValue, defaultBlock, defaultFluid, seaLevel, seed, config);
        delegate.apply(random, chunk, biome, x, z, height, surfaceNoiseValue, defaultBlock, newFluid, seaLevel, seed, config);
    }

    protected BlockState redirectFluid(Random random, IChunk chunk, Biome biome, int x, int z, int height, double surfaceNoiseValue, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, T config) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void initNoise(long seed) {
        delegate.initNoise(seed);
    }
}
