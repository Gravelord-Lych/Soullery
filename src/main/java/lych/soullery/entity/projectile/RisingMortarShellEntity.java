package lych.soullery.entity.projectile;

import lych.soullery.entity.ModEntities;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ModDamageSources;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class RisingMortarShellEntity extends AbstractFireballEntity implements IMortarShell {
    static final float DAMAGE = 10;
    static final float DEFAULT_EXPLOSION_POWER = 2;

    private double requiredHeight = 50;
    private double startHeight;
    @Nullable
    private Vector3d target;
    private float explosionPower = DEFAULT_EXPLOSION_POWER;
    private boolean burning;

    public RisingMortarShellEntity(EntityType<? extends RisingMortarShellEntity> type, World world) {
        super(type, world);
        requiredHeight = -1;
        target = null;
    }

    public RisingMortarShellEntity(Vector3d target, double x, double y, double z, World world) {
        super(ModEntities.RISING_MORTAR_SHELL, x, y, z, 0, 1, 0, world);
        this.target = target;
    }

    public RisingMortarShellEntity(Vector3d target, LivingEntity owner, World world) {
        super(ModEntities.RISING_MORTAR_SHELL, owner, 0, 1, 0, world);
        this.target = target;
    }

    { recordStartPos(); }

    private void recordStartPos() {
        startHeight = position().y;
    }

    public double getRequiredHeight() {
        return requiredHeight;
    }

    public void setRequiredHeight(double requiredHeight) {
        this.requiredHeight = requiredHeight;
    }

    @Override
    protected boolean shouldBurn() {
        return isBurning();
    }

    @Override
    public void tick() {
        super.tick();
        handleMovement();
        if (target != null && requiredHeight > 0 && getY() - startHeight >= requiredHeight) {
            DroppingMortarShellEntity shell = new DroppingMortarShellEntity(target.x, target.y > getY() - 10 ? target.y + 10 : getY(), target.z, level);
            shell.setBurning(isBurning());
            shell.setExplosionPower(getExplosionPower());
            shell.setOwner(getOwner());
            level.addFreshEntity(shell);
            remove();
        }
    }

    private void handleMovement() {
        Vector3d standard = new Vector3d(0, 1, 0);
        Vector3d movement = getDeltaMovement();
        if (!movement.equals(standard)) {
            double x = MathHelper.lerp(0.3, movement.x, standard.x);
            double y = MathHelper.lerp(0.3, movement.y, standard.y);
            double z = MathHelper.lerp(0.3, movement.z, standard.z);
            setDeltaMovement(new Vector3d(x, y, z));
        }
    }

    @Override
    protected void onHit(RayTraceResult ray) {
        super.onHit(ray);
        onCannonballHit(this);
    }

    static <T extends ProjectileEntity & IMortarShell> void onCannonballHit(T shell) {
        if (!shell.level.isClientSide()) {
            boolean canGrief = EntityUtils.canMobGrief(shell.getOwner() == null ? shell : shell.getOwner());
            shell.level.explode(null,
                    shell.getX(),
                    shell.getY(),
                    shell.getZ(),
                    shell.getExplosionPower(),
                    canGrief && shell.isBurning(),
                    canGrief ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
            shell.remove();
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult ray) {
        super.onHitEntity(ray);
        onCannonballHitEntity(ray, this);
    }

    static <T extends ProjectileEntity & IMortarShell> void onCannonballHitEntity(EntityRayTraceResult ray, T shell) {
        if (!shell.level.isClientSide()) {
            Entity entity = ray.getEntity();
            Entity owner = shell.getOwner();
            entity.hurt(ModDamageSources.softFireball(shell, owner, false), DAMAGE);
            if (owner instanceof LivingEntity) {
                shell.doEnchantDamageEffects((LivingEntity) owner, entity);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putDouble("RequiredHeight", getRequiredHeight());
        compoundNBT.putDouble("StartHeight", startHeight);
        if (target != null) {
            compoundNBT.putDouble("TargetX", target.x);
            compoundNBT.putDouble("TargetY", target.y);
            compoundNBT.putDouble("TargetZ", target.z);
        }
        compoundNBT.putFloat("ExplosionPower", getExplosionPower());
        compoundNBT.putBoolean("Burning", isBurning());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("RequiredHeight", Constants.NBT.TAG_DOUBLE)) {
            setRequiredHeight(compoundNBT.getDouble("RequiredHeight"));
        }
        if (compoundNBT.contains("StartHeight", Constants.NBT.TAG_DOUBLE)) {
            startHeight = compoundNBT.getDouble("StartHeight");
        }
        double x = Double.NaN, y = Double.NaN, z = Double.NaN;
        if (compoundNBT.contains("TargetX")) {
            x = compoundNBT.getDouble("TargetX");
        }
        if (compoundNBT.contains("TargetY")) {
            y = compoundNBT.getDouble("TargetY");
        }
        if (compoundNBT.contains("TargetZ")) {
            z = compoundNBT.getDouble("TargetZ");
        }
        if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
            target = new Vector3d(x, y, z);
        } else {
            target = null;
        }
        if (compoundNBT.contains("ExplosionPower", Constants.NBT.TAG_FLOAT)) {
            setExplosionPower(compoundNBT.getFloat("ExplosionPower"));
        }
        setBurning(compoundNBT.getBoolean("Burning"));
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
