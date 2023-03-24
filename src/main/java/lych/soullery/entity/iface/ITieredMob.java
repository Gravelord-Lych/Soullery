package lych.soullery.entity.iface;

import lych.soullery.config.ConfigHelper;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ITieredMob {
    int MIN_TIER = 1;
//  Maximum tier is 233 because bosses with too high tiers are almost impossible to kill.
//  Also prevents unexpected problems.
    int MAX_TIER = 233;

    int getTier();

    void setTier(int tier);

    Set<UUID> getKillerUUIDSet();

    default boolean reachedTier(int tier) {
        return getTier() >= tier;
    }

    default Set<PlayerEntity> getKillers() {
        return getKillerUUIDSet().stream().map(uuid -> ((Entity) this).level.getPlayerByUUID(uuid)).collect(Collectors.toSet());
    }

    default void setTierAccordingToPlayers() {
        if (ConfigHelper.isBossesTiered()) {
            int expectedTier = getTierOf(((Entity) this).level, ((Entity) this).getType());
            setTier(expectedTier);
        } else {
            setTier(1);
        }
    }

    default void handleHurt(DamageSource source) {
        if (source.getEntity() instanceof PlayerEntity) {
            getKillerUUIDSet().add(source.getEntity().getUUID());
        }
    }

    default void handleDeath(DamageSource source) {
        getKillers().stream().filter(player -> ((IPlayerEntityMixin) player).getTier(((Entity) this).getType()) <= getTier()).forEach(player -> ((IPlayerEntityMixin) player).upgrade(((Entity) this).getType()));
    }

    default void saveKillers(CompoundNBT compoundNBT) {
        ListNBT listNBT = new ListNBT();
        for (UUID uuid : getKillerUUIDSet()) {
            CompoundNBT playerNBT = new CompoundNBT();
            playerNBT.putUUID("KillerUUID", uuid);
            listNBT.add(playerNBT);
        }
        compoundNBT.put("Killers", listNBT);
    }

    default void loadKillers(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("Killers", Constants.NBT.TAG_LIST)) {
            getKillerUUIDSet().clear();
            ListNBT listNBT = compoundNBT.getList("Killers", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundNBT playerNBT = listNBT.getCompound(i);
                UUID killerUUID = playerNBT.getUUID("KillerUUID");
                getKillerUUIDSet().add(killerUUID);
            }
        }
    }

    static int getTierOf(World world, EntityType<?> type) {
        return MathHelper.clamp(world.players().stream().mapToInt(player -> ((IPlayerEntityMixin) player).getTier(type)).max().orElse(MIN_TIER), MIN_TIER, MAX_TIER);
    }
}
