package lych.soullery.world.event;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public abstract class AbstractWorldEvent implements WorldEvent {
    private static final String CENTER_X = "CX";
    private static final String CENTER_Y = "CY";
    private static final String CENTER_Z = "CZ";
    private static final String ID = "Id";

    protected final BlockPos center;
    protected final ServerWorld level;
    protected final Random random = new Random();
    private final int id;
    private boolean dirty;
    protected boolean active;
    protected long ticksActive;

    protected AbstractWorldEvent(int id, ServerWorld level, BlockPos center) {
        this.id = id;
        this.center = center;
        this.level = level;
    }

    protected AbstractWorldEvent(ServerWorld level, CompoundNBT compoundNBT) {
        this.id = compoundNBT.getInt(ID);
        this.center = new BlockPos(compoundNBT.getInt(CENTER_X), compoundNBT.getInt(CENTER_Y), compoundNBT.getInt(CENTER_Z));
        this.level = level;
    }

    @Override
    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt(ID, id);
        compoundNBT.putInt(CENTER_X, center.getX());
        compoundNBT.putInt(CENTER_Y, center.getY());
        compoundNBT.putInt(CENTER_Z, center.getZ());
        addAdditionalSaveData(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void tick() {
        if (!isStopped()) {
            if (shouldStopIfPeaceful() && level.getDifficulty() == Difficulty.PEACEFUL) {
                stop();
                return;
            }
            update();
            if (!active) {
                return;
            }
            if (isOver()) {
                postEventTick();
            } else {
                ticksActive++;
                eventTick();
            }
        }
    }

    protected void eventTick() {}

    protected void postEventTick() {}

    @SuppressWarnings("deprecation")
    protected void update() {
        active = level.hasChunkAt(center);
    }

    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

    @Override
    public boolean isActive() {
        return active;
    }

    protected boolean shouldStopIfPeaceful() {
        return true;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public BlockPos getCenter() {
        return center;
    }

    @Override
    public ServerWorld getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractWorldEvent that = (AbstractWorldEvent) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setDirty() {
        setDirty(true);
    }
}
