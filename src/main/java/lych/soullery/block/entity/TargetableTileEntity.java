package lych.soullery.block.entity;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class TargetableTileEntity extends TileEntity implements ITickableTileEntity {
    public TargetableTileEntity(TileEntityType<?> type) {
        super(type);
    }


    @Override
    public void tick() {
        if (level != null) {
            attack();
        }
    }

    protected void attack() {
        List<? extends Entity> targets = findTargets(level);
        targets.forEach(target -> applyTarget(target, level.isClientSide()));
    }

    protected AxisAlignedBB getBB(int rh, int rv) {
        return new AxisAlignedBB(getBlockPos()).inflate(rh, rv, rh);
    }

    protected double distanceToSqr(Entity entity) {
        return entity.distanceToSqr(position());
    }

    protected double horizontalDistanceToSqr(Entity entity) {
        double tx = entity.getX() - getX();
        double tz = entity.getZ() - getZ();
        return tx * tx + tz * tz;
    }

    public double getX() {
        return getBlockPos().getX() + 0.5;
    }

    public double getY() {
        return getBlockPos().getY() + 0.5;
    }

    public double getZ() {
        return getBlockPos().getZ() + 0.5;
    }

    public Vector3d position() {
        return Vector3d.atCenterOf(getBlockPos());
    }

    protected abstract List<? extends Entity> findTargets(World level);

    protected abstract void applyTarget(Entity target, boolean clientside);
}
