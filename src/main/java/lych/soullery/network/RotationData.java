package lych.soullery.network;

import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.extension.control.MindOperatorSynchronizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RotationData {
    private final int mob;
    private final double amount;

    public RotationData(int mob, double scroll) {
        this.mob = mob;
        this.amount = scroll;
    }

    public RotationData(PacketBuffer buffer) {
        this(buffer.readVarInt(), buffer.readDouble());
    }

    public double getAmount() {
        return amount;
    }

    public MobEntity getMob(World world) {
        Entity entity = world.getEntity(mob);
        return entity instanceof MobEntity ? (MobEntity) entity : null;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeVarInt(mob);
        buf.writeDouble(amount);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();
        MobEntity mob = getMob(sender.getLevel());
        Controller<?> controller = MindOperatorSynchronizer.getActiveController(sender.getLevel(), mob);
        if (controller == null) {
            ctx.get().setPacketHandled(true);
            return;
        }
        if (!(controller instanceof MindOperator)) {
            throw new AssertionError();
        }
        ctx.get().enqueueWork(() -> MindOperatorSynchronizer.handleRotationOffsetS(mob, sender, (MindOperator) controller, getAmount()));
        ctx.get().setPacketHandled(true);
    }
}
