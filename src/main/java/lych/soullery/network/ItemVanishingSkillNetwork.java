package lych.soullery.network;

import it.unimi.dsi.fastutil.ints.IntSet;
import lych.soullery.Soullery;
import lych.soullery.util.mixin.IClientPlayerMixin;
import lych.soullery.world.ItemDestroyer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ItemVanishingSkillNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(Soullery.prefix("item_vanishing"), () -> VERSION, ItemVanishingSkillNetwork::isCorrectVersion, ItemVanishingSkillNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(Packet.class, nextID())
                .encoder(Packet::toBytes)
                .decoder(Packet::new)
                .consumer(Packet::handler)
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }

    public static final class Packet {
        private final int slotIndex;
        private final boolean add;

        public Packet(int slotIndex, boolean add) {
            ItemDestroyer.checkIndex(slotIndex);
            this.slotIndex = slotIndex;
            this.add = add;
        }

        public Packet(PacketBuffer buf) {
            this.slotIndex = buf.readVarInt();
            this.add = buf.readBoolean();
        }

        public void toBytes(PacketBuffer buf) {
            buf.writeVarInt(slotIndex);
            buf.writeBoolean(add);
        }

        public void handler(Supplier<NetworkEvent.Context> ctx) {
            IntSet itemVanishingSlots = ((IClientPlayerMixin) Minecraft.getInstance().player).getItemVanishingSlots();
            if (add) {
                itemVanishingSlots.add(slotIndex);
            } else {
                itemVanishingSlots.remove(slotIndex);
            }
            ctx.get().setPacketHandled(true);
        }
    }
}
