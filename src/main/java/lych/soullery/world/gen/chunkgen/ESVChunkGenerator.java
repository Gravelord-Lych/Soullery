package lych.soullery.world.gen.chunkgen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ESVChunkGenerator extends ChunkGenerator {
    private static final int SPACING = 80;
    private static final int SPACING_X = 69;
    private static final int SPACING_Z = 40;
    static final BlockPos ISLAND_1 = new BlockPos(0, 0, SPACING);
    static final BlockPos ISLAND_2 = new BlockPos(SPACING_X, 0, SPACING_Z);
    static final BlockPos ISLAND_3 = new BlockPos(SPACING_X, 0, -SPACING_Z);
    static final BlockPos ISLAND_4 = new BlockPos(0, 0, -SPACING);
    static final BlockPos ISLAND_5 = new BlockPos(-SPACING_X, 0, -SPACING_Z);
    static final BlockPos ISLAND_6 = new BlockPos(-SPACING_X, 0, SPACING_Z);
    static final int MAIN_RADIUS = 40;
    static final int ISLAND_RADIUS = 30;
    static final int MAIN_HEIGHT = 60;
    static final int ISLAND_HEIGHT = 65;

    private static final BlockState STATE = ModBlocks.DECAYED_STONE.defaultBlockState();
    public static final Codec<ESVChunkGenerator> CODEC = RecordCodecBuilder.create(ESVChunkGenerator::makeCodec);

    public ESVChunkGenerator(BiomeProvider biomeSource) {
        super(biomeSource, new DimensionStructuresSettings(false));
    }

    public static Island getMainIsland() {
        return new Island(0, 0, MAIN_RADIUS, MAIN_HEIGHT);
    }

    public static List<Island> getIslands() {
        return ImmutableList.of(new Island(ISLAND_1.getX(), ISLAND_1.getZ(), ISLAND_RADIUS, ISLAND_HEIGHT),
                new Island(ISLAND_2.getX(), ISLAND_2.getZ(), ISLAND_RADIUS, ISLAND_HEIGHT),
                new Island(ISLAND_3.getX(), ISLAND_3.getZ(), ISLAND_RADIUS, ISLAND_HEIGHT),
                new Island(ISLAND_4.getX(), ISLAND_4.getZ(), ISLAND_RADIUS, ISLAND_HEIGHT),
                new Island(ISLAND_5.getX(), ISLAND_5.getZ(), ISLAND_RADIUS, ISLAND_HEIGHT),
                new Island(ISLAND_6.getX(), ISLAND_6.getZ(), ISLAND_RADIUS, ISLAND_HEIGHT));
    }

    public static List<Island> getAllIslands() {
        return ImmutableList.<Island>builder().add(getMainIsland()).addAll(getIslands()).build();
    }

    @Override
    public void applyBiomeDecoration(WorldGenRegion region, StructureManager manager) {}

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion region, IChunk chunk) {}

    @Override
    public void fillFromNoise(IWorld world, StructureManager manager, IChunk chunk) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        Heightmap oceanMap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap surfaceMap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

        int minBlockX = chunk.getPos().x << 4;
        int minBlockZ = chunk.getPos().z << 4;

        for (Island island : getAllIslands()) {
            placeIsland(minBlockX, minBlockZ, mutablePos, island, chunk, oceanMap, surfaceMap);
        }
    }

    private void placeIsland(int minBlockX, int minBlockZ, BlockPos.Mutable mutablePos, Island island, IChunk chunk, Heightmap oceanMap, Heightmap surfaceMap) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = minBlockX + x;
                int realZ = minBlockZ + z;
                int radius = island.getRadius();
                BlockPos pos = new BlockPos(realX, 0, realZ);
                if (pos.distSqr(island.getCenterX(), 0, island.getCenterZ(), false) <= (radius * radius + 1)) {
                    Pair<Integer, Integer> heights = getHeight(pos.getX() - island.getCenterX(), pos.getZ() - island.getCenterZ(), island);
                    for (int y = heights.getFirst(); y < heights.getSecond(); y++) {
                        mutablePos.set(pos.getX(), y, pos.getZ());
                        chunk.setBlockState(mutablePos, STATE, false);
                        oceanMap.update(x, y, z, STATE);
                        surfaceMap.update(x, y, z, STATE);
                    }
                }
            }
        }
    }

    private static Pair<Integer, Integer> getHeight(int rx, int rz, Island island) {
        float distance = MathHelper.sqrt(rx * rx + rz * rz);
        if (distance > island.getRadius()) {
            return Pair.of(-1, -1);
        }
        float minHeight = minHeightFunc(island.getRadius(), distance);
        float maxHeight = maxHeightFunc(island.getRadius(), distance);
        if (maxHeight < 0) {
            maxHeight = 0;
        }
        maxHeight = (float) Math.log(maxHeight + 0.1f) + 3;
        return Pair.of((int) minHeight + island.getHeight(), (int) maxHeight + island.getHeight());
    }

    private static float maxHeightFunc(int radius, float distance) {
        return -0.01f * (distance - radius) * (distance + radius);
    }

    private static float minHeightFunc(int radius, float distance) {
        return -MathHelper.sqrt(radius * radius - distance * distance) / 1.3f;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Type type) {
// TODO - check
        for (Island island : getAllIslands()) {
            Pair<Integer, Integer> heights = getHeight(x - island.getCenterX(), z - island.getCenterZ(), island);
            if (heights.getFirst() > 0) {
                return heights.getSecond() - 1;
            }
        }
        return 0;
    }

    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        for (Island island : getAllIslands()) {
            Pair<Integer, Integer> heights = getHeight(x - island.getCenterX(), z - island.getCenterZ(), island);
            if (heights.getFirst() > 0) {
                BlockState[] array = new BlockState[heights.getSecond()];
                for (int i = heights.getFirst(); i < heights.getSecond(); i++) {
                    array[i] = STATE;
                }
                return new Blockreader(array);
            }
        }
        return new Blockreader(new BlockState[0]);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new ESVChunkGenerator(biomeSource.withSeed(seed));
    }

    private static App<RecordCodecBuilder.Mu<ESVChunkGenerator>, ESVChunkGenerator> makeCodec(RecordCodecBuilder.Instance<ESVChunkGenerator> instance) {
        return instance
                .group(BiomeProvider.CODEC.fieldOf("biome_source").forGetter(ESVChunkGenerator::getBiomeSource))
                .apply(instance, instance.stable(ESVChunkGenerator::new));
    }

    public static class Island {
        public static final Codec<Island> CODEC = RecordCodecBuilder.create(Island::makeCodec);
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final AxisAlignedBB topBoundingBox;

        public Island(int centerX, int centerZ, int radius, int height) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.radius = radius;
            this.height = height;
            topBoundingBox = new AxisAlignedBB(centerX - radius, 0, centerZ - radius, centerX + radius, 255, centerZ + radius);
        }

        public boolean isCenterWithinChunk(BlockPos pos) {
            return pos.getX() >> 4 == centerX >> 4 && pos.getZ() >> 4 == centerZ >> 4;
        }

        public <T extends PlayerEntity> List<T> getPlayersInside(List<T> players) {
            return players.stream().filter(player -> isPosInside(player.blockPosition())).collect(Collectors.toList());
        }

        public long getPlayerCountInside(List<PlayerEntity> players) {
            return players.stream().map(Entity::blockPosition).filter(this::isPosInside).count();
        }

        public boolean isPosInside(BlockPos pos) {
            return (pos.getX() - centerX) * (pos.getX() - centerX) + (pos.getZ() - centerZ) * (pos.getZ() - centerZ) <= radius * radius;
        }

        public BlockPos getCenter() {
            return new BlockPos(getCenterX(), getHeight(), getCenterZ());
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Island)) return false;
            Island island = (Island) o;
            return getCenterX() == island.getCenterX() && getCenterZ() == island.getCenterZ() && getRadius() == island.getRadius() && getHeight() == island.getHeight();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getCenterX(), getCenterZ(), getRadius(), getHeight());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("centerX", centerX)
                    .add("centerZ", centerZ)
                    .add("radius", radius)
                    .add("height", height)
                    .toString();
        }

        private static App<RecordCodecBuilder.Mu<Island>, Island> makeCodec(RecordCodecBuilder.Instance<Island> instance) {
            return instance.group(Codec.INT.fieldOf("centerX").orElse(0).forGetter(Island::getCenterX),
                    Codec.INT.fieldOf("centerZ").orElse(0).forGetter(Island::getCenterZ),
                    Codec.INT.fieldOf("radius").orElse(0).forGetter(Island::getRadius),
                    Codec.INT.fieldOf("height").orElse(0).forGetter(Island::getHeight)).apply(instance, Island::new);
        }

        public AxisAlignedBB getTopBoundingBox() {
            return topBoundingBox;
        }
    }
}
