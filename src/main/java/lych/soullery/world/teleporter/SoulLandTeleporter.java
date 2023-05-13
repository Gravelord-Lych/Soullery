package lych.soullery.world.teleporter;

import com.google.common.collect.ImmutableSet;
import lych.soullery.block.ModBlocks;
import lych.soullery.tag.ModFluidTags;
import lych.soullery.util.PositionCalculators;
import lych.soullery.util.TeleporterUtils;
import lych.soullery.util.WorldUtils;
import lych.soullery.world.gen.biome.SLBiomes;
import lych.soullery.world.gen.dimension.ModDimensions;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

import static lych.soullery.Soullery.LOGGER;

public class SoulLandTeleporter implements ITeleporter {
    private static final int ABOVE_AMOUNT = 2;
    private static final Set<ResourceLocation> SPAWNABLE_BIOMES = ImmutableSet.of(
            SLBiomes.SOUL_PLAINS,
            SLBiomes.SOUL_MOUNTAINS,
            SLBiomes.SPIKED_SOUL_PLAINS,
            SLBiomes.PARCHED_DESERT,
            SLBiomes.PARCHED_DESERT_HILLS,
            SLBiomes.CRIMSON_PLAINS,
            SLBiomes.CRIMSON_HILLS,
            SLBiomes.CRIMSON_PLAINS_EDGE,
            SLBiomes.WARPED_PLAINS,
            SLBiomes.WARPED_HILLS,
            SLBiomes.WARPED_PLAINS_EDGE
    ).stream().map(RegistryKey::location).collect(ImmutableSet.toImmutableSet());

    @Nullable
    private final BlockPos expected;
    private boolean positionChanged;

    public SoulLandTeleporter(@Nullable BlockPos expected) {
        this.expected = expected;
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        if (expected != null) {
            LOGGER.info("Found expected position: {}", expected);
            float h = 0.1f;
            float w = entity.getBbWidth() * 0.8f;
            AxisAlignedBB bb = AxisAlignedBB.ofSize(w, h, w).move(expected);
            boolean inWall = destWorld.getBlockCollisions(entity, bb, (state, pos) -> state.isSuffocating(destWorld, pos)).findAny().isPresent();
            if (!inWall && getLavaBeneathPos(destWorld, expected) < 0) {
                LOGGER.info("Expected position is available");
                return TeleporterUtils.createCommonInfo(entity, expected);
            } else {
                LOGGER.warn("Expected position is unavailable, we'll find another one");
                positionChanged = true;
            }
        }
        BlockPos pos = entity.blockPosition();
        pos = recalcPos(destWorld, pos);
        RegistryKey<Biome> biome = destWorld.getBiomeName(pos).orElse(null);
        if (biome != null) {
            destWorld.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos), 3, pos);
            if (destWorld.dimension() != ModDimensions.SOUL_LAND || SPAWNABLE_BIOMES.contains(biome.location())) {
                LOGGER.info("Successfully found immediately! Dest biome is {}", biome.location());
                inCaseOfLava(destWorld, pos);
                return TeleporterUtils.createCommonInfo(entity, pos);
            } else {
                LOGGER.info("Safe biome not found (Found: {}), try finding one nearby", biome.location());
                BlockPos biomePos = search(pos, 1, 200, destWorld);
                if (biomePos != null) {
                    biomePos = recalcPos(destWorld, biomePos);
                    LOGGER.info("Successfully found! Dest pos is {}", biomePos);
                    inCaseOfLava(destWorld, biomePos);
                    return TeleporterUtils.createCommonInfo(entity, biomePos);
                } else {
                    LOGGER.info("Safe biome still not found, try finding one in a larger radius");
                    BlockPos biomePos2 = search(pos, 4, 800, destWorld);
                    if (biomePos2 != null) {
                        biomePos2 = recalcPos(destWorld, biomePos2);
                        LOGGER.info("Successfully found! Dest pos is {}", biomePos2);
                        inCaseOfLava(destWorld, biomePos2);
                        return TeleporterUtils.createCommonInfo(entity, biomePos2);
                    } else {
                        LOGGER.warn("Safe biome still not found");
                        BlockPos newPos = recalcPos(destWorld, pos);
                        inCaseOfLava(destWorld, newPos);
                        return TeleporterUtils.createCommonInfo(entity, newPos);
                    }
                }
            }
        } else {
            LOGGER.error("Biome at {} in {} is null", pos, destWorld.dimension().location());
            return TeleporterUtils.createCommonInfo(entity, pos);
        }
    }

    private static void inCaseOfLava(ServerWorld destWorld, BlockPos newPos) {
        int below = getLavaBeneathPos(destWorld, newPos);
        if (below >= 0) {
            placeSoulObsidians(destWorld, newPos, below);
        }
    }

    private static void placeSoulObsidians(ServerWorld destWorld, BlockPos newPos, int below) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                destWorld.setBlock(newPos.offset(x, - ABOVE_AMOUNT - below, z), ModBlocks.SOUL_OBSIDIAN.defaultBlockState(), Constants.BlockFlags.DEFAULT);
            }
        }
    }

    private static int getLavaBeneathPos(ServerWorld destWorld, BlockPos newPos) {
        for (int i = 0; i < 4; i++) {
            if (destWorld.getFluidState(newPos.below(ABOVE_AMOUNT).below(i)).is(ModFluidTags.SOUL_LAVA)) {
                return i;
            }
        }
        return -1;
    }

    private static BlockPos recalcPos(ServerWorld destWorld, BlockPos biomePos) {
        return WorldUtils.calculateSummonPosition2(biomePos.getX(), biomePos.getZ(), destWorld, PositionCalculators::downLq).above(ABOVE_AMOUNT);
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        entity.fallDistance = 0;
        return ITeleporter.super.placeEntity(entity, currentWorld, destWorld, yaw, repositionEntity);
    }

    @Nullable
    private BlockPos search(BlockPos pos, int step, int range, ServerWorld destWorld) {
        return destWorld.getChunkSource().getGenerator().getBiomeSource().findBiomeHorizontal(pos.getX(), pos.getY(), pos.getZ(), range, step, biome -> SPAWNABLE_BIOMES.contains(biome.getRegistryName()), destWorld.random, true);
    }

    public boolean isPositionChanged() {
        return positionChanged;
    }
}
