package lych.soullery.network;

import lych.soullery.Soullery;
import lych.soullery.item.AbstractWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Random;
import java.util.function.Supplier;

public class WandSoundNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(Soullery.prefix("wands"), () -> VERSION, WandSoundNetwork::isCorrectVersion, WandSoundNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(Integer.class, nextID())
                .encoder((i, buf) -> buf.writeVarInt(i))
                .decoder(PacketBuffer::readVarInt)
                .consumer(WandSoundNetwork::handle)
                .add();
    }

    @SuppressWarnings("deprecation")
    public static void handle(int id, Supplier<NetworkEvent.Context> ctx) {
        AbstractWandItem<?> item = (AbstractWandItem<?>) Registry.ITEM.byId(id);
        Random random = Minecraft.getInstance().level.random;
        Minecraft.getInstance().player.playSound(item.getSound(), item.getVolume(random), item.getPitch(random));
        ctx.get().setPacketHandled(true);
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }
}
