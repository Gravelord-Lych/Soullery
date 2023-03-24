package lych.soullery.entity.projectile;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EtherealArrowEntity extends SoulArrowEntity {
    private static final DataParameter<Boolean> DATA_ENHANCED = EntityDataManager.defineId(EtherealArrowEntity.class, DataSerializers.BOOLEAN);

    public EtherealArrowEntity(EntityType<? extends EtherealArrowEntity> type, World world) {
        super(type, world);
    }

    public EtherealArrowEntity(World world, double x, double y, double z) {
        super(ModEntities.ETHEREAL_ARROW, world, x, y, z);
    }

    public EtherealArrowEntity(World world, LivingEntity owner) {
        super(ModEntities.ETHEREAL_ARROW, world, owner);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ENHANCED, false);
    }

    public boolean isEnhanced() {
        return entityData.get(DATA_ENHANCED);
    }

    public void setEnhanced(boolean enhanced) {
        entityData.set(DATA_ENHANCED, enhanced);
    }

    @Override
    public void tick() {
        super.tick();
        if (!World.isInWorldBounds(blockPosition()) || !Double.isFinite(getX()) || !Double.isFinite(getY()) || !Double.isFinite(getZ())) {
            remove();
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof ProjectileEntity) {
            return false;
        }
        return super.canHitEntity(entity) && entity != getOwner() && ESVMob.nonESVMob(entity);
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult ray) {
        playSound(getHitGroundSoundEvent(), 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
        sendParticles(3, 4);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        if (isEnhanced()) {
            EntityUtils.clearInvulnerableTime(result.getEntity());
        }
    }

    @Override
    protected boolean isSilentArrow() {
        return true;
    }

    @Override
    protected void doAreaOfEffectDamage(RayTraceResult result) {}

    @Override
    public void playerTouch(PlayerEntity player) {}
}
