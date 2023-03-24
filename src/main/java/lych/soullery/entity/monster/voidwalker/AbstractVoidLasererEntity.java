package lych.soullery.entity.monster.voidwalker;

import lych.soullery.Soullery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractVoidLasererEntity<T extends AbstractVoidLasererEntity<T>> extends AbstractVoidwalkerEntity {
    protected static final DataParameter<Integer> DATA_LASER_ID = EntityDataManager.defineId(AbstractVoidLasererEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> DATA_LASER_TARGET = EntityDataManager.defineId(AbstractVoidLasererEntity.class, DataSerializers.INT);

    protected AbstractVoidLasererEntity(EntityType<? extends T> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_LASER_TARGET, -1);
        entityData.define(DATA_LASER_ID, -1);
    }

    @Override
    public boolean isMeleeAttacker() {
        return false;
    }

    @Override
    public boolean canCreateWeapon() {
        return false;
    }

    @Override
    public ItemStack createWeapon() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean onSetTarget(LivingEntity target) {
        return false;
    }

    @Override
    public boolean isPrimary() {
        return false;
    }

    @Nullable
    public Entity getLaserTarget() {
        return level.getEntity(entityData.get(DATA_LASER_TARGET));
    }

    public void setLaserTarget(@Nullable Entity target) {
        entityData.set(DATA_LASER_TARGET, target == null ? -1 : target.getId());
    }

    @OnlyIn(Dist.CLIENT)
    public Vector3d getSelfPosition(float partialTicks) {
        return getPosition(partialTicks).add(0, getEyeOffset(), 0);
    }

    public float getEyeOffset() {
        return getEyeHeight();
    }

    @OnlyIn(Dist.CLIENT)
    public Vector3d getTargetPosition(Entity target, float partialTicks) {
        return target.getPosition(partialTicks).add(0, target.getBbHeight() * MathHelper.clamp(getTargetYOffset(), 0, 1), 0);
    }

    protected double getTargetYOffset() {
        return 0.45;
    }

    public static ResourceLocation prefixTex(String name) {
        return Soullery.prefixTex("entity/esv/void_laserer/" + name);
    }

    @Nullable
    public abstract ILaserProvider<? super T> provideLaser();

    public interface ILaserProvider<T extends AbstractVoidLasererEntity<T>> {
        float DEFAULT_LASER_SCALE = 0.2f;

        ResourceLocation getTextureLocation(T armorer, Entity target);

        int getSrcColor(T laserer, Entity target);

        int getDestColor(T laserer, Entity target);

        default float getSrcScale(T laserer, Entity target) {
            return DEFAULT_LASER_SCALE;
        }

        default float getDestScale(T laserer, Entity target) {
            return DEFAULT_LASER_SCALE;
        }
    }
}
