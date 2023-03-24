package lych.soullery.world.event.ticker;

@FunctionalInterface
public interface IContextedWorldTicker<T> extends IWorldTicker {
    @SuppressWarnings("unchecked")
    @Override
    default void tick(WorldTicker ticker, int timeRemaining) {
        try {
            tickWithContext((ContextedWorldTicker<? extends T>) ticker, timeRemaining);
        } catch (ClassCastException e) {
            throw new IllegalStateException("No context found");
        }
    }

    void tickWithContext(ContextedWorldTicker<? extends T> ticker, int timeRemaining);
}
