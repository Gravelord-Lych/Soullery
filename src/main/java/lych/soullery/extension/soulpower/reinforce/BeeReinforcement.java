package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.api.event.ArrowSpawnEvent;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

public class BeeReinforcement extends BowAttackReinforcement {
    private static final Int2IntMap POISON_TIME_MAP = EntityUtils.intChoiceBuilder().range(1).value(80).range(2).value(160).range(3).value(240).build();

    public BeeReinforcement() {
        super(EntityType.BEE);
    }

    @Override
    protected void onSpawn(AbstractArrowEntity arrow, PlayerEntity player, int level, ArrowSpawnEvent event) {}

    @Override
    protected void onImpact(AbstractArrowEntity arrow, LivingEntity owner, int level, ProjectileImpactEvent.Arrow event) {
        if (event.getRayTraceResult() instanceof EntityRayTraceResult) {
            Entity target = ((EntityRayTraceResult) event.getRayTraceResult()).getEntity();
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).addEffect(new EffectInstance(Effects.POISON, POISON_TIME_MAP.get(level)));
            }
        }
    }
}
