package lych.soullery.network;

import lych.soullery.Soullery;
import lych.soullery.listener.CommonEventListener;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;

public class ClickHandlerNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(Soullery.prefix("clicks"), () -> VERSION, ClickHandlerNetwork::isCorrectVersion, ClickHandlerNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(Type.class, nextID())
                .encoder((hand, buf) -> buf.writeEnum(hand))
                .decoder(buf -> buf.readEnum(Type.class))
                .consumer((key, ctx) -> {
                    CommonEventListener.handleEmptyClickServerside(Objects.requireNonNull(ctx.get().getSender(), "Packets that are sent from a client to the server must have a sender"), key);
                })
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }

    public enum Type {
        LEFT,
        RIGHT
    }
}
