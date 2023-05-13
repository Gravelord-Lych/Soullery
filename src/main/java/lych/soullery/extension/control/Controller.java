package lych.soullery.extension.control;

import com.google.common.base.MoreObjects;
import lych.soullery.Soullery;
import lych.soullery.util.mixin.IGoalSelectorMixin;
import lych.soullery.util.mixin.INearestAttackableTargetGoalMixin;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class Controller<T extends MobEntity> implements Comparable<Controller<?>> {
    protected static final int REMOVE_DISTANCE = 40;
    protected static final int WARN_DISTANCE = 30;
    private final ControllerType<T> type;
    private final UUID mob;
    private final UUID player;
    protected final ServerWorld level;
    private boolean preparing;

    public Controller(ControllerType<T> type, UUID mob, UUID player, ServerWorld level) {
        this.type = type;
        this.mob = mob;
        this.player = player;
        this.level = level;
    }

    public Controller(ControllerType<T> type, CompoundNBT compoundNBT, ServerWorld level) {
        this.type = type;
        this.mob = compoundNBT.getUUID("Mob");
        this.player = compoundNBT.getUUID("Player");
        this.preparing = compoundNBT.getBoolean("Preparing");
        this.level = level;
    }

    @Nullable
    protected NearestAttackableTargetGoal<?> findTargetGoalTowards(Class<?> type, GoalSelector targetSelector) {
        return ((IGoalSelectorMixin) targetSelector).getAvailableGoals().stream()
                .map(PrioritizedGoal::getGoal)
                .filter(goal -> goal instanceof NearestAttackableTargetGoal)
                .map(goal -> (NearestAttackableTargetGoal<?>) goal)
                .filter(goal -> type.isAssignableFrom(((INearestAttackableTargetGoalMixin<?>) goal).getTargetType()))
                .findFirst()
                .orElse(null);
    }

    public UUID getMobUUID() {
        return mob;
    }

    public UUID getPlayerUUID() {
        return player;
    }

    @SuppressWarnings("unchecked")
    public Optional<T> getMob() {
        Entity entity = level.getEntity(getMobUUID());
        try {
            return Optional.ofNullable((T) entity);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    @Nullable
    public PlayerEntity getPlayer() {
        return level.getPlayerByUUID(player);
    }

    public boolean tick() {
        Optional<T> optional = getMob();
        ServerPlayerEntity player = (ServerPlayerEntity) getPlayer();
        if (optional.isPresent() && player != null) {
            T mob = optional.get();
            if (isNotLoaded(mob, player)) {
                handleNotLoaded(mob, player);
                return false;
            } else if (shouldWarnNotLoaded(mob, player)) {
                warnNotLoaded(mob, player);
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    protected boolean isNotLoaded(T mob, ServerPlayerEntity player) {
        if (!level.hasChunkAt(mob.blockPosition())) {
            return true;
        } else if (noDistanceRestrictions()) {
            return false;
        }
        double removeDistance = getRemoveDistance(mob, player);
        return mob.distanceToSqr(player) >= removeDistance * removeDistance;
    }

    protected boolean shouldWarnNotLoaded(T mob, ServerPlayerEntity player) {
        if (noDistanceRestrictions()) {
            return false;
        }
        double warnDistance = getWarnDistance(mob, player);
        return mob.distanceToSqr(player) >= warnDistance * warnDistance;
    }

    protected int getRemoveDistance(T mob, ServerPlayerEntity player) {
        return REMOVE_DISTANCE;
    }

    protected int getWarnDistance(T mob, ServerPlayerEntity player) {
        return WARN_DISTANCE;
    }

    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putUUID("Mob", getMobUUID());
        compoundNBT.putUUID("Player", player);
        compoundNBT.putBoolean("Preparing", preparing);
        return compoundNBT;
    }

    public ControllerType<T> getType() {
        return type;
    }

    public ResourceLocation getRegistryName() {
        return getType().getRegistryName();
    }

    public boolean isPreparing() {
        return preparing;
    }

    public void setPreparing(boolean preparing) {
        this.preparing = preparing;
    }

    public abstract void startControlling(MobEntity mob, GoalSelector goalSelector, GoalSelector targetSelector);

    public void stopControlling(MobEntity mob, boolean mobAlive) {}

    public boolean overrideBehaviorGoals() {
        return false;
    }

    public boolean overrideTargetGoals() {
        return true;
    }

    public int getPriority() {
        return 1000;
    }

    public float[] getColor() {
        return getType().getColor();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("mob", mob)
                .add("player", player)
                .add("level", level)
                .add("preparing", preparing)
                .toString();
    }

    @SuppressWarnings("unchecked")
    public final void handleDeathRaw(MobEntity mob, PlayerEntity player) {
        try {
            handleDeath((T) mob, (ServerPlayerEntity) player);
        } catch (ClassCastException e) {
            Soullery.LOGGER.warn(SoulManager.MARKER, "Death not handled for controlled mob", e);
        }
    }

    protected void handleDeath(T mob, ServerPlayerEntity player) {}

    protected void handleDepleted(ServerPlayerEntity player) {}

    protected void handleNotLoaded(T mob, ServerPlayerEntity player) {}

    protected void warnNotLoaded(T mob, ServerPlayerEntity player) {}

    protected boolean noDistanceRestrictions() {
        return true;
    }

    public boolean shouldDisableBrain() {
        return true;
    }

    public boolean shouldDisableTargetTasksAdditionally() {
        return false;
    }

    @Override
    public int compareTo(@NotNull Controller<?> o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
