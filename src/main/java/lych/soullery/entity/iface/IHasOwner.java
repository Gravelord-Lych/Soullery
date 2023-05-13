package lych.soullery.entity.iface;

import lych.soullery.Soullery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public interface IHasOwner<T extends LivingEntity> {
    @Nullable
    UUID getOwnerUUID();

    void setOwnerUUID(@Nullable UUID ownerUUID);

    @SuppressWarnings("unchecked")
    @Nullable
    default T getOwner() {
        if (getOwnerUUID() == null) {
            return null;
        }

        World world = getAsEntity().level;
        Entity possibleOwner = null;

        if (world instanceof ServerWorld) {
            possibleOwner = (((ServerWorld) world).getEntity(getOwnerUUID()));
            if (possibleOwner == null) {
                return null;
            }
        }

        T owner = null;
        if (possibleOwner != null) {
            try {
                owner = (T) possibleOwner;
            } catch (ClassCastException e) {
                Soullery.LOGGER.debug("PossibleOwner's type does not match, he/she/it is not the real owner");
            }
        }

        return owner;
    }

    default void setOwner(@Nullable T owner) {
        if (owner != null) {
            setOwnerUUID(owner.getUUID());
        } else {
            setOwnerUUID(null);
        }
    }

    default void saveOwner(CompoundNBT compoundNBT) {
        if (getOwnerUUID() != null) {
            compoundNBT.putUUID("Owner", getOwnerUUID());
        }
    }

    default void loadOwner(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("Owner", Constants.NBT.TAG_INT_ARRAY)) {
            setOwnerUUID(compoundNBT.getUUID("Owner"));
        }
    }

    default boolean isOwnedBy(@Nullable LivingEntity entity) {
        return Objects.equals(entity, getOwner());
    }

    default boolean shouldSetDirectDamageToOwner() {
        return false;
    }

    default boolean shouldSetIndirectDamageToOwner() {
        return false;
    }

    default Entity getAsEntity() {
        return (Entity) this;
    }

    default boolean isOwnerInTheSameWorld() {
        return getOwner() != null;
    }
}
