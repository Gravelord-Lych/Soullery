package lych.soullery.world.event.ticker;

@FunctionalInterface
public interface IWorldTicker {
    void tick(WorldTicker ticker, int timeRemaining);
}
