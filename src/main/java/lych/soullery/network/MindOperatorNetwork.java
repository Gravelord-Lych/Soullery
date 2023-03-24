package lych.soullery.network;

import lych.soullery.Soullery;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MindOperatorNetwork {
    public static SimpleChannel MOVEMENTS;
    public static SimpleChannel ROTATIONS;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        MOVEMENTS = NetworkRegistry.newSimpleChannel(Soullery.prefix("mind_operator_movements"), () -> VERSION, MindOperatorNetwork::isCorrectVersion, MindOperatorNetwork::isCorrectVersion);
        MOVEMENTS.messageBuilder(MovementData.class, nextID())
                .encoder(MovementData::toBytes)
                .decoder(MovementData::new)
                .consumer(MovementData::handler)
                .add();

        ROTATIONS = NetworkRegistry.newSimpleChannel(Soullery.prefix("mind_operator_rotations"), () -> VERSION, MindOperatorNetwork::isCorrectVersion, MindOperatorNetwork::isCorrectVersion);
        ROTATIONS.messageBuilder(RotationData.class, nextID())
                .encoder(RotationData::toBytes)
                .decoder(RotationData::new)
                .consumer(RotationData::handler)
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }
}
