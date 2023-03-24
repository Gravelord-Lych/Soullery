package lych.soullery.entity.projectile;

import lych.soullery.entity.ModEntities;
import lych.soullery.item.ModItems;
import lych.soullery.mixin.client.ClientWorldAccessor;
import lych.soullery.util.RedstoneParticles;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IWorldRendererMixin;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import java.awt.*;

public class SoulArrowEntity extends AbstractArrowEntity {
    private static final DataParameter<Boolean> DATA_AOE = EntityDataManager.defineId(SoulArrowEntity.class, DataSerializers.BOOLEAN);
    private double damageMultiplier = 1;

    public SoulArrowEntity(EntityType<? extends SoulArrowEntity> type, World world) {
        super(type, world);
    }

    public SoulArrowEntity(World world, double x, double y, double z) {
        this(ModEntities.SOUL_ARROW, world, x, y, z);
    }

    public SoulArrowEntity(EntityType<? extends SoulArrowEntity> type, World world, double x, double y, double z) {
        super(type, x, y, z, world);
    }

    public SoulArrowEntity(World world, LivingEntity owner) {
        this(ModEntities.SOUL_ARROW, world, owner);
    }

    public SoulArrowEntity(EntityType<? extends SoulArrowEntity> type, World world, LivingEntity owner) {
        super(type, owner, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_AOE, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isSilentArrow() && level instanceof ServerWorld) {
            ((ServerWorld) level).sendParticles(ParticleTypes.SOUL, getX() + getDeltaMovement().x, getY() + getDeltaMovement().y + 0.05, getZ() + getDeltaMovement().z, 1, 0, 0.02, 0, 0.01);
        }
    }

    protected boolean isSilentArrow() {
        return false;
    }

    public void setDoAreaOfEffectDamage(boolean aoe) {
        entityData.set(DATA_AOE, aoe);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if (level.isClientSide() && entityData.get(DATA_AOE)) {
            ClientWorldAccessor cl = (ClientWorldAccessor) level;
            for (int i = 0; i < 40 + random.nextInt(16); i++) {
                Vector3d location = result.getLocation();
                double power = 0.5 + random.nextDouble() * 4;
                double randomAngle = random.nextDouble() * Math.PI * 2;
                double xOffs = Math.cos(randomAngle) * power;
                double ySpeed = 0.01 + random.nextDouble() * 0.5;
                double zOffs = Math.sin(randomAngle) * power;
                IParticleData data = RedstoneParticles.create(Color.HSBtoRGB((random.nextFloat() - random.nextFloat()) * 0.025f + 0.5f, random.nextFloat() * 0.2f + 0.8f, random.nextFloat() * 0.1f + 0.9f));
                Particle particle = ((IWorldRendererMixin) cl.getLevelRenderer()).callAddParticleInternal(data, data.getType().getOverrideLimiter(), location.x + xOffs * 0.6, location.y + 0.35, location.z + zOffs * 0.6, xOffs, ySpeed, zOffs);
                if (particle != null) {
                    particle.setPower((float) power);
                }
            }
        }
        if (!level.isClientSide() && entityData.get(DATA_AOE)) {
            remove();
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        super.onHitBlock(result);
        if (entityData.get(DATA_AOE)) {
            doAreaOfEffectDamage(result);
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        sendParticles(4, 8);
        doAreaOfEffectDamage(result);
    }

    protected void doAreaOfEffectDamage(RayTraceResult result) {
        if (level.isClientSide()) {
            return;
        }
        AxisAlignedBB bb = new AxisAlignedBB(result.getLocation().subtract(1.5, 2, 1.5), result.getLocation().add(1.5, 2, 1.5));
        if (entityData.get(DATA_AOE)) {
            bb = bb.inflate(2);
        }
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, bb, EntityPredicates.ATTACK_ALLOWED)) {
            int damage = MathHelper.ceil(MathHelper.clamp(getDeltaMovement().length() * getBaseDamage() / 2, 0, 2147483647));
            entity.hurt(Utils.getOrDefault(getOwner(), DamageSource.MAGIC, owner -> DamageSource.indirectMagic(this, owner)), damage);
        }
    }

    protected void sendParticles(int minCount, int maxCount) {
        if (level instanceof ServerWorld) {
            ((ServerWorld) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX() + random.nextGaussian() * 0.1, getY() + random.nextDouble() * 0.5, getZ() + random.nextGaussian() * 0.1, random.nextInt(maxCount - minCount + 1) + minCount, random.nextGaussian() * 0.05, random.nextGaussian() * 0.1, random.nextGaussian() * 0.05, 0.1);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.SOUL_ARROW);
    }

    @Override
    public double getBaseDamage() {
        return super.getBaseDamage() * getDamageMultiplier();
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = Math.max(0, damageMultiplier);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
