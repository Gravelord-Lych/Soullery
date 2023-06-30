package lych.soullery.util;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Counter extends ForwardingMap<UUID, Integer> implements INBTSerializable<ListNBT> {
    private final Map<UUID, Integer> map = new HashMap<>();

    @Override
    protected Map<UUID, Integer> delegate() {
        return map;
    }

    public int getAndAdd(UUID uuid) {
        return getAndAdd(uuid, 1);
    }

    public int getAndAdd(UUID uuid, int count) {
        return put(uuid, getCount(uuid) + count);
    }

    public int addAndGet(UUID uuid) {
        return addAndGet(uuid, 1);
    }

    public int addAndGet(UUID uuid, int count) {
        int oldCount = getCount(uuid);
        int newCount = oldCount + count;
        put(uuid, newCount);
        return newCount;
    }

    public int getCount(@Nullable UUID uuid) {
        Integer i = get(uuid);
        return i == null ? 0 : i;
    }

    public ImmutableSet<Count> getCounts() {
        return entrySet().stream().map(entry -> new Count(entry.getKey(), entry.getValue())).collect(ImmutableSet.toImmutableSet());
    }

    @Deprecated
    @Nullable
    @Override
    public Integer get(@Nullable Object key) {
        return super.get(key);
    }

    @Override
    public ListNBT serializeNBT() {
        ListNBT listNBT = new ListNBT();
        for (Map.Entry<UUID, Integer> entry : entrySet()) {
            CompoundNBT entryNBT = new CompoundNBT();
            entryNBT.putUUID("UUID", entry.getKey());
            entryNBT.putInt("Count", entry.getValue());
            listNBT.add(entryNBT);
        }
        return listNBT;
    }

    @Override
    public void deserializeNBT(ListNBT nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            CompoundNBT entryNBT = nbt.getCompound(i);
            UUID uuid = entryNBT.getUUID("UUID");
            int count = entryNBT.getInt("Count");
            put(uuid, count);
        }
    }

    public static final class Count {
        private final UUID key;
        private final int count;

        Count(UUID key, int count) {
            this.key = key;
            this.count = count;
        }

        public UUID getKey() {
            return key;
        }

        public int getCount() {
            return count;
        }
    }
}