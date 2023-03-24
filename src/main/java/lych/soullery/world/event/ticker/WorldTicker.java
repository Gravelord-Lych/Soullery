package lych.soullery.world.event.ticker;

import lych.soullery.world.event.AbstractWorldEvent;
import lych.soullery.world.event.manager.NotLoadedException;
import lych.soullery.world.event.manager.UnknownObjectException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class WorldTicker extends AbstractWorldEvent {
    private final WorldTickerType type;
    private boolean firstTick;
    private int timeRemaining;

    public WorldTicker(WorldTickerType type, int id, ServerWorld level, BlockPos center) {
        super(id, level, center);
        this.type = type;
        timeRemaining = getTickDuration();
        firstTick = true;
    }

    protected WorldTicker(WorldTickerType type, ServerWorld level, CompoundNBT compoundNBT) {
        super(level, compoundNBT);
        this.type = type;
        timeRemaining = compoundNBT.getInt("TimeRemaining");
        firstTick = compoundNBT.getBoolean("FirstTick");
    }

    public static WorldTicker load(ServerWorld level, CompoundNBT compoundNBT) throws NotLoadedException {
        ResourceLocation name = new ResourceLocation(compoundNBT.getString("WorldTickerType"));
        WorldTickerType type = WorldTickerType.getOptional(name).orElseThrow(() -> new UnknownObjectException(name));
        if (type instanceof ContextedWorldTickerType<?>) {
            return new ContextedWorldTicker<>((ContextedWorldTickerType<?>) type, level, compoundNBT);
        }
        return new WorldTicker(type, level, compoundNBT);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putString("WorldTickerType", type.getRegistryName().toString());
        compoundNBT.putInt("TimeRemaining", timeRemaining);
        compoundNBT.putBoolean("FirstTick", firstTick);
    }

    @Override
    protected void eventTick() {
        if (timeRemaining > 0) {
            mainTick();
            timeRemaining--;
            if (firstTick) {
                firstTick = false;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void update() {
        active = level.hasChunkAt(center) && !level.players().isEmpty();
    }

    protected void mainTick() {
        type.tick(this, timeRemaining);
    }

    private int getTickDuration() {
        return type.getDuration(this);
    }

    public boolean isFirstTick() {
        return firstTick;
    }

    public boolean isLastTick() {
        return timeRemaining == 1;
    }

    @Override
    public void stop() {}

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return timeRemaining <= 0;
    }

    @Override
    public boolean isOver() {
        return false;
    }

    public Random getRandom() {
        return random;
    }

    public WorldTickerType getType() {
        return type;
    }
}
