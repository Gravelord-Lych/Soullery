package lych.soullery.entity.iface;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

public interface ISeeker {
    @Nullable
    Entity getTarget();

    void setTarget(@Nullable Entity target);

    default void tickSeeker() {
        if (getTarget() != null && canSeek()) {
            Vector3d targetPos = getVectorToTarget(getTarget()).scale(0.8);
            Vector3d movement = ((Entity) this).getDeltaMovement();

            double movementLength = movement.length();
            double targetPosLength = targetPos.length();
            double totalLength = Math.sqrt(movementLength * movementLength + targetPosLength * targetPosLength);

            double dotProduct = movement.dot(targetPos) / (movementLength * targetPosLength);

            if (dotProduct > getSeekThreshold()) {
                Vector3d newMotion = movement.scale(movementLength / totalLength).add(targetPos.scale(movementLength / totalLength));
                ((Entity) this).setDeltaMovement(newMotion.add(0, 0.045F, 0));
            }
        }
    }

    default Vector3d getVectorToTarget(Entity target) {
        return new Vector3d(target.getX() - ((Entity) this).getX(), target.getEyeHeight() - ((Entity) this).getY(), target.getZ() - ((Entity) this).getZ());
    }

    default double getSeekThreshold() {
        return 0.5;
    }

    default boolean canSeek() {
        return true;
    }
}
