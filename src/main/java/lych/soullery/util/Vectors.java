package lych.soullery.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

import java.util.Objects;
import java.util.Random;

public final class Vectors {
    private Vectors() {}

    public static boolean isVertical(Vector3d a, Vector3d b) {
        return a.dot(b) == 0;
    }

    public static double getAngle(Vector3d a, Vector3d b) {
        return Math.acos(getCosAngle(a, b));
    }

    public static double getCosAngle(Vector3d a, Vector3d b) {
        return a.dot(b) / (a.length() * b.length());
    }

    public static Vector3d lerp(double amount, Vector3d a, Vector3d b) {
        double x = MathHelper.lerp(amount, a.x, b.x);
        double y = MathHelper.lerp(amount, a.y, b.y);
        double z = MathHelper.lerp(amount, a.z, b.z);
        return new Vector3d(x, y, z);
    }

    public static Vector3d rotate90(Vector3d posToRotate, Vector3d center, boolean clockwise) {
        return clockwise ? new Vector3d(center.x + posToRotate.z - center.z, posToRotate.y, center.z - posToRotate.x + center.x) : new Vector3d(center.x - posToRotate.z + center.z, posToRotate.y, center.z + posToRotate.x - center.x);
    }

    public static Vector3d rotateTo(Vector3d posToRotate, double rad, boolean clockwise) {
        return rotateTo(posToRotate, Vector3d.ZERO, rad, clockwise);
    }

    public static Vector3d rotateTo(Vector3d posToRotate, Vector3d center, double rad, boolean clockwise) {
        double distanceToCenter = posToRotate.distanceTo(center);
        if (rad == 0) {
            return new Vector3d(center.x, posToRotate.y, center.z - distanceToCenter);
        }
        double radToCenter = radTo(posToRotate, center);
        return clockwise ? new Vector3d(MathHelper.sin((float) (radToCenter + rad)) * distanceToCenter + center.x, posToRotate.y, MathHelper.cos((float) (radToCenter + rad)) * distanceToCenter + center.z) : new Vector3d(MathHelper.sin((float) (radToCenter + Math.PI * 2 - rad)) * distanceToCenter + center.x, posToRotate.y, MathHelper.cos((float) (radToCenter + Math.PI * 2 - rad)) * distanceToCenter + center.z);
    }

    public static double radTo(Vector3d vec, Vector3d target) {
        return MathHelper.atan2(target.x - vec.x, target.z - vec.z);
    }

    public static Vector3d midpoint(Vector3d a, Vector3d b) {
        if (Objects.equals(a, b)) {
            return copyOf(a);
        }
        return new Vector3d((a.x + b.x) / 2, (a.y + b.y) / 2, (a.z + b.z) / 2);
    }

    public static Vector3d moveToGround(Vector3d vec, World world) {
        return new Vector3d(vec.x, world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(vec)).getY(), vec.z);
    }

    public static Vector3d randomVector(Random random) {
        return new Vector3d(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1);
    }

    public static Vector3d randomVector(Random random, double length) {
        return randomVector(random).normalize().scale(length);
    }

    public static Vector3d copyOf(Vector3d vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vector3d copyWithoutY(Vector3d vec) {
        return new Vector3d(vec.x, 0, vec.z);
    }
}
