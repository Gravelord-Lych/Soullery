package lych.soullery.util;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class TeleporterUtils {
    public static final int DEFAULT_CHUNK_SEARCH_RADIUS = 5;

    private TeleporterUtils() {}

    public static PortalInfo createCommonInfo(Entity entity, BlockPos pos) {
        return createCommonInfo(entity, pos.getX(), pos.getY(), pos.getZ());
    }

    public static PortalInfo createCommonInfo(Entity entity, double x, double y, double z) {
        return createCommonInfo(entity, new Vector3d(x, y, z));
    }

    public static PortalInfo createCommonInfo(Entity entity, Vector3d pos) {
        return new PortalInfo(pos, Vector3d.ZERO, entity.yRot, entity.xRot);
    }

    public static boolean hasAllStructures(ServerWorld world, BlockPos pos, double blockDistance, Structure<?>... structures) {
        return hasAllStructures(world, pos, blockDistance, DEFAULT_CHUNK_SEARCH_RADIUS, false, structures);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean hasAllStructures(ServerWorld world, BlockPos pos, double blockDistance, int chunkDistance, boolean addReference, Structure<?>... structures) {
        if (structures == null || structures.length == 0) {
            return false;
        }
        for (Structure<?> structure : structures) {
            @Nullable BlockPos structurePos = world.findNearestMapFeature(structure, pos, chunkDistance, addReference);
            if (structurePos == null) {
                return false;
            } else if (!pos.closerThan(structurePos, blockDistance)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasAnyStructures(ServerWorld world, BlockPos pos, double blockDistance, Structure<?>... structures) {
        return hasAnyStructures(world, pos, blockDistance, DEFAULT_CHUNK_SEARCH_RADIUS, false, structures);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean hasAnyStructures(ServerWorld world, BlockPos pos, double blockDistance, int chunkDistance, boolean addReference, Structure<?>... structures) {
        if (structures == null || structures.length == 0) {
            return false;
        }
        for (Structure<?> structure : structures) {
            @Nullable BlockPos structurePos = world.findNearestMapFeature(structure, pos, chunkDistance, addReference);
            if (structurePos != null && pos.closerThan(structurePos, blockDistance)) {
                return true;
            }
        }
        return false;
    }
}
