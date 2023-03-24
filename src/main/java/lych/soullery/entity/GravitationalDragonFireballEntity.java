package lych.soullery.entity;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class GravitationalDragonFireballEntity extends ThrowableEntity {
    private static final float RADIUS = 2.5f;
    private static final float FINAL_RADIUS = 4;

    public GravitationalDragonFireballEntity(EntityType<? extends ThrowableEntity> type, World world) {
        super(type, world);
    }

    public GravitationalDragonFireballEntity(double x, double y, double z, World world) {
        super(ModEntities.GRAVITATIONAL_DRAGON_FIREBALL, x, y, z, world);
    }

    public GravitationalDragonFireballEntity(LivingEntity owner, World world) {
        super(ModEntities.GRAVITATIONAL_DRAGON_FIREBALL, owner, world);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void onHit(RayTraceResult ray) {
        super.onHit(ray);
        if (ray instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityRay = (EntityRayTraceResult) ray;
            if (entityRay.getEntity() != getOwner()) {
                handleHit(entityRay);
            }
        } else {
            handleHit(ray);
        }
    }

    private void handleHit(RayTraceResult ray) {
        Entity owner = getOwner();
        if (!level.isClientSide) {
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(level, ray.getLocation().x, ray.getLocation().y, ray.getLocation().z);
            if (owner instanceof LivingEntity) {
                cloud.setOwner((LivingEntity) owner);
            }
            cloud.setParticle(ParticleTypes.DRAGON_BREATH);
            cloud.setRadius(RADIUS);
            cloud.setDuration(400);
            cloud.setRadiusPerTick((FINAL_RADIUS - cloud.getRadius()) / cloud.getDuration());
            cloud.addEffect(new EffectInstance(Effects.HARM, 1, 1));
            level.levelEvent(Constants.WorldEvents.DRAGON_FIREBALL_HIT, blockPosition(), isSilent() ? -1 : 1);
            level.addFreshEntity(cloud);
            remove();
        }
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
