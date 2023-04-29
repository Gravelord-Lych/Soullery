package lych.soullery.entity.functional;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.world.event.manager.SoulDragonFightManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class SoulCrystalEntity extends EnderCrystalEntity {
    private boolean healable;

    public SoulCrystalEntity(EntityType<? extends EnderCrystalEntity> type, World world) {
        super(type, world);
    }

    public SoulCrystalEntity(World world, double x, double y, double z) {
        this(ModEntities.SOUL_CRYSTAL, world);
        setPos(x, y ,z);
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        } else if (source.getEntity() instanceof SoulDragonEntity) {
            return false;
        } else {
            if (isAlive() && !level.isClientSide()) {
                if (canBeDestroyed(source)) {
                    destroy(source);
                }
            }
            return true;
        }
    }

    protected boolean canBeDestroyed(DamageSource source) {
        return true;
    }

    protected void destroy(DamageSource source) {
        remove();
        if (!source.isExplosion()) {
            level.explode(null, getX(), getY(), getZ(), 6, Explosion.Mode.DESTROY);
        }
        onDestroyedBy(source);
    }

    @Override
    public void kill() {
        onDestroyedBy(DamageSource.GENERIC);
        remove();
    }

    private void onDestroyedBy(DamageSource source) {
        if (level instanceof ServerWorld) {
            SoulDragonFightManager manager = SoulDragonFightManager.get((ServerWorld) level);
            manager.forEach(fight -> fight.onCrystalDestroyed(this, source));
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean isHealable() {
        return healable;
    }

    public void setHealable(boolean healable) {
        this.healable = healable;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("Healable", isHealable());
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setHealable(compoundNBT.getBoolean("Healable"));
    }
}
