package lych.soullery.entity.projectile;

import lych.soullery.client.particle.ModParticles;
import lych.soullery.effect.ModEffects;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.IPurifiable;
import lych.soullery.network.SoulDragonNetwork;
import lych.soullery.network.SoulDragonNetwork.MessageType;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

public class SoulballEntity extends DamagingProjectileEntity implements IPurifiable {
    private static final DataParameter<Boolean> DATA_PURE = EntityDataManager.defineId(SoulballEntity.class, DataSerializers.BOOLEAN);
    private boolean usedForBombard;

    public SoulballEntity(EntityType<? extends SoulballEntity> type, World world) {
        super(type, world);
    }

    public SoulballEntity(double x, double y, double z, double tx, double ty, double tz, World world) {
        super(ModEntities.SOULBALL, x, y, z, tx, ty, tz, world);
    }

    public SoulballEntity(LivingEntity owner, double x, double y, double z, World world) {
        super(ModEntities.SOULBALL, owner, x, y, z, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PURE, false);
    }

    @Override
    protected void onHit(RayTraceResult ray) {
        super.onHit(ray);
        Entity owner = getOwner();
        if (ray.getType() != RayTraceResult.Type.ENTITY || !((EntityRayTraceResult) ray).getEntity().is(owner)) {
            if (!level.isClientSide()) {
                List<LivingEntity> victims = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
                AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(this.level, this.getX(), this.getY(), this.getZ());
                if (owner instanceof LivingEntity) {
                    cloud.setOwner((LivingEntity)owner);
                }

                cloud.setParticle(isPurified() ? ModParticles.SOUL_DRAGON_BREATH_PURE : ModParticles.SOUL_DRAGON_BREATH);
                cloud.setRadius(3);
                cloud.setDuration(isUsedForBombard() ? 40 : 600);
                if (isUsedForBombard()) {
                    cloud.setRadiusPerTick(-0.05f);
                } else {
                    cloud.setRadiusPerTick((7 - cloud.getRadius()) / cloud.getDuration());
                }
                cloud.addEffect(new EffectInstance(isPurified() ? ModEffects.PURE_SOUL_FIRED : ModEffects.SOUL_FIRED, 1, isPurified() ? 1 : 0));
                if (isPurified()) {
                    cloud.addEffect(new EffectInstance(Effects.WEAKNESS, 20 * 7, 0, false, false, true));
                }

                if (!victims.isEmpty()) {
                    for (LivingEntity victim : victims) {
                        double distanceSqr = distanceToSqr(victim);
                        if (distanceSqr < 4 * 4) {
                            cloud.setPos(victim.getX(), victim.getY(), victim.getZ());
                            break;
                        }
                    }
                }

                SoulDragonNetwork.INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), new SoulDragonNetwork.Message(MessageType.SHOW_SOULBALL_HIT_PARTICLE, blockPosition(), getId(), isSilent(), isPurified()));

                level.addFreshEntity(cloud);
                remove();
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected IParticleData getTrailParticle() {
        return isPurified() ? ModParticles.SOUL_DRAGON_BREATH_PURE : ModParticles.SOUL_DRAGON_BREATH;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("Purified", isPurified());
        compoundNBT.putBoolean("UsedForBombard", isUsedForBombard());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setPurified(compoundNBT.getBoolean("Purified"));
        setUsedForBombard(compoundNBT.getBoolean("UsedForBombard"));
    }

    public boolean isUsedForBombard() {
        return usedForBombard;
    }

    public void setUsedForBombard(boolean usedForBombard) {
        this.usedForBombard = usedForBombard;
    }
}
