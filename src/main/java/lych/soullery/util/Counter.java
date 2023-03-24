package lych.soullery.util;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Counter<T, N extends INBT> implements INBTSerializable<ListNBT> {
    private final Object2IntMap<T> counts = new Object2IntOpenHashMap<>();
    private final Function<? super T, ? extends N> serializer;
    private final Function<? super N, ? extends T> deserializer;

    public Counter(Function<? super T, ? extends N> serializer, Function<? super N, ? extends T> deserializer) {
        this(0, serializer, deserializer);
    }

    public Counter(int defaultCount, Function<? super T, ? extends N> serializer, Function<? super N, ? extends T> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        counts.defaultReturnValue(defaultCount);
    }

    public void add(T t) {
        add(t, 1);
    }

    public void add(T t, int value) {
        counts.put(t, counts.getInt(t) + value);
    }

    public int get(T t) {
        return counts.getInt(t);
    }

    public void set(T t, int newCount) {
        counts.put(t, newCount);
    }

    @Override
    public ListNBT serializeNBT() {
        ListNBT listNBT = new ListNBT();
        for (Object2IntMap.Entry<T> entry : counts.object2IntEntrySet()) {
            CompoundNBT entryNBT = new CompoundNBT();
            entryNBT.put("CounterKey", serializer.apply(entry.getKey()));
            entryNBT.putInt("Count", entry.getIntValue());
        }
        return listNBT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeNBT(ListNBT nbt) {
        counts.clear();
        for (int i = 0; i < nbt.size(); i++) {
            CompoundNBT compoundNBT = nbt.getCompound(i);
            T key = deserializer.apply((N) compoundNBT.get("CounterKey"));
            int count = compoundNBT.getInt("Count");
            counts.put(key, count);
        }
    }
}
