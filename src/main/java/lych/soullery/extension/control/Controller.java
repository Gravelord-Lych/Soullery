package lych.soullery.extension.control;

import com.google.common.base.MoreObjects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class Controller<T extends MobEntity> {
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
    protected PlayerEntity getPlayer() {
        return level.getPlayerByUUID(player);
    }

    protected SoulManager getSoulManager() {
        return SoulManager.get(level);
    }

    public boolean tick() {
        return true;
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
        return 0;
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
}
