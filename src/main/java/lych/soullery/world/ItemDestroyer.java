package lych.soullery.world;

import com.google.common.base.Preconditions;
import lych.soullery.api.capability.APICapabilities;
import lych.soullery.network.ItemVanishingSkillNetwork;
import lych.soullery.network.ItemVanishingSkillNetwork.Packet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public final class ItemDestroyer {
    private ItemDestroyer() {}

    public static void tryVanish(ServerPlayerEntity player, int slotIndex, int time) {
        checkIndex(slotIndex);
        vanish(player, slotIndex, time);
        sync(player, slotIndex, true);
    }

    public static void sync(ServerPlayerEntity player, int slotIndex, boolean add) {
        ItemVanishingSkillNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new Packet(slotIndex, add));
    }

    private static void vanish(PlayerEntity player, int slotIndex, int time) {
        player.getCapability(APICapabilities.ITEM_VANISHING_SKILL).ifPresent(ivs -> ivs.vanishAt(slotIndex, time));
    }

    public static void checkIndex(int slotIndex) {
        Preconditions.checkArgument(PlayerInventory.isHotbarSlot(slotIndex), String.format("SlotIndex %s is not within [0, %s)", slotIndex, PlayerInventory.getSelectionSize()));
    }
}
