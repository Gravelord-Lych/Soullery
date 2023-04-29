package lych.soullery.network;

import lych.soullery.Soullery;
import lych.soullery.client.particle.ModParticles;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class SoulDragonNetwork {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(Soullery.prefix("soul_dragon"), () -> VERSION, SoulDragonNetwork::isCorrectVersion, SoulDragonNetwork::isCorrectVersion);
        INSTANCE.messageBuilder(Message.class, nextID())
                .encoder(Message::toBytes)
                .decoder(Message::new)
                .consumer(Message::handle)
                .add();
    }

    private static boolean isCorrectVersion(String version) {
        return version.equals(VERSION);
    }

    public enum MessageType {
        SHOW_SOULBALL_HIT_PARTICLE {
            @Override
            public void handle(Supplier<NetworkEvent.Context> ctx, Message message) {
                ClientWorld level = Minecraft.getInstance().level;
                BlockPos pos = message.pos;
                Random random = level.random;
                for (int i = 0; i < 200; i++) {
                    float power = random.nextFloat() * 4;
                    float angle = random.nextFloat() * ((float) Math.PI * 2);
                    double xd = MathHelper.cos(angle) * power;
                    double yd = 0.01 + random.nextDouble() * 0.5;
                    double zd = MathHelper.sin(angle) * power;
                    level.addParticle(message.pure ? ModParticles.SOUL_DRAGON_BREATH_PURE : ModParticles.SOUL_DRAGON_BREATH, pos.getX() + xd * 0.1, pos.getY() + 0.3, pos.getZ() + zd * 0.1, xd, yd, zd);
                }
                if (!message.silent) {
//                  TODO - sound
                    level.playLocalSound(pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1, random.nextFloat() * 0.1f + 0.9f, false);
                }
            }
        },
        PARTICLE_BREATHED_POSITION {
            @Override
            public void handle(Supplier<NetworkEvent.Context> ctx, Message message) {
                for (int i = 0; i < 8; i++) {
                    ClientWorld level = Minecraft.getInstance().level;
                    SoulDragonEntity dragon = message.getDragon(level);
                    Vector3d movement = dragon.getDeltaMovement();
                    Vector3d lookVec = dragon.getHeadLookVector(1).normalize();
                    lookVec.yRot((-(float) Math.PI / 4F));
                    double rx = message.pos.getX() + dragon.getRandom().nextGaussian() / 2;
                    double ry = message.pos.getY() + dragon.getRandom().nextGaussian() / 2;
                    double rz = message.pos.getZ() + dragon.getRandom().nextGaussian() / 2;
                    level.addParticle(ModParticles.SOUL_DRAGON_BREATH, rx, ry, rz, -lookVec.x * 0.08 + movement.x, -lookVec.y * 0.3 + movement.y, -lookVec.z * 0.08 + movement.z);
                }
            }
        };

        public abstract void handle(Supplier<NetworkEvent.Context> ctx, Message message);
    }

    public static class Message {
        private final MessageType type;
        private final int dragon;
        public final BlockPos pos;
        public final boolean silent;
        public final boolean pure;

        public Message(MessageType type, BlockPos pos, int dragon, boolean silent, boolean pure) {
            this.type = type;
            this.pos = pos;
            this.dragon = dragon;
            this.silent = silent;
            this.pure = pure;
        }

        public Message(PacketBuffer buffer) {
            this(buffer.readEnum(MessageType.class), buffer.readBlockPos(), buffer.readVarInt(), buffer.readBoolean(), buffer.readBoolean());
        }

        @SuppressWarnings("UnnecessaryCallToStringValueOf")
        public SoulDragonEntity getDragon(World world) {
            Entity entity = world.getEntity(dragon);
            if (entity instanceof SoulDragonEntity) {
                return (SoulDragonEntity) entity;
            }
            throw new IllegalStateException(String.format("%s is not a SoulDragon", Objects.toString(Utils.applyIfNonnull(entity, e -> e.getType().getRegistryName()))));
        }

        public void toBytes(PacketBuffer buffer) {
            buffer.writeEnum(type);
            buffer.writeBlockPos(pos);
            buffer.writeVarInt(dragon);
            buffer.writeBoolean(silent);
            buffer.writeBoolean(pure);
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            type.handle(ctx, this);
            ctx.get().setPacketHandled(true);
        }
    }
}
