package lych.soullery.network;

import lych.soullery.Soullery;
import lych.soullery.client.particle.ModParticles;
import lych.soullery.entity.functional.FortifiedSoulCrystalEntity;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
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
                    level.playLocalSound(pos, ModSoundEvents.SOULBALL_EXPLODE.get(), SoundCategory.HOSTILE, 1, random.nextFloat() * 0.1f + 0.9f, false);
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
        },
        RECOGNIZE_FORTIFIED_CRYSTAL {
            @Override
            public void handle(Supplier<NetworkEvent.Context> ctx, Message message) {
                Entity entity = Minecraft.getInstance().level.getEntity(message.msg);
                if (entity instanceof FortifiedSoulCrystalEntity) {
                    message.getDragon(Minecraft.getInstance().level).nearestCrystal = (FortifiedSoulCrystalEntity) entity;
                } else {
                    throw new AssertionError();
                }
            }
        },
        PLAY_SHOOT_SOUND {
            @Override
            public void handle(Supplier<NetworkEvent.Context> ctx, Message message) {
                if (message.silent) {
                    return;
                }
                ClientWorld level = Minecraft.getInstance().level;
                Random random = level.random;
                level.playLocalSound(message.pos, ModSoundEvents.SOUL_DRAGON_SHOOT.get(), SoundCategory.HOSTILE, 10, (random.nextFloat() - random.nextFloat()) * 0.2f + 1, false);
            }
        },
        DRAGON_DEATH {
            @Override
            public void handle(Supplier<NetworkEvent.Context> ctx, Message message) {
                if (message.silent) {
                    return;
                }
                ActiveRenderInfo info = Minecraft.getInstance().gameRenderer.getMainCamera();
                if (info.isInitialized()) {
                    double dx = (double) message.pos.getX() - info.getPosition().x;
                    double dy = (double) message.pos.getY() - info.getPosition().y;
                    double dz = (double) message.pos.getZ() - info.getPosition().z;
                    double dis = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    double x = info.getPosition().x;
                    double y = info.getPosition().y;
                    double z = info.getPosition().z;
                    if (dis > 0) {
                        x += dx / dis * 2;
                        y += dy / dis * 2;
                        z += dz / dis * 2;
                    }
                    Minecraft.getInstance().level.playLocalSound(x, y, z, ModSoundEvents.SOUL_RABBIT_DEATH.get(), SoundCategory.HOSTILE, 5, 1, false);
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
        public final int msg;

        public Message(MessageType type, BlockPos pos, int dragon, boolean silent, boolean pure) {
            this(type, pos, dragon, silent, pure, -1);
        }

        public Message(MessageType type, BlockPos pos, int dragon, boolean silent, boolean pure, int msg) {
            this.type = type;
            this.pos = pos;
            this.dragon = dragon;
            this.silent = silent;
            this.pure = pure;
            this.msg = msg;
        }

        public Message(PacketBuffer buffer) {
            this(buffer.readEnum(MessageType.class), buffer.readBlockPos(), buffer.readVarInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readVarInt());
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
            buffer.writeVarInt(msg);
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            type.handle(ctx, this);
            ctx.get().setPacketHandled(true);
        }
    }
}
