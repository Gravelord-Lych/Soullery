package lych.soullery.world.event.ticker;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.ToIntFunction;

import static lych.soullery.world.event.ticker.WorldTickers.TICKERS;

public class WorldTickerType {
    @NotNull
    private final ResourceLocation registryName;
    private final ToIntFunction<? super WorldTicker> durationFunction;
    private final IWorldTicker worldTicker;

    public WorldTickerType(ResourceLocation registryName, IWorldTicker ticker) {
        this(registryName, 100, ticker);
    }

    public WorldTickerType(ResourceLocation registryName, int duration, IWorldTicker ticker) {
        this(registryName, tickerIn -> duration, ticker);
    }

    public WorldTickerType(ResourceLocation registryName, ToIntFunction<? super WorldTicker> durationFunction, IWorldTicker ticker) {
        this.registryName = registryName;
        this.durationFunction = durationFunction;
        this.worldTicker = ticker;
    }

    public static WorldTickerType createAndRegister(ResourceLocation registryName, IWorldTicker ticker) {
        WorldTickerType type = new WorldTickerType(registryName, ticker);
        registerTicker(type);
        return type;
    }

    public static WorldTickerType createAndRegister(ResourceLocation registryName, int duration, IWorldTicker ticker) {
        WorldTickerType type = new WorldTickerType(registryName, duration, ticker);
        registerTicker(type);
        return type;
    }

    public static WorldTickerType createAndRegister(ResourceLocation registryName, ToIntFunction<? super WorldTicker> durationFunction, IWorldTicker ticker) {
        WorldTickerType type = new WorldTickerType(registryName, durationFunction, ticker);
        registerTicker(type);
        return type;
    }

    public void tick(WorldTicker ticker, int timeRemaining) {
        worldTicker.tick(ticker, timeRemaining);
    }

    public static ImmutableSet<WorldTickerType> getWorldTickers() {
        return ImmutableSet.copyOf(TICKERS);
    }

    public static void registerTicker(WorldTickerType type) {
        Preconditions.checkState(TICKERS.add(type), "Duplicate WorldTickerType: " + type.getRegistryName());
    }

    public static Optional<WorldTickerType> getOptional(ResourceLocation registryName) {
        for (WorldTickerType type : getWorldTickers()) {
            if (type.getRegistryName().equals(registryName)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

//  TODO
    public static void onRegisterTickers() {}

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public int getDuration(WorldTicker ticker) {
        return durationFunction.applyAsInt(ticker);
    }
}
