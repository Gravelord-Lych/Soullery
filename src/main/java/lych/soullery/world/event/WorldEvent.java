package lych.soullery.world.event;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public interface WorldEvent {
    CompoundNBT save();

    void tick();

    void stop();

    boolean isActive();

    boolean isStarted();

    boolean isStopped();

    boolean isOver();

    int getId();

    BlockPos getCenter();

    ServerWorld getLevel();
}
