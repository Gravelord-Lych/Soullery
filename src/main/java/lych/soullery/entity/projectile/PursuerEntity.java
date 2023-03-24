package lych.soullery.entity.projectile;

import lych.soullery.client.particle.ModParticles;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class PursuerEntity extends ProjectileEntity {
    private static final float DAMAGE = 8;
    private static final float TYPE_2_DAMAGE = 5;
    private static final double VELOCITY = 0.35;
    private static final double VELOCITY_TYPE_2 = 0.47;
    private static final DataParameter<Boolean> DATA_ALT_TYPE = EntityDataManager.defineId(PursuerEntity.class, DataSerializers.BOOLEAN);
    private int target = -1;
    private int seekTicksRemaining = 30;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    private int noMoveTicks;

    public PursuerEntity(EntityType<? extends PursuerEntity> type, World world) {
        super(type, world);
    }

    public PursuerEntity(double x, double y, double z, double tx, double ty, double tz, World world) {
        this(ModEntities.PURSUER, world);
        moveTo(x, y, z, yRot, xRot);
        setDeltaMovement(tx, ty, tz);
    }

    public PursuerEntity(LivingEntity owner, LivingEntity target, double yOffs, World world) {
        this(ModEntities.PURSUER, world);
        setTarget(target);
        setOwner(owner);
        double x = owner.getX();
        double y = owner.getY() + yOffs;
        double z = owner.getZ();
        moveTo(x, y, z, yRot, xRot);
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = getTarget();
        if (!level.isClientSide()) {
            targetDeltaX = MathHelper.clamp(targetDeltaX * 1.025, -1, 1);
            targetDeltaY = MathHelper.clamp(targetDeltaY * 1.025, -1, 1);
            targetDeltaZ = MathHelper.clamp(targetDeltaZ * 1.025, -1, 1);
            Vector3d oldMovement = getDeltaMovement();
            setDeltaMovement(oldMovement.add((targetDeltaX - oldMovement.x) * getVelocity(), (targetDeltaY - oldMovement.y) * getVelocity(), (targetDeltaZ - oldMovement.z) * getVelocity()));
            if (EntityUtils.isAlive(target) && seekTicksRemaining > 0) {
                updateMovement(target);
                seekTicksRemaining--;
            }
        }
        if (getDeltaMovement().lengthSqr() <= 1.0E-8) {
            noMoveTicks++;
        }
        if (noMoveTicks >= 10) {
            remove();
            return;
        }
        RayTraceResult ray = ProjectileHelper.getHitResult(this, this::canHitEntity);
        if (ray.getType() != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, ray)) {
            onHit(ray);
        }
        checkInsideBlocks();
        Vector3d movement = getDeltaMovement();
        double newX = getX() + movement.x;
        double newY = getY() + movement.y;
        double newZ = getZ() + movement.z;
        ProjectileHelper.rotateTowardsMovement(this, 0.4f);
        setPos(newX, newY, newZ);
        if (level.isClientSide()) {
            level.addParticle(isAltType() ? ModParticles.PURSUER_RAIL_TYPE_2 : ModParticles.PURSUER_RAIL, getX() - movement.x, getY() - movement.y - 0.2, getZ() - movement.z, 0, 0, 0);
        }
    }

    private void updateMovement(LivingEntity target) {
        double tx = target.getX(0.5);
        double ty = target.getY(0.5);
        double tz = target.getZ(0.5);
        Vector3d delta = new Vector3d(tx - getX(), ty - getY(), tz - getZ());
        delta = delta.normalize().scale(getVelocity());
        targetDeltaX = delta.x;
        targetDeltaY = delta.y;
        targetDeltaZ = delta.z;
    }

    private double getVelocity() {
        return isAltType() ? VELOCITY_TYPE_2 : VELOCITY;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult ray) {
        super.onHitEntity(ray);
        DamageSource src = getOwner() == null ? DamageSource.MAGIC : DamageSource.indirectMagic(this, getOwner());
        if (isAltType()) {
            src.bypassMagic();
        }
        src.setScalesWithDifficulty().setProjectile();
        ray.getEntity().hurt(src, isAltType() ? TYPE_2_DAMAGE : DAMAGE);
    }

    @Override
    protected void onHit(RayTraceResult ray) {
        super.onHit(ray);
        remove();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof PursuerEntity && ((PursuerEntity) entity).getOwner() == getOwner()) {
            return false;
        }
        if (ESVMob.isESVMob(entity) && entity != getTarget()) {
            return false;
        }
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_ALT_TYPE, false);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Target", target);
        compoundNBT.putBoolean("AltType", isAltType());
        compoundNBT.putInt("SeekTicks", seekTicksRemaining);
        compoundNBT.putDouble("TXD", targetDeltaX);
        compoundNBT.putDouble("TYD", targetDeltaY);
        compoundNBT.putDouble("TZD", targetDeltaZ);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("Target")) {
            target = compoundNBT.getInt("Target");
        }
        setAltType(compoundNBT.getBoolean("AltType"));
        seekTicksRemaining = compoundNBT.getInt("SeekTicks");
        targetDeltaX = compoundNBT.getDouble("TXD");
        targetDeltaY = compoundNBT.getDouble("TYD");
        targetDeltaZ = compoundNBT.getDouble("TZD");
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level.isClientSide()) {
            ((ServerWorld) level).sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 15, 0.2, 0.2, 0.2, 0);
            remove();
        }
        return true;
    }

    @Nullable
    public LivingEntity getTarget() {
        Entity entity = level.getEntity(target);
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = Utils.getOrDefault(target, -1, Entity::getId);
    }

    public boolean isAltType() {
        return entityData.get(DATA_ALT_TYPE);
    }

    public void setAltType(boolean altType) {
        entityData.set(DATA_ALT_TYPE, altType);
    }
}
