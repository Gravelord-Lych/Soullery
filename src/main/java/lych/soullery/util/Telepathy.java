package lych.soullery.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Telepathy {
    public static final double DEFAULT_ANGLE_WEIGHT = 4;

    private Telepathy() {}

    @Nullable
    public static <T extends Entity> T telepathicAttackFirst(Class<T> type, Entity entity, double reachDistance, double attackAngle, double angleWeight, Predicate<? super T> attackPredicate) {
        List<T> res = telepathicAttack(type, entity, reachDistance, attackAngle, angleWeight, 1, attackPredicate);
        return res.isEmpty() ? null : res.get(0);
    }

    public static <T extends Entity> List<T> telepathicAttack(Class<T> type, Entity entity, double reachDistance, double attackAngle, double angleWeight, int maxCount, Predicate<? super T> attackPredicate) {
        return telepathicAttack(type, entity, reachDistance, attackAngle, angleWeight, maxCount, true, attackPredicate);
    }

    @Nullable
    public static <T extends Entity> T telepathicAttackFirstMetaphysically(Class<T> type, Entity entity, double reachDistance, double attackAngle, double angleWeight, Predicate<? super T> attackPredicate) {
        List<T> res = telepathicAttackMetaphysically(type, entity, reachDistance, attackAngle, angleWeight, 1, attackPredicate);
        return res.isEmpty() ? null : res.get(0);
    }

    public static <T extends Entity> List<T> telepathicAttackMetaphysically(Class<T> type, Entity entity, double reachDistance, double attackAngle, double angleWeight, int maxCount, Predicate<? super T> attackPredicate) {
        return telepathicAttack(type, entity, reachDistance, attackAngle, angleWeight, maxCount, false, attackPredicate);
    }

    private static <T extends Entity> List<T> telepathicAttack(Class<T> type, Entity entity, double reachDistance, double attackAngle, double angleWeight, int maxCount, boolean testBlock, Predicate<? super T> attackPredicate) {
        Vector3d position = entity.getEyePosition(0);
        Vector3d viewVector = entity.getViewVector(1);
        AxisAlignedBB possibleEntitiesBB = entity.getBoundingBox().expandTowards(viewVector.scale(reachDistance)).inflate(1);
        return findValidEntities(type, entity, possibleEntitiesBB, viewVector, position, attackAngle, angleWeight, maxCount, testBlock, attackPredicate);
    }

    private static <T extends Entity> List<T> findValidEntities(Class<T> type, Entity entity, AxisAlignedBB bb, Vector3d viewVector, Vector3d position, double attackAngle, double angleWeight, int maxCount, boolean testBlock, Predicate<? super T> attackPredicate) {
        List<T> entities = entity.level.getEntitiesOfClass(type, bb, attackPredicate.and(anotherEntity -> anotherEntity != entity));
        entities.removeIf(anotherEntity -> {
            if (getAngle(entity, anotherEntity, viewVector) > attackAngle) {
                return true;
            }
            if (testBlock) {
                Vector3d targetPos = anotherEntity.position();
                BlockRayTraceResult blockRay = entity.level.clip(new RayTraceContext(position, targetPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
                return blockRay.getType() != RayTraceResult.Type.MISS;
            }
            return false;
        });
        return entities.stream().sorted(Comparator.comparingDouble(o -> o.distanceTo(entity) + getAngle(entity, o, viewVector) * angleWeight)).limit(maxCount).collect(Collectors.toList());
    }

    private static <T extends Entity> double getAngle(Entity entity, T anotherEntity, Vector3d viewVector) {
        return Vectors.getAngle(EntityUtils.eyeOf(entity).vectorTo(EntityUtils.centerOf(anotherEntity)), viewVector);
    }
}
