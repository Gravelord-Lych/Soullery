package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

public class StrayReinforcement extends SkeletonReinforcement {
    private static final Int2IntMap SLOWDOWN_TIME_MAP = EntityUtils.intChoiceBuilder().range(1).value(100).range(2).value(200).range(3).value(300).build();

    public StrayReinforcement() {
        super(EntityType.STRAY);
    }

    @Override
    protected void onImpact(AbstractArrowEntity arrow, LivingEntity owner, int level, ProjectileImpactEvent.Arrow event) {
        super.onImpact(arrow, owner, level, event);
        if (event.getRayTraceResult() instanceof EntityRayTraceResult) {
            Entity target = ((EntityRayTraceResult) event.getRayTraceResult()).getEntity();
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, SLOWDOWN_TIME_MAP.get(level)));
            }
        }
    }

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
        return super.isCompatibleWith(reinforcement) && reinforcement != Reinforcements.SKELETON;
    }
}
