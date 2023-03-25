package lych.soullery.extension.control.attack;

import lych.soullery.util.EntityUtils;
import lych.soullery.util.Telepathy;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IBrainMixin;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ShootableItem;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DynamicTargetFinder<T extends MobEntity> implements TargetFinder<T> {
    public static final TargetFinder<MobEntity> DEFAULT = new DynamicTargetFinder<>(RangedTargetFinder.DEFAULT, new TelepathicTargetFinder(16, Math.PI / 6, Telepathy.DEFAULT_ANGLE_WEIGHT));
    @Nullable
    private final TargetFinder<? super T> primary;
    private final TargetFinder<? super T> forRanged;

    public DynamicTargetFinder(TargetFinder<? super T> forRanged) {
        this(null, forRanged);
    }

    public DynamicTargetFinder(@Nullable TargetFinder<? super T> primary, TargetFinder<? super T> forRanged) {
        this.primary = primary;
        this.forRanged = forRanged;
    }

    @Nullable
    @Override
    public LivingEntity findTarget(T operatingMob, ServerPlayerEntity player, CompoundNBT data) {
        if (operatingMob instanceof IRangedAttackMob) {
            Optional<Goal> goal = EntityUtils.findAnyRangedAttackableGoal(operatingMob);

            LivingEntity target = forRanged.findTarget(operatingMob, player, data);
            operatingMob.setTarget(target);
            LivingEntity finalTarget = null;

            if (goal.filter(Goal::canUse).isPresent() || ((IBrainMixin<?>) operatingMob.getBrain()).isValidBrain() && operatingMob.getMainHandItem().getItem() instanceof ShootableItem) {
                finalTarget = target;
            }

            operatingMob.setTarget(null);
            if (finalTarget == null) {
                finalTarget = Utils.applyIfNonnull(primary, f -> f.findTarget(operatingMob, player, data));
            }
            return finalTarget;
        }
        return Utils.applyIfNonnull(primary, f -> f.findTarget(operatingMob, player, data));
    }

    @Nullable
    @Override
    public TargetFinder<? super T> getPrimary() {
        return primary;
    }
}
