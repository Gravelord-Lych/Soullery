package lych.soullery.entity.projectile;

import lych.soullery.util.Vectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class SeekingProjectileEntity extends DamagingProjectileEntity {
    private int targetId = -1;
    private int maxSeekTicks = Integer.MAX_VALUE;
    private int seekTicksRemaining = getMaxSeekTicks();

    protected SeekingProjectileEntity(EntityType<? extends SeekingProjectileEntity> type, World world) {
        super(type, world);
    }

    public SeekingProjectileEntity(EntityType<? extends SeekingProjectileEntity> type, double x, double y, double z, double tx, double ty, double tz, World world) {
        super(type, x, y, z, tx, ty, tz, world);
    }

    public SeekingProjectileEntity(EntityType<? extends SeekingProjectileEntity> type, LivingEntity owner, double tx, double ty, double tz, World world) {
        super(type, owner, tx, ty, tz, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null && canSeek()) {
            Vector3d target = getVectorToTarget(getTarget()).normalize().scale(0.8);
            Vector3d movement = getDeltaMovement();

            double movementLength = movement.length();
            double targetLength = target.length();
            double totalLength = Math.sqrt(movementLength * movementLength + targetLength * targetLength);

            if (shouldSeek(Vectors.getAngle(movement, target))) {
                Vector3d newMotion = movement.scale(movementLength / totalLength).add(target.scale(movementLength / totalLength));
                setDeltaMovement(newMotion.add(0, 0.045, 0));
            }
        }
        if (seekTicksRemaining != Integer.MAX_VALUE && seekTicksRemaining > 0) {
            seekTicksRemaining--;
        }
    }

    protected Vector3d getVectorToTarget(Entity target) {
        return new Vector3d(target.getX() - getX(), target.getEyeHeight() - getY(), target.getZ() - getZ());
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("TargetId", getTargetId());
        compoundNBT.putInt("MaxSeekTicks", getMaxSeekTicks());
        compoundNBT.putInt("SeekTicksRemaining", seekTicksRemaining);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("TargetId", Constants.NBT.TAG_INT)) {
            setTargetId(compoundNBT.getInt("TargetId"));
        }
        if (compoundNBT.contains("MaxSeekTicks", Constants.NBT.TAG_INT)) {
            setMaxSeekTicks(compoundNBT.getInt("MaxSeekTicks"));
        }
        if (compoundNBT.contains("SeekTicksRemaining", Constants.NBT.TAG_INT)) {
            seekTicksRemaining = compoundNBT.getInt("SeekTicksRemaining");
        }
    }

    protected boolean canSeek() {
        return seekTicksRemaining > 0;
    }

//    TODO
    protected boolean shouldSeek(double angle) {
        return true;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    @Nullable
    public Entity getTarget() {
        return level.getEntity(getTargetId());
    }

    public void setTarget(@Nullable Entity target) {
        setTargetId(target == null ? -1 : target.getId());
    }

    public int getMaxSeekTicks() {
        return maxSeekTicks;
    }

    public void setMaxSeekTicks(int maxSeekTicks) {
        this.maxSeekTicks = maxSeekTicks;
    }
}
