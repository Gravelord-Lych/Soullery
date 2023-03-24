package lych.soullery.world.event.ticker;

import lych.soullery.world.event.manager.NotLoadedException;
import lych.soullery.world.event.manager.UnknownObjectException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public class ContextedWorldTicker<T> extends WorldTicker {
    @Nullable
    private final T context;
    private final BiConsumer<? super T, ? super CompoundNBT> contextSaver;

    public ContextedWorldTicker(ContextedWorldTickerType<? super T> type, int id, ServerWorld level, BlockPos center, @Nullable T context) {
        super(type, id, level, center);
        this.context = context;
        this.contextSaver = type.getContextSaver();
    }

    protected ContextedWorldTicker(ContextedWorldTickerType<T> type, ServerWorld level, CompoundNBT compoundNBT) throws NotLoadedException {
        super(type, level, compoundNBT);
        this.contextSaver = type.getContextSaver();
        T context = type.getContextLoader().load(level, center, compoundNBT.getCompound("ContextSaveData"));
        if (context != null) {
            this.context = context;
        } else {
            throw new NotLoadedException("the context of the event is invalid");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        CompoundNBT contextSaveData = new CompoundNBT();
        contextSaver.accept(context, contextSaveData);
        compoundNBT.put("ContextSaveData", contextSaveData);
    }

    public Optional<T> getContext() {
        return Optional.ofNullable(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContextedWorldTickerType<T> getType() {
        return (ContextedWorldTickerType<T>) super.getType();
    }

    public interface ContextLoader<T> {
        @Nullable
        T load(ServerWorld world, BlockPos center, CompoundNBT nbt) throws UnknownObjectException;
    }
}
