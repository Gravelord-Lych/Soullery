package lych.soullery.entity.projectile;

import lych.soullery.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class RedstoneBombEntity extends DamagingProjectileEntity {
    private static final float DAMAGE = 6;

    public RedstoneBombEntity(EntityType<? extends RedstoneBombEntity> type, World world) {
        super(type, world);
    }

    public RedstoneBombEntity(double x, double y, double z, double tx, double ty, double tz, World world) {
        super(ModEntities.REDSTONE_BOMB, x, y, z, tx, ty, tz, world);
    }

    public RedstoneBombEntity(LivingEntity owner, double tx, double ty, double tz, World world) {
        super(ModEntities.REDSTONE_BOMB, owner, tx, ty, tz, world);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected IParticleData getTrailParticle() {
        return RedstoneParticleData.REDSTONE;
    }

    @Override
    protected void onHit(RayTraceResult ray) {
        super.onHit(ray);
        remove();
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult ray) {
        super.onHitEntity(ray);
        DamageSource src = getOwner() != null ? DamageSource.indirectMagic(this, getOwner()) : DamageSource.MAGIC;
        src.setScalesWithDifficulty();
        if (!ray.getEntity().equals(getOwner())){
            ray.getEntity().hurt(src, DAMAGE);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
