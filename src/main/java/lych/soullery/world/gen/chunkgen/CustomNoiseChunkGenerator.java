package lych.soullery.world.gen.chunkgen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.common.world.StructureSpawnManager;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

// TODO - some names of local variables and method parameters are not accurate

/**
 * More customizable version
 * @see NoiseChunkGenerator
 */
@SuppressWarnings({"deprecation", "unused"})
public abstract class CustomNoiseChunkGenerator extends ChunkGenerator {
    protected static final float[] BEARD_KERNEL = Util.make(new float[13824], array -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    array[i * 24 * 24 + j * 24 + k] = (float) computeContribution(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    protected static final float[] BIOME_WEIGHTS = Util.make(new float[25], array -> {
        for (int chunkXOffset = -2; chunkXOffset <= 2; ++chunkXOffset) {
            for (int chunkZOffset = -2; chunkZOffset <= 2; ++chunkZOffset) {
                float f = 10 / MathHelper.sqrt((chunkXOffset * chunkXOffset + chunkZOffset * chunkZOffset) + 0.2f);
                array[chunkXOffset + 2 + (chunkZOffset + 2) * 5] = f;
            }
        }
    });
    protected final int chunkHeight;
    protected final int chunkWidth;
    protected final int chunkCountX;
    protected final int chunkCountY;
    protected final int chunkCountZ;
    protected final SharedSeedRandom random;
    protected final OctavesNoiseGenerator minLimitPerlinNoise;
    protected final OctavesNoiseGenerator maxLimitPerlinNoise;
    protected final OctavesNoiseGenerator mainPerlinNoise;
    protected final INoiseGenerator surfaceNoise;
    protected final OctavesNoiseGenerator depthNoise;
    @Nullable
    protected final SimplexNoiseGenerator islandNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    protected final long seed;
    protected final Supplier<DimensionSettings> settings;
    protected final int height;

    @Nullable
    private SimplexNoiseGenerator customNoiseGenerator;

    protected CustomNoiseChunkGenerator(BiomeProvider biomeSource, long seed, Supplier<DimensionSettings> settingsSupplier) {
        this(biomeSource, biomeSource, seed, settingsSupplier);
    }

    protected CustomNoiseChunkGenerator(BiomeProvider biomeSource, BiomeProvider runtimeBiomeSource, long seed, Supplier<DimensionSettings> settingsSupplier) {
        super(biomeSource, runtimeBiomeSource, settingsSupplier.get().structureSettings(), seed);
        this.seed = seed;
        DimensionSettings settings = settingsSupplier.get();
        this.settings = settingsSupplier;
        NoiseSettings noiseSettings = settings.noiseSettings();
        this.height = noiseSettings.height();
        this.chunkHeight = noiseSettings.noiseSizeVertical() * 4;
        this.chunkWidth = noiseSettings.noiseSizeHorizontal() * 4;
        this.defaultBlock = settings.getDefaultBlock();
        this.defaultFluid = settings.getDefaultFluid();
        this.chunkCountX = 16 / chunkWidth;
        this.chunkCountY = noiseSettings.height() / chunkHeight;
        this.chunkCountZ = 16 / chunkWidth;
        this.random = new SharedSeedRandom(seed);
        this.minLimitPerlinNoise = new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-15, 0));
        this.maxLimitPerlinNoise = new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-15, 0));
        this.mainPerlinNoise = new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceNoise = noiseSettings.useSimplexSurfaceNoise() ? new PerlinNoiseGenerator(random, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(random, IntStream.rangeClosed(-3, 0));
        this.random.consumeCount(2620);
        this.depthNoise = new OctavesNoiseGenerator(random, IntStream.rangeClosed(-15, 0));
        if (noiseSettings.islandNoiseOverride()) {
            SharedSeedRandom randomIn = new SharedSeedRandom(seed);
            randomIn.consumeCount(17292);
            islandNoise = new SimplexNoiseGenerator(randomIn);
        } else {
            islandNoise = null;
        }
    }

    public boolean stable(long seed, RegistryKey<DimensionSettings> registryKey) {
        return this.seed == seed && settings.get().stable(registryKey);
    }

    protected double sampleAndClampNoise(int chunkX, int chunkY, int chunkZ, double xzScale, double yScale, double xzFactor, double yFactor) {
        double minNoiseValue = 0.0D;
        double maxNoiseValue = 0.0D;
        double mainNoiseValue = 0.0D;
        double scale = 1;

        for (int i = 0; i < getOctaveCount(); i++) {
            double wrappedX = OctavesNoiseGenerator.wrap((double) chunkX * xzScale * scale);
            double wrappedY = OctavesNoiseGenerator.wrap((double) chunkY * yScale * scale);
            double wrappedZ = OctavesNoiseGenerator.wrap((double) chunkZ * xzScale * scale);
            double realYScale = yScale * scale;
            ImprovedNoiseGenerator minNoise = minLimitPerlinNoise.getOctaveNoise(i);
            if (minNoise != null) {
                minNoiseValue += minNoise.noise(wrappedX, wrappedY, wrappedZ, realYScale, (double) chunkY * realYScale) / scale;
            }
            ImprovedNoiseGenerator maxNoise = maxLimitPerlinNoise.getOctaveNoise(i);
            if (maxNoise != null) {
                maxNoiseValue += maxNoise.noise(wrappedX, wrappedY, wrappedZ, realYScale, (double) chunkY * realYScale) / scale;
            }
            if (i < 8) {
                ImprovedNoiseGenerator mainNoise = mainPerlinNoise.getOctaveNoise(i);
                if (mainNoise != null) {
                    mainNoiseValue += mainNoise.noise(OctavesNoiseGenerator.wrap((double) chunkX * xzFactor * scale), OctavesNoiseGenerator.wrap((double) chunkY * yFactor * scale), OctavesNoiseGenerator.wrap((double) chunkZ * xzFactor * scale), yFactor * scale, (double) chunkY * yFactor * scale) / scale;
                }
            }
            scale /= 2;
        }

        return MathHelper.clampedLerp(minNoiseValue / 512, maxNoiseValue / 512, (mainNoiseValue / 10 + 1) / 2);
    }

    protected double[] makeAndFillNoiseColumn(int coveredChunkX, int coveredChunkZ) {
        double[] array = new double[chunkCountY + 1];
        fillNoiseColumn(array, coveredChunkX, coveredChunkZ);
        return array;
    }

    protected void fillNoiseColumn(double[] noiseArray, int quadChunkX, int quadChunkZ) {
        NoiseSettings noiseSettings = settings.get().noiseSettings();
        double heightValue;
        double heightScale;
        if (customNoiseGenerator != null) {
            heightValue = customHeight(customNoiseGenerator, quadChunkX, quadChunkZ);
            heightScale = customScale(customNoiseGenerator, heightValue, quadChunkX, quadChunkZ);
        } else if (islandNoise != null) {
            heightValue = getIslandHeightValue(islandNoise, quadChunkX, quadChunkZ);
            heightScale = getIslandHeightScale(heightValue);
        } else {
            float totalScale = 0;
            float totalDepth = 0;
            float multiplier = 0;
            int seaLevel = getSeaLevel();
            float depth = biomeSource.getNoiseBiome(quadChunkX, seaLevel, quadChunkZ).getDepth();

            for (int xOffs = -2; xOffs <= 2; ++xOffs) {
                for (int zOffs = -2; zOffs <= 2; ++zOffs) {
                    Biome newBiome = biomeSource.getNoiseBiome(quadChunkX + xOffs, seaLevel, quadChunkZ + zOffs);
                    float newDepth = newBiome.getDepth();
                    float newScale = newBiome.getScale();
                    float amplifiedDepth;
                    float amplifiedScale;
                    if (noiseSettings.isAmplified() && newDepth > 0) {
                        amplifiedDepth = getAmplifiedDepth(newDepth);
                        amplifiedScale = getAmplifiedScale(newScale);
                    } else {
                        amplifiedDepth = newDepth;
                        amplifiedScale = newScale;
                    }
                    float scale = (newDepth > depth ? 0.5f : 1) * BIOME_WEIGHTS[xOffs + 2 + (zOffs + 2) * 5] / (amplifiedDepth + 2);
                    totalScale += amplifiedScale * scale;
                    totalDepth += amplifiedDepth * scale;
                    multiplier += scale;
                }
            }

            float averageDepth = totalDepth / multiplier;
            float averageScale = totalScale / multiplier;
            heightValue = calculateHeightValue(averageDepth);
            heightScale = calculateHeightScale(averageScale);
        }
        genNoise(noiseArray, quadChunkX, quadChunkZ, noiseSettings, heightValue, heightScale);
    }

    protected void genNoise(double[] noiseArray, int quadChunkX, int quadChunkZ, NoiseSettings noiseSettings, double heightValue, double heightScale) {
        double xzScale = 684.412 * noiseSettings.noiseSamplingSettings().xzScale();
        double yScale = 684.412 * noiseSettings.noiseSamplingSettings().yScale();
        double xzFactor = xzScale / noiseSettings.noiseSamplingSettings().xzFactor();
        double yFactor = yScale / noiseSettings.noiseSamplingSettings().yFactor();
        double topTarget = noiseSettings.topSlideSettings().target();
        double topSize = noiseSettings.topSlideSettings().size();
        double topOffset = noiseSettings.topSlideSettings().offset();
        double bottomTarget = noiseSettings.bottomSlideSettings().target();
        double bottomSize = noiseSettings.bottomSlideSettings().size();
        double bottomOffset = noiseSettings.bottomSlideSettings().offset();
        double density = noiseSettings.randomDensityOffset() ? getRandomDensity(quadChunkX, quadChunkZ) : 0;
        double densityFactor = noiseSettings.densityFactor();
        double densityOffset = noiseSettings.densityOffset();

        for (int chunkY = 0; chunkY <= chunkCountY; ++chunkY) {
            double noiseValue = sampleAndClampNoise(quadChunkX, chunkY, quadChunkZ, xzScale, yScale, xzFactor, yFactor);
            double realDensity = 1 - (double) chunkY * 2 / (double) chunkCountY + density;
            realDensity = realDensity * densityFactor + densityOffset;
            double height = (realDensity + heightValue) * heightScale;
            if (height > 0) {
                noiseValue = noiseValue + height * 4;
            } else {
                noiseValue = noiseValue + height;
            }
            if (topSize > 0) {
                double topNoise = ((double) (chunkCountY - chunkY) - topOffset) / topSize;
                noiseValue = MathHelper.clampedLerp(topTarget, noiseValue, topNoise);
            }
            if (bottomSize > 0) {
                double bottomNoise = ((double) chunkY - bottomOffset) / bottomSize;
                noiseValue = MathHelper.clampedLerp(bottomTarget, noiseValue, bottomNoise);
            }
            noiseArray[chunkY] = noiseValue;
        }
    }

    protected float getIslandHeightValue(SimplexNoiseGenerator islandNoise, int quadChunkX, int quadChunkZ) {
        return EndBiomeProvider.getHeightValue(islandNoise, quadChunkX, quadChunkZ) - 8;
    }

    /**
     * For sky island gen.
     */
    protected double getIslandHeightScale(double heightValue) {
        return heightValue > 0 ? 0.25 : 1;
    }

    protected double getRandomDensity(int chunkX, int chunkZ) {
        double noiseValue = depthNoise.getValue(chunkX * 200, 10, chunkZ * 200, 1, 0, true);
        double realNoiseValue;
        if (noiseValue < 0) {
            realNoiseValue = -noiseValue * 0.3;
        } else {
            realNoiseValue = noiseValue;
        }

        double density = realNoiseValue * 24.575625 - 2;
        return density < 0 ? density * 0.009486607142857142 : Math.min(density, 1) * 0.006640625;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Type type) {
        return iterateNoiseColumn(x, z, null, type.isOpaque());
    }

    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        BlockState[] stateArray = new BlockState[chunkCountY * chunkHeight];
        iterateNoiseColumn(x, z, stateArray, null);
        return new Blockreader(stateArray);
    }

    protected int iterateNoiseColumn(int x, int z, @Nullable BlockState[] stateArray, @Nullable Predicate<BlockState> predicate) {
        int coveredChunkX = Math.floorDiv(x, chunkWidth);
        int coveredChunkZ = Math.floorDiv(z, chunkWidth);
        int remainderX = Math.floorMod(x, chunkWidth);
        int remainderZ = Math.floorMod(z, chunkWidth);
        double rpX = (double) remainderX / (double) chunkWidth;
        double rpZ = (double) remainderZ / (double) chunkWidth;
        double[][] noiseArray = new double[][]{ makeAndFillNoiseColumn(coveredChunkX, coveredChunkZ), makeAndFillNoiseColumn(coveredChunkX, coveredChunkZ + 1), makeAndFillNoiseColumn(coveredChunkX + 1, coveredChunkZ), makeAndFillNoiseColumn(coveredChunkX + 1, coveredChunkZ + 1) };

        for (int chunkY = chunkCountY - 1; chunkY >= 0; --chunkY) {
            double xyzNoise = noiseArray[0][chunkY];
            double xyz1Noise = noiseArray[1][chunkY];
            double x1yzNoise = noiseArray[2][chunkY];
            double x1yz1Noise = noiseArray[3][chunkY];
            double xy1zNoise = noiseArray[0][chunkY + 1];
            double xy1z1Noise = noiseArray[1][chunkY + 1];
            double x1y1zNoise = noiseArray[2][chunkY + 1];
            double x1y1z1Noise = noiseArray[3][chunkY + 1];

            for (int y = chunkHeight - 1; y >= 0; --y) {
                double yOffset = (double) y / (double) chunkHeight;
                double depth = MathHelper.lerp3(yOffset, rpX, rpZ, xyzNoise, xy1zNoise, x1yzNoise, x1y1zNoise, xyz1Noise, xy1z1Noise, x1yz1Noise, x1y1z1Noise);
                int realY = chunkY * chunkHeight + y;
                BlockState state = generateBaseState(depth, x, realY, z);
                if (stateArray != null) {
                    stateArray[realY] = state;
                }
                if (predicate != null && predicate.test(state)) {
                    return realY + 1;
                }
            }
        }
        return 0;
    }

    protected BlockState generateBaseState(double depth, int realX, int realHeight, int realZ) {
        BlockState state;
        if (depth > 0) {
            state = defaultBlock;
        } else if (realHeight < getSeaLevel()) {
            state = defaultFluid;
        } else {
            state = getAir();
        }
        return state;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion region, IChunk chunk) {
        ChunkPos pos = chunk.getPos();
        int chunkX = pos.x;
        int chunkZ = pos.z;
        SharedSeedRandom randomIn = new SharedSeedRandom();
        randomIn.setBaseChunkSeed(chunkX, chunkZ);
        int minBlockX = pos.getMinBlockX();
        int minBlockZ = pos.getMinBlockZ();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        generateSurface(region, chunk, randomIn, minBlockX, minBlockZ, mutablePos);
        setBedrock(chunk, randomIn);
    }

    protected void generateSurface(WorldGenRegion region, IChunk chunk, SharedSeedRandom randomIn, int minBlockX, int minBlockZ, BlockPos.Mutable mutablePos) {
        for (int rx = 0; rx < 16; rx++) {
            for (int rz = 0; rz < 16; rz++) {
                int x = minBlockX + rx;
                int z = minBlockZ + rz;
                int height = chunk.getHeight(Heightmap.Type.WORLD_SURFACE_WG, rx, rz) + 1;
                double surfaceNoiseValue = surfaceNoise.getSurfaceNoiseValue((double) x * 0.0625D, (double) z * 0.0625D, 0.0625D, (double) rx * 0.0625D) * 15.0D;
                if (shouldGenerateTerrain(x, height, z)) {
                    buildSurfaceAt(region, chunk, randomIn, mutablePos, x, z, height, surfaceNoiseValue);
                }
            }
        }
    }

    protected void buildSurfaceAt(WorldGenRegion region, IChunk chunk, SharedSeedRandom randomIn, BlockPos.Mutable mutablePos, int x, int z, int height, double surfaceNoiseValue) {
        region.getBiome(mutablePos.set(x, height, z)).buildSurfaceAt(randomIn, chunk, x, z, height, surfaceNoiseValue, defaultBlock, defaultFluid, getSeaLevel(), region.getSeed());
    }

    protected void setBedrock(IChunk chunk, Random randomIn) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        int minBlockX = chunk.getPos().getMinBlockX();
        int minBlockZ = chunk.getPos().getMinBlockZ();
        DimensionSettings settings = this.settings.get();
        int bedrockFloorPosition = settings.getBedrockFloorPosition();
        int bedrockRoofPosition = height - 1 - settings.getBedrockRoofPosition();
        boolean hasRoof = bedrockRoofPosition + 4 >= 0 && bedrockRoofPosition < height;
        boolean hasFloor = bedrockFloorPosition + 4 >= 0 && bedrockFloorPosition < height;
        if (hasRoof || hasFloor) {
            for(BlockPos pos : BlockPos.betweenClosed(minBlockX, 0, minBlockZ, minBlockX + 15, 0, minBlockZ + 15)) {
                if (shouldGenerateTerrain(pos.getX(), pos.getY(), pos.getZ())) {
                    if (hasRoof) {
                        buildBedrockRoof(chunk, randomIn, mutablePos, bedrockRoofPosition, pos);
                    }
                    if (hasFloor) {
                        buildBedrockFloor(chunk, randomIn, mutablePos, bedrockFloorPosition, pos);
                    }
                }
            }
        }
    }

    @Override
    public void fillFromNoise(IWorld world, StructureManager manager, IChunk chunk) {
        ObjectList<StructurePiece> structurePieces = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> jigsawJunctions = new ObjectArrayList<>(32);
        ChunkPos pos = chunk.getPos();
        int chunkX = pos.x;
        int chunkZ = pos.z;
        int minBlockX = chunkX << 4;
        int minBlockZ = chunkZ << 4;
        handleNoiseAffectingStructures(manager, structurePieces, jigsawJunctions, pos, minBlockX, minBlockZ);
        double[][][] noises = new double[2][chunkCountZ + 1][chunkCountY + 1];
        for(int i = 0; i < chunkCountZ + 1; ++i) {
            noises[0][i] = new double[chunkCountY + 1];
            fillNoiseColumn(noises[0][i], chunkX * chunkCountX, chunkZ * chunkCountZ + i);
            noises[1][i] = new double[chunkCountY + 1];
        }
        ChunkPrimer primer = (ChunkPrimer) chunk;
        Heightmap oceanMap = primer.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap surfaceMap = primer.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        ObjectListIterator<StructurePiece> pieceItr = structurePieces.iterator();
        ObjectListIterator<JigsawJunction> junctionItr = jigsawJunctions.iterator();
        updateTerrain(structurePieces, jigsawJunctions, chunkX, chunkZ, minBlockX, minBlockZ, noises, primer, oceanMap, surfaceMap, mutablePos, pieceItr, junctionItr);
    }

    protected void updateTerrain(ObjectList<StructurePiece> structurePieces, ObjectList<JigsawJunction> jigsawJunctions, int chunkX, int chunkZ, int minBlockX, int minBlockZ, double[][][] noises, ChunkPrimer primer, Heightmap oceanMap, Heightmap surfaceMap, BlockPos.Mutable mutablePos, ObjectListIterator<StructurePiece> pieceItr, ObjectListIterator<JigsawJunction> junctionItr) {
        for (int cx = 0; cx < chunkCountX; ++cx) {
            for (int cz = 0; cz < chunkCountZ + 1; ++cz) {
                fillNoiseColumn(noises[1][cz], chunkX * chunkCountX + cx + 1, chunkZ * chunkCountZ + cz);
            }

            for (int cz = 0; cz < chunkCountZ; ++cz) {
                ChunkSection section = primer.getOrCreateSection(15);
                section.acquire();

                for (int cy = chunkCountY - 1; cy >= 0; --cy) {
                    double n1 = noises[0][cz][cy];
                    double n2 = noises[0][cz + 1][cy];
                    double n3 = noises[1][cz][cy];
                    double n4 = noises[1][cz + 1][cy];
                    double n5 = noises[0][cz][cy + 1];
                    double n6 = noises[0][cz + 1][cy + 1];
                    double n7 = noises[1][cz][cy + 1];
                    double n8 = noises[1][cz + 1][cy + 1];

                    for(int y = chunkHeight - 1; y >= 0; --y) {
                        int realY = cy * chunkHeight + y;
                        int relativeY = realY & 15;
                        int chunkY = realY >> 4;
                        if (section.bottomBlockY() >> 4 != chunkY) {
                            section.release();
                            section = primer.getOrCreateSection(chunkY);
                            section.acquire();
                        }

                        double py = y / (double) chunkHeight;
                        double n9 = MathHelper.lerp(py, n1, n5);
                        double n10 = MathHelper.lerp(py, n3, n7);
                        double n11 = MathHelper.lerp(py, n2, n6);
                        double n12 = MathHelper.lerp(py, n4, n8);

                        for (int i = 0; i < chunkWidth; ++i) {
                            int realX = minBlockX + cx * chunkWidth + i;
                            int relativeX = realX & 15;
                            double pxz = (double) i / (double) chunkWidth;
                            double n14 = MathHelper.lerp(pxz, n9, n10);
                            double n15 = MathHelper.lerp(pxz, n11, n12);

                            for(int j = 0; j < chunkWidth; ++j) {
                                int realZ = minBlockZ + cz * chunkWidth + j;
                                int relativeZ = realZ & 15;
                                double pxz2 = (double) j / (double) chunkWidth;
                                double n16 = MathHelper.lerp(pxz2, n14, n15);
                                double depth = MathHelper.clamp(n16 / 200, -1, 1);

                                int mbbX;
                                int mbbY;
                                int mbbZ;

                                for (depth = depth / 2 - depth * depth * depth / 24; pieceItr.hasNext(); depth += getContribution(mbbX, mbbY, mbbZ) * 0.8) {
                                    StructurePiece piece = pieceItr.next();
                                    MutableBoundingBox boundingBox = piece.getBoundingBox();
                                    mbbX = Math.max(0, Math.max(boundingBox.x0 - realX, realX - boundingBox.x1));
                                    mbbY = realY - (boundingBox.y0 + (piece instanceof AbstractVillagePiece ? ((AbstractVillagePiece) piece).getGroundLevelDelta() : 0));
                                    mbbZ = Math.max(0, Math.max(boundingBox.z0 - realZ, realZ - boundingBox.z1));
                                }

                                pieceItr.back(structurePieces.size());

                                while (junctionItr.hasNext()) {
                                    JigsawJunction junction = junctionItr.next();
                                    mbbX = realX - junction.getSourceX();
                                    mbbY = realY - junction.getSourceGroundY();
                                    mbbZ = realZ - junction.getSourceZ();
                                    depth += getContribution(mbbX, mbbY, mbbZ) * 0.4;
                                }

                                junctionItr.back(jigsawJunctions.size());
                                BlockState baseState = generateBaseState(depth, realX, realY, realZ);

                                if (shouldGenerateTerrain(realX, realY, realZ) && !baseState.isAir()) {
                                    mutablePos.set(realX, realY, realZ);
                                    if (baseState.getLightValue(primer, mutablePos) != 0) {
                                        primer.addLight(mutablePos);
                                    }
                                    placeBlock(section, realX, realY, realZ, relativeX, relativeY, relativeZ, baseState);
                                    oceanMap.update(relativeX, realY, relativeZ, baseState);
                                    surfaceMap.update(relativeX, realY, relativeZ, baseState);
                                }
                            }
                        }
                    }
                }

                section.release();
            }

            double[][] noiseArray = noises[0];
            noises[0] = noises[1];
            noises[1] = noiseArray;
        }
    }

    protected void handleNoiseAffectingStructures(StructureManager manager, ObjectList<StructurePiece> structurePieces, ObjectList<JigsawJunction> jigsawJunctions, ChunkPos pos, int minBlockX, int minBlockZ) {
        for (Structure<?> structure : Structure.NOISE_AFFECTING_FEATURES) {
            manager.startsForFeature(SectionPos.of(pos, 0), structure).flatMap(structureStart -> structureStart.getPieces().stream()).filter(piece -> piece.isCloseToChunk(pos, 12)).forEach(piece -> {
                if (piece instanceof AbstractVillagePiece) {
                    AbstractVillagePiece villagePiece = (AbstractVillagePiece) piece;
                    JigsawPattern.PlacementBehaviour behaviour = villagePiece.getElement().getProjection();
                    if (behaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                        structurePieces.add(villagePiece);
                    }
                    for (JigsawJunction junction : villagePiece.getJunctions()) {
                        int sourceX = junction.getSourceX();
                        int sourceZ = junction.getSourceZ();
                        if (sourceX > minBlockX - 12 && sourceZ > minBlockZ - 12 && sourceX < minBlockX + 15 + 12 && sourceZ < minBlockZ + 15 + 12) {
                            jigsawJunctions.add(junction);
                        }
                    }
                } else {
                    structurePieces.add(piece);
                }
            });
        }
    }

    protected static double getContribution(int x, int y, int z) {
        int i = x + 12;
        int j = y + 12;
        int k = z + 12;
        if (i >= 0 && i < 24) {
            if (j >= 0 && j < 24) {
                return k >= 0 && k < 24 ? (double) BEARD_KERNEL[k * 24 * 24 + i * 24 + j] : 0;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    protected static double computeContribution(int x, int y, int z) {
        double distSqr = x * x + z * z;
        double ySqr = (y + 0.5) * (y + 0.5);
        double d1 = Math.pow(Math.E, -(ySqr / 16 + distSqr / 16));
        double d2 = -(y + 0.5) * MathHelper.fastInvSqrt(ySqr / 2 + distSqr / 2) / 2;
        return d1 * d2;
    }

    @Override
    public int getGenDepth() {
        return height;
    }

    @Override
    public int getSeaLevel() {
        return settings.get().seaLevel();
    }

    protected Supplier<DimensionSettings> getSettingsSupplier() {
        return settings;
    }

    protected long getSeed() {
        return seed;
    }

    @Override
    public List<MobSpawnInfo.Spawners> getMobsAt(Biome biome, StructureManager manager, EntityClassification classification, BlockPos pos) {
        if (!shouldGenerateTerrain(pos.getX(), pos.getY(), pos.getZ())) {
            return Collections.emptyList();
        }
        List<MobSpawnInfo.Spawners> spawns = StructureSpawnManager.getStructureSpawns(manager, classification, pos);
        if (spawns != null) {
            return spawns;
        }
        return super.getMobsAt(biome, manager, classification, pos);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        if (!settings.get().disableMobGeneration()) {
            int centerX = region.getCenterX();
            int centerZ = region.getCenterZ();
            if (shouldGenerateTerrain(new ChunkPos(centerX, centerX))) {
                Biome biome = region.getBiome(new ChunkPos(centerX, centerZ).getWorldPosition());
                SharedSeedRandom randomIn = new SharedSeedRandom();
                randomIn.setDecorationSeed(region.getSeed(), centerX << 4, centerZ << 4);
                WorldEntitySpawner.spawnMobsForChunkGeneration(region, biome, centerX, centerZ, randomIn);
            }
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenRegion region, StructureManager manager) {
        int centerX = region.getCenterX();
        int centerZ = region.getCenterZ();
        boolean shouldApply = shouldGenerateTerrain(new ChunkPos(centerX, centerZ));
        if (shouldApply) {
            super.applyBiomeDecoration(region, manager);
        }
    }

    @Override
    public void applyCarvers(long seed, BiomeManager manager, IChunk chunk, GenerationStage.Carving stage) {
        if (shouldGenerateTerrain(chunk.getPos())) {
            super.applyCarvers(seed, manager, chunk, stage);
        }
    }

    @Override
    public void createStructures(DynamicRegistries registries, StructureManager structureManager, IChunk chunk, TemplateManager templateManager, long seed) {
        if (shouldGenerateTerrain(chunk.getPos())) {
            super.createStructures(registries, structureManager, chunk, templateManager, seed);
        }
    }

    @Override
    public void createReferences(ISeedReader reader, StructureManager manager, IChunk chunk) {
        if (shouldGenerateTerrain(chunk.getPos())) {
            super.createReferences(reader, manager, chunk);
        }
    }

    @Override
    public boolean hasStronghold(ChunkPos pos) {
        if (shouldGenerateTerrain(pos)) {
            return super.hasStronghold(pos);
        }
        return false;
    }

    protected boolean shouldGenerateTerrain(ChunkPos pos) {
        BlockPos worldPosition = pos.getWorldPosition();
        boolean shouldApply = true;
        X:
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < 16; z++) {
                    if (!shouldGenerateTerrain(x, y, z)) {
                        shouldApply = false;
                        break X;
                    }
                }
            }
        }
        return shouldApply;
    }


    //  ----------------------------------------EXTENSIONS----------------------------------------

    /**
     * Get a BlockState for air generation. <b>Should return a BlockState that is air.</b>
     * @return The BlockState
     */
    protected BlockState getAir() {
        return Blocks.AIR.defaultBlockState();
    }

    /**
     * Build bedrock floor for the world.
     * @param chunk The chunk
     * @param randomIn The {@link SharedSeedRandom Random} instance of the world
     * @param mutablePos The mutable pos used to place block
     * @param baseHeight The base height of the bedrock floor
     * @param immutablePos The immutable pos that contains x and z coordinate
     */
    protected void buildBedrockFloor(IChunk chunk, Random randomIn, BlockPos.Mutable mutablePos, int baseHeight, BlockPos immutablePos) {
        for (int y = 4; y >= 0; --y) {
            if (y <= randomIn.nextInt(5)) {
                chunk.setBlockState(mutablePos.set(immutablePos.getX(), baseHeight + y, immutablePos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
            }
        }
    }

    /**
     * Build bedrock roof for the world.
     * @param chunk The chunk
     * @param randomIn The {@link SharedSeedRandom Random} instance of the world
     * @param mutablePos The mutable pos used to place block
     * @param baseHeight The base height of the bedrock roof
     * @param immutablePos the immutable pos that contains x and z coordinate
     */
    protected void buildBedrockRoof(IChunk chunk, Random randomIn, BlockPos.Mutable mutablePos, int baseHeight, BlockPos immutablePos) {
        for (int y = 0; y < 5; ++y) {
            if (y <= randomIn.nextInt(5)) {
                chunk.setBlockState(mutablePos.set(immutablePos.getX(), baseHeight - y, immutablePos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
            }
        }
    }

    /**
     * Place a block at the position of the chunk.
     * @param section The {@link ChunkSection} used to place block
     * @param realX The real x coordinate
     * @param realY The real y coordinate
     * @param realZ The real z coordinate
     * @param relativeX The x coordinate related to the chunk (0 ~ 15)
     * @param relativeY The x coordinate related to the chunk (0 ~ 15)
     * @param relativeZ The x coordinate related to the chunk (0 ~ 15)
     * @param baseState The state that will be used for the position
     */
    protected void placeBlock(ChunkSection section, int realX, int realY, int realZ, int relativeX, int relativeY, int relativeZ, BlockState baseState) {
        section.setBlockState(relativeX, relativeY, relativeZ, baseState, false);
    }

    /**
     * @param x The real x coordinate
     * @param y The real y coordinate
     * @param z The real z coordinate
     * @return True if terrain can be generated at the position
     */
    @Contract(pure = true)
    protected boolean shouldGenerateTerrain(int x, int y, int z) {
        return true;
    }

    // For amplified worlds
    protected float getAmplifiedScale(float newScale) {
        return 1 + newScale * 4;
    }
    protected float getAmplifiedDepth(float newDepth) {
        return 1 + newDepth * 2;
    }

    /**
     * The smaller the value returned by the method, the smoother the terrain.
     * @return The height scale
     */
    protected double calculateHeightScale(float averageScale) {
        return 96.0D / (averageScale * 0.9F + 0.1F);
    }

    /**
     * The smaller the value returned by the method, the lower the terrain.
     * @return The height value
     */
    protected double calculateHeightValue(float averageDepth) {
        return (averageDepth * 0.5F - 0.125F) * 0.265625D;
    }

    protected void setCustomNoiseGenerator(SimplexNoiseGenerator customNoiseGenerator) {
        Objects.requireNonNull(customNoiseGenerator);
        if (this.customNoiseGenerator == null) {
            this.customNoiseGenerator = customNoiseGenerator;
        } else {
            throw new IllegalStateException("CustomNoiseGenerator exists");
        }
    }

//  These methods SHOULD be overridden if customNoiseGenerator is non-null
    /**
     * @return The height value
     */
    protected double customHeight(SimplexNoiseGenerator customNoiseGenerator, int quadChunkX, int quadChunkZ) {
        throw new UnsupportedOperationException();
    }
    /**
     * @return The height scale
     */
    protected double customScale(SimplexNoiseGenerator customNoiseGenerator, double heightValue, int quadChunkX, int quadChunkZ) {
        throw new UnsupportedOperationException();
    }

//  TODO: Untested
    protected int getOctaveCount() {
        return 16;
    }

    protected boolean checkNoFluidBiomes() {
        return true;
    }
}
