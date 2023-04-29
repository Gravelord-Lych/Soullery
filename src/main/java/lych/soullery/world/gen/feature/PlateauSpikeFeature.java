package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.block.ModBlocks;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.functional.SoulCrystalEntity;
import lych.soullery.tag.ModBlockTags;
import lych.soullery.util.PositionCalculators;
import lych.soullery.world.gen.config.PlateauSpikeConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlateauSpikeFeature extends Feature<PlateauSpikeConfig> {
    public static final int Y_OFFSET = -3;
    public static final int COUNT = 12;
    public static final int RADIUS = 48;
    private static final float BAR_RADIUS_MULTIPLIER = 0.75f;
    private static final int BAR_HEIGHT = 4;
    private static final int ADDITIONAL_HEIGHT = 3;

    public PlateauSpikeFeature(Codec<PlateauSpikeConfig> codec) {
        super(codec);
    }

    public static List<BlockPos> findSpikeLocations(ISeedReader level, BlockPos center, Random random) {
        double rad;
        List<BlockPos> spikePosList = new ArrayList<>();
        for (int i = 0; i < 360; i += 360 / COUNT) {
            rad = Math.toRadians(i);
            int x = (int) (MathHelper.cos((float) rad) * RADIUS) + center.getX();
            int z = (int) (MathHelper.sin((float) rad) * RADIUS) + center.getZ();
            spikePosList.add(new BlockPos(x, 0, z));
        }
        return spikePosList;
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, PlateauSpikeConfig config) {
        for (Spike spike : config.getSpikes()) {
            placeSpike(reader, spike, random);
        }
        return true;
    }

    private void placeSpike(ISeedReader reader, Spike spike, Random random) {
        int radius = spike.getRadius();
        int startY = PositionCalculators.heightmap(spike.getCenterX(), spike.getCenterZ(), reader.getLevel()) + Y_OFFSET;

        for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(spike.getCenterX() - radius, startY, spike.getCenterZ() - radius), new BlockPos(spike.getCenterX() + radius, startY + spike.getHeight() + BAR_HEIGHT + ADDITIONAL_HEIGHT + 5, spike.getCenterZ() + radius))) {
            boolean yEdge = pos.getY() == spike.getHeight() + BAR_HEIGHT + ADDITIONAL_HEIGHT;
            double r = radius;
            if (yEdge) {
                r -= 2;
            }
            if (pos.distSqr(spike.getCenterX(), pos.getY(), spike.getCenterZ(), false) <= (r * r + 1) && (pos.getY() < startY + spike.getHeight() || pos.getY() > startY + spike.getHeight() + BAR_HEIGHT && pos.getY() <= startY + spike.getHeight() + BAR_HEIGHT + ADDITIONAL_HEIGHT)) {
                setBlock(reader, pos, ModBlocks.SOUL_OBSIDIAN.defaultBlockState());
            } else if (pos.getY() > 65 && !reader.getBlockState(pos).is(ModBlockTags.SOUL_DRAGON_IMMUNE)) {
                setBlock(reader, pos, Blocks.AIR.defaultBlockState());
            }
        }

        int min = (int) (-radius * BAR_RADIUS_MULTIPLIER);
        int max = (int) (radius * BAR_RADIUS_MULTIPLIER);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = min; x <= max; ++x) {
            for (int z = min; z <= max; ++z) {
                float barRadius = radius * BAR_RADIUS_MULTIPLIER;
                if (x * x + z * z > barRadius * barRadius || x * x + z * z < (barRadius - 1.2f) * (barRadius - 1.2f)) {
                    continue;
                }
                for (int y = 0; y <= BAR_HEIGHT; ++y) {
                    BlockPos pos = mutable.set(spike.getCenterX() + x, startY + spike.getHeight() + y, spike.getCenterZ() + z).immutable();
                    BlockState bar = ModBlocks.SOUL_METAL_BARS.getState(reader.getLevel(), pos);
                    setBlock(reader, pos, bar);
                }
            }
        }

        SoulCrystalEntity crystal = ModEntities.SOUL_CRYSTAL.create(reader.getLevel());
        crystal.setHealable(true);
        crystal.moveTo(spike.getCenterX() + 0.5, startY + spike.getHeight() + 1, spike.getCenterZ() + 0.5, random.nextFloat() * 360, 0);
        reader.addFreshEntity(crystal);
        setBlock(reader, new BlockPos(spike.getCenterX(), startY + spike.getHeight(), spike.getCenterZ()), Blocks.BEDROCK.defaultBlockState());
    }

    public static class Spike {
        public static final Codec<Spike> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("centerX").orElse(0).forGetter(Spike::getCenterX),
                Codec.INT.fieldOf("centerZ").orElse(0).forGetter(Spike::getCenterZ),
                Codec.INT.fieldOf("radius").orElse(0).forGetter(Spike::getRadius),
                Codec.INT.fieldOf("height").orElse(0).forGetter(Spike::getHeight)).apply(instance, Spike::new)
        );
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final AxisAlignedBB topBoundingBox;
        private final int baseHeight;
        private final boolean hasCustomBB;

        public Spike(int centerX, int centerZ, int radius, int height) {
            this(centerX, centerZ, radius, height, new AxisAlignedBB(centerX - radius, 0, centerZ - radius, centerX + radius, 256, centerZ + radius), 0, false);
        }

        private Spike(int centerX, int centerZ, int radius, int height, AxisAlignedBB topBoundingBox, int baseHeight) {
            this(centerX, centerZ, radius, height, topBoundingBox, baseHeight, true);
        }

        private Spike(int centerX, int centerZ, int radius, int height, AxisAlignedBB topBoundingBox, int baseHeight, boolean hasCustomBB) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.radius = radius;
            this.height = height;
            this.topBoundingBox = topBoundingBox;
            this.baseHeight = baseHeight;
            this.hasCustomBB = hasCustomBB;
        }

        public boolean isCenterWithinChunk(BlockPos center) {
            return center.getX() >> 4 == centerX >> 4 && center.getZ() >> 4 == centerZ >> 4;
        }

        public int getCenterX() {
            return centerX;
        }

        public int getCenterZ() {
            return centerZ;
        }

        public int getRadius() {
            return radius;
        }

        public int getHeight() {
            return height;
        }

        public int getBaseHeight() {
            return baseHeight;
        }

        public AxisAlignedBB getTopBoundingBox() {
            return topBoundingBox;
        }

        public boolean isGenerated() {
            return hasCustomBB;
        }

        public Spike generate(int baseHeight, double bbMinY) {
            AxisAlignedBB tb = topBoundingBox;
            double maxY = bbMinY + BAR_HEIGHT;
            return new Spike(centerX, centerZ, radius, height, new AxisAlignedBB(tb.minX, bbMinY, tb.minZ, tb.maxX, maxY, tb.maxZ), baseHeight);
        }
    }
}
