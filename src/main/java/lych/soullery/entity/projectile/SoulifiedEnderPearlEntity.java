package lych.soullery.entity.projectile;

import lych.soullery.Soullery;
import lych.soullery.api.event.SoulifiedEnderPearlTeleportEvent;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ModEntityNames;
import lych.soullery.entity.monster.IPurifiable;
import lych.soullery.extension.highlight.EntityHighlightManager;
import lych.soullery.extension.highlight.HighlighterType;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SoulifiedEnderPearlEntity extends ThrowableEntity implements IPurifiable {
    private static final double REMOVE_DISTANCE_IF_UNSTABLE = 150;
    private static final DataParameter<Boolean> DATA_PURE = EntityDataManager.defineId(SoulifiedEnderPearlEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DATA_GRAVITY = EntityDataManager.defineId(SoulifiedEnderPearlEntity.class, DataSerializers.INT);

    public SoulifiedEnderPearlEntity(EntityType<? extends SoulifiedEnderPearlEntity> type, World world) {
        super(type, world);
    }

    public SoulifiedEnderPearlEntity(World world, LivingEntity owner) {
        this(ModEntities.SOULIFIED_ENDER_PEARL, world, owner);
    }

    public SoulifiedEnderPearlEntity(EntityType<? extends SoulifiedEnderPearlEntity> type, World world, LivingEntity owner) {
        this(type, world);
        setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        setOwner(owner);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSqr) {
        double size = getBoundingBox().getSize() * 4;
        if (Double.isNaN(size)) {
            size = 4;
        }
        size = size * 256;
        return distanceSqr < size * size;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_PURE, false);
        entityData.define(DATA_GRAVITY, Gravity.NORMAL.getId());
    }

    @Override
    public void tick() {
        if (getOwner() instanceof PlayerEntity && !EntityUtils.isAlive(getOwner()) || getGT().isUnstable() && EntityUtils.isAlive(getOwner()) && farAway()) {
            remove();
        } else {
            super.tick();
            if (!level.isClientSide() && isPurified()) {
                EntityHighlightManager.get((ServerWorld) level).highlight(HighlighterType.SOULIFIED_ENDER_PEARL, this);
            }
            if (getGravity() == 0) {
                balance();
            }
        }
    }

    protected void balance() {
//      Prevent weightless pearl from not moving
        setDeltaMovement(getDeltaMovement().scale(isInWater() ? 1.25 : 1.01010101010101));
    }

    private boolean farAway() {
        Objects.requireNonNull(getOwner());
        return getHorizontalDistanceSqr(getOwner().position().vectorTo(position())) >= REMOVE_DISTANCE_IF_UNSTABLE * REMOVE_DISTANCE_IF_UNSTABLE;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult ray) {
        super.onHitEntity(ray);
        ray.getEntity().hurt(DamageSource.thrown(this, getOwner()), 0);
    }

    @Override
    protected void onHit(RayTraceResult ray) {
        super.onHit(ray);
        Entity owner = getOwner();
        for (int i = 0; i < 32; i++) {
            level.addParticle(ParticleTypes.PORTAL, getX(), getY() + random.nextDouble() * 2, getZ(), random.nextGaussian(), 0, random.nextGaussian());
        }
        if (!level.isClientSide() && isAlive()) {
            if (owner instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity)owner;
                if (player.connection.getConnection().isConnected() && player.level == level && !player.isSleeping()) {
                    SoulifiedEnderPearlTeleportEvent event = new SoulifiedEnderPearlTeleportEvent(player, getX(), getY(), getZ(), this, isPurified() ? 1 : 5);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.isCanceled()) {
                        if (owner.isPassenger()) {
                            owner.stopRiding();
                        }
                        owner.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                        owner.fallDistance = 0;
                        owner.hurt(DamageSource.FALL, event.getAttackDamage());
                    }
                }
            } else if (owner != null) {
                owner.teleportTo(getX(), getY(), getZ());
                owner.fallDistance = 0;
            }
            remove();
        }
    }
    @Override
    public boolean isPurified() {
        return entityData.get(DATA_PURE);
    }

    @Override
    public void setPurified(boolean purified) {
        entityData.set(DATA_PURE, purified);
    }

    @Override
    protected float getGravity() {
        return getGT().getValue();
    }

    private Gravity getGT() {
        return Gravity.byId(entityData.get(DATA_GRAVITY));
    }

    public void setGravity(Gravity gravity) {
        Objects.requireNonNull(gravity);
        entityData.set(DATA_GRAVITY, gravity.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Gravity", entityData.get(DATA_GRAVITY));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("Gravity")) {
            entityData.set(DATA_GRAVITY, compoundNBT.getInt("Gravity"));
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerWorld level, ITeleporter teleporter) {
        Entity owner = getOwner();
        if (owner != null && owner.level.dimension() != level.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(level, teleporter);
    }

    public enum Gravity implements IIdentifiableEnum {
        NORMAL("normal", 0x17E6C3, 0.03f, false),
        HIGH("high", 0x2EE699, 0.065f, false),
        LOW("low", 0x40FFFF, 0.015f, false),
        NO("zero", 0x59D6FF, 0, true),
        NEGATIVE("negative", 0x80BFFF, -0.02f, true);

        public static final ITextComponent GRAVITY = new TranslationTextComponent(Soullery.prefixMsg("entity", ModEntityNames.SOULIFIED_ENDER_PEARL + ".gravity"));
        private static final ITextComponent SET_GRAVITY = new TranslationTextComponent(Soullery.prefixMsg("entity", ModEntityNames.SOULIFIED_ENDER_PEARL + ".gravity.set"));
        private final String id;
        private final int color;
        private final float value;
        private final boolean unstable;

        Gravity(String id, int color, float value, boolean unstable) {
            this.id = id;
            this.color = color;
            this.value = value;
            this.unstable = unstable;
        }

        public static Gravity byId(int id) {
            try {
                return IIdentifiableEnum.byOrdinal(values(), id);
            } catch (EnumConstantNotFoundException e) {
                return NORMAL;
            }
        }

        public float getValue() {
            return value;
        }

        public boolean isUnstable() {
            return unstable;
        }

        public void sendSetGravityMessage(PlayerEntity player) {
            player.sendMessage(SET_GRAVITY.copy().append(makeText()), Util.NIL_UUID);
        }

        public ITextComponent makeText() {
            return new TranslationTextComponent(Soullery.prefixMsg("entity", ModEntityNames.SOULIFIED_ENDER_PEARL + ".gravity." + id)).withStyle(Style.EMPTY.withColor(Color.fromRgb(color)));
        }

        public Gravity cycle() {
            if (this == NEGATIVE) {
                return NORMAL;
            }
            return byId(getId() + 1);
        }
    }
}
