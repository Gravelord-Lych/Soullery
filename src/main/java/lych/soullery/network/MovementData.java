package lych.soullery.network;

import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.extension.control.MindOperatorSynchronizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MovementData {
    private final int mob;
    public final float leftImpulse;
    public final float forwardImpulse;
    public final boolean up;
    public final boolean down;
    public final boolean left;
    public final boolean right;
    public final boolean jumping;
    public final boolean shiftKeyDown;
    public final boolean autoJumpEnabled;

    public MovementData(int mob, boolean autoJumpEnabled, MovementInput input) {
        this(mob,
                input.leftImpulse,
                input.forwardImpulse,
                input.up,
                input.down,
                input.left,
                input.right,
                input.jumping,
                input.shiftKeyDown,
                autoJumpEnabled);
    }

    public MovementData(int mob, float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left, boolean right, boolean jumping, boolean shiftKeyDown, boolean autoJumpEnabled) {
        this.mob = mob;
        this.leftImpulse = leftImpulse;
        this.forwardImpulse = forwardImpulse;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.jumping = jumping;
        this.shiftKeyDown = shiftKeyDown;
        this.autoJumpEnabled = autoJumpEnabled;
    }

    public MovementData(PacketBuffer buf) {
        this(buf.readVarInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean());
    }

    @Nullable
    public MobEntity getMob(World world) {
        Entity entity = world.getEntity(mob);
        return entity instanceof MobEntity ? (MobEntity) entity : null;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeVarInt(mob);
        buf.writeFloat(leftImpulse);
        buf.writeFloat(forwardImpulse);
        buf.writeBoolean(up);
        buf.writeBoolean(down);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(jumping);
        buf.writeBoolean(shiftKeyDown);
        buf.writeBoolean(autoJumpEnabled);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();
        MobEntity mob = getMob(sender.getLevel());
        Controller<?> controller = MindOperatorSynchronizer.getActiveController(sender.getLevel(), mob);
        if (!(controller instanceof MindOperator)) {
            ctx.get().setPacketHandled(true);
            return;
        }
        ctx.get().enqueueWork(() -> MindOperatorSynchronizer.handleMovementS(mob, sender, (MindOperator) controller, this));
        ctx.get().setPacketHandled(true);
    }
}
