package lych.soullery.extension.control.attack;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import org.jetbrains.annotations.Nullable;

public class RangedTargetFinder implements TargetFinder<MobEntity> {
    public static final RangedTargetFinder DEFAULT = new RangedTargetFinder(3);
    public static final RangedTargetFinder IMPROVED = new RangedTargetFinder(5);

    private final double range;

    public RangedTargetFinder(double range) {
        this.range = range;
    }

    @Nullable
    @Override
    public LivingEntity findTarget(MobEntity operatingMob, ServerPlayerEntity player) {
        EntityRayTraceResult ray = EntityUtils.getEntityRayTraceResult(operatingMob, range);
        if (ray == null) {
            return null;
        }
        return !(ray.getEntity() instanceof LivingEntity) || ray.getEntity() == player ? null : (LivingEntity) ray.getEntity();
    }
}
