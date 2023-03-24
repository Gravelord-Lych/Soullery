package lych.soullery.extension.laser;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class LaserHitType<T> {
//  Used when laser hits air (nothing).
    public static final LaserHitType<Object> AIR = new LaserHitType<>();
    public static final LaserHitType<BlockState> BLOCK = new LaserHitType<>();
    public static final LaserHitType<LivingEntity> ENTITY = new LaserHitType<>(true);
    public static final LaserHitType<BlockState> FLUID = new LaserHitType<>();

    private final UUID uuid;
    final boolean addToLaserAttackResult;

    public LaserHitType() {
        this(false);
    }

    public LaserHitType(boolean addToLaserAttackResult) {
        this(UUID.randomUUID(), addToLaserAttackResult);
    }

    public LaserHitType(UUID uuid, boolean addToLaserAttackResult) {
        this.uuid = uuid;
        this.addToLaserAttackResult = addToLaserAttackResult;
    }

    /**
     * Similar to <code>obj instanceof T</code>.
     * @return True if the object is an instance of type parameter <code>T</code>
     */
    @SuppressWarnings({"unchecked", "unused"})
    public boolean matches(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            T t = (T) obj;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("LaserHitType(UUID: %s)", uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaserHitType<?> that = (LaserHitType<?>) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
