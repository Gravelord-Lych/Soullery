package lych.soullery.network;

import com.google.common.base.MoreObjects;
import lych.soullery.Soullery;
import lych.soullery.client.LaserRenderingManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.awt.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class LaserNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(Soullery.prefix("laser"), () -> VERSION, LaserNetwork::isCorrectVersion, LaserNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(LaserPacket.class, nextID())
                .encoder(LaserPacket::toBytes)
                .decoder(LaserPacket::new)
                .consumer(LaserPacket::handler)
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }

    public static class LaserPacket {
        private final LaserRenderData data;

        public LaserPacket(PacketBuffer buffer) {
            double sx = buffer.readDouble();
            double sy = buffer.readDouble();
            double sz = buffer.readDouble();
            double dx = buffer.readDouble();
            double dy = buffer.readDouble();
            double dz = buffer.readDouble();
            int r = buffer.readInt();
            int g = buffer.readInt();
            int b = buffer.readInt();
            int owner = buffer.readInt();
            int tickCount = buffer.readInt();
            data = new LaserRenderData(new Vector3d(sx, sy, sz), new Vector3d(dx, dy, dz), new Color(r, g, b), owner, tickCount);
        }

        public LaserPacket(LaserRenderData data) {
            this.data = data;
        }

        public void toBytes(PacketBuffer buf) {
            buf.writeDouble(data.getSrc().x);
            buf.writeDouble(data.getSrc().y);
            buf.writeDouble(data.getSrc().z);
            buf.writeDouble(data.getDest().x);
            buf.writeDouble(data.getDest().y);
            buf.writeDouble(data.getDest().z);
            buf.writeInt(data.getColor().getRed());
            buf.writeInt(data.getColor().getGreen());
            buf.writeInt(data.getColor().getBlue());
            buf.writeInt(data.getOwner());
            buf.writeInt(data.getRenderTickCount());
        }

        public void handler(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> LaserRenderingManager.getInstance().addLaser(data));
            ctx.get().setPacketHandled(true);
        }
    }

    public static class LaserRenderData {
        private static final AtomicLong ID_COUNTER = new AtomicLong();
        private final long id;
        private final Color color;
        private final Vector3d src;
        private final Vector3d dest;
        private final int owner;
        private final int renderTickCount;

        public LaserRenderData(Vector3d src, Vector3d dest, Color color, int owner, int renderTickCount) {
            this.src = src;
            this.dest = dest;
            this.color = color;
            this.owner = owner;
            this.renderTickCount = renderTickCount;
            id = ID_COUNTER.incrementAndGet();
        }

        public Vector3d getSrc() {
            return src;
        }

        public Vector3d getDest() {
            return dest;
        }

        public Color getColor() {
            return color;
        }

        public int getRenderTickCount() {
            return renderTickCount;
        }

        public int getOwner() {
            return owner;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("src", src)
                    .add("dest", dest)
                    .add("color", color)
                    .add("owner", owner)
                    .add("renderTickCount", renderTickCount)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LaserRenderData data = (LaserRenderData) o;
            return getId() == data.getId();
        }

        @Override
        public int hashCode() {
            return Long.hashCode(getId());
        }

        public long getId() {
            return id;
        }
    }
}
