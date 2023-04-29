package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.api.event.ArrowSpawnEvent;
import lych.soullery.util.mixin.IAbstractArrowEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

import java.util.function.Supplier;

public class SkeletonReinforcement extends BowAttackReinforcement {
    public static final double ENHANCED_ARROW_SPEED_MULTIPLIER = 0.1;

    public SkeletonReinforcement() {
        this(EntityType.SKELETON);
    }

    protected SkeletonReinforcement(EntityType<?> type) {
        super(type);
    }

    protected SkeletonReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    protected SkeletonReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected void onSpawn(AbstractArrowEntity arrow, PlayerEntity player, int level, ArrowSpawnEvent event) {
        ((IAbstractArrowEntityMixin) arrow).setEnhancedLevel(level);
    }

    @Override
    protected void onImpact(AbstractArrowEntity arrow, LivingEntity owner, int level, ProjectileImpactEvent.Arrow event) {}
}
