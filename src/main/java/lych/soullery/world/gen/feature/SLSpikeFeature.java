package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class SLSpikeFeature extends Feature<SLSpikeConfig> {
    public SLSpikeFeature(Codec<SLSpikeConfig> codec) {
        super(codec);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, SLSpikeConfig config) {
        while (reader.isEmptyBlock(pos) && pos.getY() > 2) {
            pos = pos.below();
        }
        if (config.placeOn.stream().anyMatch(reader.getBlockState(pos)::equals)) {
            pos = pos.above(random.nextInt(4));
            int height = random.nextInt(4) + 7;
            int scale = height / 4 + random.nextInt(2);
            if (scale > 1 && shouldBeHigh(random, config.highProbability)) {
                pos = pos.above(10 + random.nextInt(30));
            }
            for (int y = 0; y < height; ++y) {
                float size = (1 - (float) y / height) * scale;
                int sizeInt = MathHelper.ceil(size);

                for (int x = -sizeInt; x <= sizeInt; ++x) {
                    float xDist = MathHelper.abs(x) - 0.25f;

                    for (int z = -sizeInt; z <= sizeInt; ++z) {
                        float zDist = MathHelper.abs(z) - 0.25f;
                        if ((x == 0 && z == 0 || xDist * xDist + zDist * zDist <= size * size) && (x != -sizeInt && x != sizeInt && z != -sizeInt && z != sizeInt || random.nextFloat() < 0.75f)) {
                            BlockState state = reader.getBlockState(pos.offset(x, y, z));
                            if (state.isAir(reader, pos.offset(x, y, z)) || config.placeOn.contains(state)) {
                                setBlock(reader, pos.offset(x, y, z), config.toPlace);
                            }
                            if (y != 0 && sizeInt > 1) {
                                state = reader.getBlockState(pos.offset(x, -y, z));
                                if (state.isAir(reader, pos.offset(x, -y, z)) || config.placeOn.contains(state)) {
                                    setBlock(reader, pos.offset(x, -y, z), config.toPlace);
                                }
                            }
                        }
                    }
                }
            }

            int size2 = scale - 1;
            if (size2 > 1) {
                size2 = 1;
            }
            for (int x = -size2; x <= size2; ++x) {
                for (int z = -size2; z <= size2; ++z) {
                    BlockPos pos2 = pos.offset(x, -1, z);
                    int count = 50;
                    if (Math.abs(x) == 1 && Math.abs(z) == 1) {
                        count = random.nextInt(5);
                    }
                    while (pos2.getY() > 50) {
                        BlockState state = reader.getBlockState(pos2);
                        if (!state.isAir(reader, pos2) && !config.placeOn.contains(state) && state != config.toPlace) {
                            break;
                        }
                        setBlock(reader, pos2, config.toPlace);
                        pos2 = pos2.below();
                        --count;
                        if (count <= 0) {
                            pos2 = pos2.below(random.nextInt(5) + 1);
                            count = random.nextInt(5);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private static boolean shouldBeHigh(Random random, double highProbability) {
        return random.nextDouble() < highProbability;
    }
}

