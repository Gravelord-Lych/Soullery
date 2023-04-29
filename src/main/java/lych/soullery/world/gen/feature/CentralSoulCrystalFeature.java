package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import lych.soullery.block.ModBlocks;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.functional.FortifiedSoulCrystalEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Objects;
import java.util.Random;

import static java.lang.Math.abs;

public class CentralSoulCrystalFeature extends Feature<NoFeatureConfig> {
    public static final int DISTANCE_TO_GROUND = 35;
    private static final int RADIUS_TB = 3;
    public static final int RADIUS = 4;
    public static final int HEIGHT = 9;
    private static final int INNER_HEIGHT = 7;
    private static final int INNER_FLOOR_CEILING_HEIGHT = (HEIGHT - INNER_HEIGHT) / 2;
    private static final int INNER_FLOOR_CEILING_RADIUS = 2;
    private static FortifiedSoulCrystalEntity currentCrystal;

    public CentralSoulCrystalFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    public static FortifiedSoulCrystalEntity getCurrentCrystal() {
        Objects.requireNonNull(currentCrystal, "Where's the crystal?");
        FortifiedSoulCrystalEntity temp = currentCrystal;
        currentCrystal = null;
        return temp;
    }

    @SuppressWarnings("NonStrictComparisonCanBeEquality")
    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig config) {
        final int rtb = RADIUS_TB;
        final int r = RADIUS;
        final int ir = INNER_FLOOR_CEILING_RADIUS;
        final int ih = INNER_FLOOR_CEILING_HEIGHT;
        int y = 0;

        for (int x = -rtb; x <= rtb; x++) {
            for (int z = -rtb; z <= rtb; z++) {
                setBlock(reader, pos.offset(x, y, z), ModBlocks.SOUL_OBSIDIAN.defaultBlockState());
            }
        }
        y++;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                setBlock(reader, pos.offset(x, y, z), ModBlocks.SOUL_OBSIDIAN.defaultBlockState());
            }
        }

        for (int iy = 1; iy <= HEIGHT; iy++) {
            y++;
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos offset = pos.offset(x, y, z);
                    if (abs(x) == r || abs(z) == r) {
                        setBlock(reader, offset, ModBlocks.REFINED_SOUL_METAL_BARS.getState(reader, offset));
                    } else if (abs(x) <= ir && abs(z) <= ir) {
                        if (iy <= ih || iy >= HEIGHT + 1 - ih) {
                            setBlock(reader, offset, ModBlocks.SOUL_OBSIDIAN.defaultBlockState());
                        } else if (abs(x) == ir || abs(z) == ir) {
                            setBlock(reader, offset, ModBlocks.REFINED_SOUL_METAL_BARS.getState(reader, offset));
                        } else if (x == 0 && z == 0 && iy == ih + 1) {
                            setBlock(reader, offset, Blocks.BEDROCK.defaultBlockState());
                            FortifiedSoulCrystalEntity crystal = ModEntities.FORTIFIED_SOUL_CRYSTAL.create(reader.getLevel());
                            crystal.setHealable(true);
                            crystal.moveTo(offset.getX() + 0.5, offset.getY() + 1, offset.getZ() + 0.5, random.nextFloat() * 360, 0);
                            reader.addFreshEntity(crystal);
                            currentCrystal = crystal;
                        } else {
                            setBlock(reader, offset, Blocks.AIR.defaultBlockState());
                        }
                    } else {
                        setBlock(reader, offset, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        y++;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                setBlock(reader, pos.offset(x, y, z), ModBlocks.SOUL_OBSIDIAN.defaultBlockState());
            }
        }
        y++;
        for (int x = -rtb - 1; x <= rtb + 1; x++) {
            for (int z = -rtb - 1; z <= rtb + 1; z++) {
                if (abs(x) == rtb + 1 && abs(z) == rtb + 1) {
                    setBlock(reader, pos.offset(x, y, z), Blocks.SOUL_LANTERN.defaultBlockState());
                } else {
                    setBlock(reader, pos.offset(x, y, z), ModBlocks.SOUL_OBSIDIAN.defaultBlockState());
                }
            }
        }

        return true;
    }
}
