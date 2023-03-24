package lych.soullery.world.event.manager;

import lych.soullery.world.event.ticker.ContextedWorldTicker;
import lych.soullery.world.event.ticker.ContextedWorldTickerType;
import lych.soullery.world.event.ticker.WorldTicker;
import lych.soullery.world.event.ticker.WorldTickerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class WorldTickerManager extends EventManager<WorldTicker> {
    private static final String NAME = "WorldTickerManager";

    public WorldTickerManager(ServerWorld level) {
        super(NAME, level);
    }

    @Override
    protected WorldTicker loadEvent(ServerWorld world, CompoundNBT compoundNBT) throws NotLoadedException {
        return WorldTicker.load(world, compoundNBT);
    }

    public static void start(WorldTickerType type, ServerWorld world, BlockPos center) {
        get(world).startTicking(type, world, center);
    }

    public static <T> void startWithContext(ContextedWorldTickerType<? super T> type, ServerWorld world, BlockPos center, @Nullable T context) {
        get(world).startTickingWithContext(type, world, center, context);
    }

    public static WorldTickerManager get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(() -> new WorldTickerManager(world), NAME);
    }

    private void startTicking(WorldTickerType type, ServerWorld world, BlockPos center) {
        WorldTicker ticker = new WorldTicker(type, getUniqueID(), world, center);
        eventMap.putIfAbsent(ticker.getId(), ticker);
        setDirty();
    }

    private <T> void startTickingWithContext(ContextedWorldTickerType<? super T> type, ServerWorld world, BlockPos center, @Nullable T context) {
        ContextedWorldTicker<T> ticker = new ContextedWorldTicker<>(type, getUniqueID(), world, center, context);
        eventMap.putIfAbsent(ticker.getId(), ticker);
        setDirty();
    }
}
