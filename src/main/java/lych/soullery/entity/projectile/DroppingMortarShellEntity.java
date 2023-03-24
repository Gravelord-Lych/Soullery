package lych.soullery.entity.projectile;

import lych.soullery.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import static lych.soullery.entity.projectile.RisingMortarShellEntity.*;

public class DroppingMortarShellEntity extends ProjectileItemEntity implements IMortarShell {
    private float explosionPower = DEFAULT_EXPLOSION_POWER;
    private boolean burning;

    public DroppingMortarShellEntity(EntityType<? extends DroppingMortarShellEntity> type, World world) {
        super(type, world);
    }

    public DroppingMortarShellEntity(double x, double y, double z, World world) {
        super(ModEntities.DROPPING_MORTAR_SHELL, x, y, z, world);
    }

    public DroppingMortarShellEntity(LivingEntity owner, World world) {
        super(ModEntities.DROPPING_MORTAR_SHELL, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (level.isClientSide || (owner == null || owner.isAlive()) && level.hasChunkAt(this.blockPosition())) {
            if (isBurning()) {
                setSecondsOnFire(1);
            }
        }
    }

    @Override
    protected void onHit(RayTraceResult ray) {
        super.onHit(ray);
        onCannonballHit(this);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult ray) {
        super.onHitEntity(ray);
        onCannonballHitEntity(ray, this);
    }

    @Override
    public float getExplosionPower() {
        return explosionPower;
    }

    @Override
    public void setExplosionPower(float explosionPower) {
        this.explosionPower = explosionPower;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putFloat("ExplosionPower", getExplosionPower());
        compoundNBT.putBoolean("Burning", isBurning());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("ExplosionPower", Constants.NBT.TAG_FLOAT)) {
            setExplosionPower(compoundNBT.getFloat("ExplosionPower"));
        }
        setBurning(compoundNBT.getBoolean("Burning"));
    }

    @Override
    public boolean isBurning() {
        return burning;
    }

    @Override
    public void setBurning(boolean burning) {
        this.burning = burning;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
