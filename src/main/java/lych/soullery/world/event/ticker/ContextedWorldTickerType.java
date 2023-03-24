package lych.soullery.world.event.ticker;

import lych.soullery.world.event.ticker.ContextedWorldTicker.ContextLoader;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

public class ContextedWorldTickerType<T> extends WorldTickerType {
    private final BiConsumer<? super T, ? super CompoundNBT> contextSaver;
    private final ContextLoader<? extends T> contextLoader;

    public ContextedWorldTickerType(ResourceLocation registryName, IContextedWorldTicker<? super T> ticker, BiConsumer<? super T, ? super CompoundNBT> contextSaver, ContextLoader<? extends T> contextLoader) {
        super(registryName, ticker);
        this.contextSaver = contextSaver;
        this.contextLoader = contextLoader;
    }

    public ContextedWorldTickerType(ResourceLocation registryName, int duration, IContextedWorldTicker<? super T> ticker, BiConsumer<? super T, ? super CompoundNBT> contextSaver, ContextLoader<? extends T> contextLoader) {
        super(registryName, duration, ticker);
        this.contextSaver = contextSaver;
        this.contextLoader = contextLoader;
    }

    public ContextedWorldTickerType(ResourceLocation registryName, ToIntFunction<? super WorldTicker> durationFunction, IContextedWorldTicker<? super T> ticker, BiConsumer<? super T, ? super CompoundNBT> contextSaver, ContextLoader<? extends T> contextLoader) {
        super(registryName, durationFunction, ticker);
        this.contextSaver = contextSaver;
        this.contextLoader = contextLoader;
    }

    public static <T> ContextedWorldTickerType<T> createAndRegisterContexted(ResourceLocation registryName, IContextedWorldTicker<? super T> ticker, BiConsumer<? super T, ? super CompoundNBT> contextSaver, ContextLoader<? extends T> contextLoader) {
        ContextedWorldTickerType<T> type = new ContextedWorldTickerType<>(registryName, ticker, contextSaver, contextLoader);
        registerTicker(type);
        return type;
    }

    public static <T> ContextedWorldTickerType<T> createAndRegisterContexted(ResourceLocation registryName, int duration, IContextedWorldTicker<? super T> ticker, BiConsumer<? super T, ? super CompoundNBT> contextSaver, ContextLoader<? extends T> contextLoader) {
        ContextedWorldTickerType<T> type = new ContextedWorldTickerType<>(registryName, duration, ticker, contextSaver, contextLoader);
        registerTicker(type);
        return type;
    }

    public static <T> ContextedWorldTickerType<T> createAndRegisterContexted(ResourceLocation registryName, ToIntFunction<? super WorldTicker> durationFunction, IContextedWorldTicker<? super T> ticker, BiConsumer<? super T, ? super CompoundNBT> contextSaver, ContextLoader<? extends T> contextLoader) {
        ContextedWorldTickerType<T> type = new ContextedWorldTickerType<>(registryName, durationFunction, ticker, contextSaver, contextLoader);
        registerTicker(type);
        return type;
    }

    public BiConsumer<? super T, ? super CompoundNBT> getContextSaver() {
        return contextSaver;
    }

    public ContextLoader<? extends T> getContextLoader() {
        return contextLoader;
    }
}
