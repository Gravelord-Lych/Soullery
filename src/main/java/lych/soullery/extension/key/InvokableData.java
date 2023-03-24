package lych.soullery.extension.key;

import com.google.common.base.MoreObjects;
import net.minecraft.client.settings.KeyBinding;

import java.util.Objects;
import java.util.UUID;

public class InvokableData {
    private final UUID uuid;
    private final KeyBinding key;

    public InvokableData(UUID uuid, KeyBinding key) {
        Objects.requireNonNull(uuid, "UUID should be non-null");
        this.uuid = uuid;
        this.key = key;
    }

    public UUID getUUID() {
        return uuid;
    }

    public KeyBinding getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvokableData that = (InvokableData) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .add("key", key)
                .toString();
    }
}
