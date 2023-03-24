package lych.soullery.network;

import lych.soullery.Soullery;
import lych.soullery.extension.key.InvokableManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class InvokableNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(Soullery.prefix("invokable"), () -> VERSION, InvokableNetwork::isCorrectVersion, InvokableNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(KeyPacket.class, nextID())
                .encoder(KeyPacket::toBytes)
                .decoder(KeyPacket::new)
                .consumer(KeyPacket::handler)
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }

    public static class KeyPacket {
        private final UUID invokableUUID;

        public KeyPacket(PacketBuffer buffer) {
            invokableUUID = buffer.readUUID();
        }

        public KeyPacket(UUID invokableUUID) {
            this.invokableUUID = invokableUUID;
        }

        public void toBytes(PacketBuffer buf) {
            buf.writeUUID(invokableUUID);
        }

        public void handler(Supplier<NetworkEvent.Context> ctx) {
            ServerPlayerEntity sender = Objects.requireNonNull(ctx.get().getSender(), "Packets that are sent from a client to the server must have a sender");
            ctx.get().enqueueWork(() -> InvokableManager
                    .get(invokableUUID)
                    .onKeyPressed(sender));
            ctx.get().setPacketHandled(true);
        }
    }
}
