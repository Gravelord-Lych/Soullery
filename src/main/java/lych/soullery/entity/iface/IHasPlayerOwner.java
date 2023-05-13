package lych.soullery.entity.iface;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IHasPlayerOwner extends IHasOwner<PlayerEntity> {
    @Override
    @Nullable
    default PlayerEntity getOwner() {
        if (getOwnerUUID() == null) {
            return null;
        }
        World world = getAsEntity().level;
        if (world.isClientSide()) {
            return null;
        }
        return world.getServer().getPlayerList().getPlayer(getOwnerUUID());
    }

    @Override
    default boolean isOwnerInTheSameWorld() {
        if (getOwner() == null) {
            return false;
        }
        return getOwner().level == getAsEntity().level;
    }

    @Override
    default void loadOwner(CompoundNBT compoundNBT) {
        UUID uuid;
        if (compoundNBT.hasUUID("Owner")) {
            uuid = compoundNBT.getUUID("Owner");
        } else {
            String s = compoundNBT.getString("Owner");
            uuid = PreYggdrasilConverter.convertMobOwnerIfNecessary(getAsEntity().getServer(), s);
        }
        setOwnerUUID(uuid);
    }
}
