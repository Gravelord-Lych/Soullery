package lych.soullery.entity.projectile;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.functional.FangsEntity;
import lych.soullery.mixin.ArrowEntityAccessor;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FangsSummonerEntity extends ArrowEntity {
    private static final float FANGS_DAMAGE = 6;
    private static final double ENTITY_DAMAGE = 12;

    private int spawnChildrenCountPerDirection;
    private float fangsDamage = FANGS_DAMAGE;

    public FangsSummonerEntity(EntityType<? extends FangsSummonerEntity> arrow, World world) {
        super(arrow, world);
    }

    public FangsSummonerEntity(World world, double x, double y, double z) {
        this(ModEntities.FANGS_SUMMONER, world);
        setPos(x, y, z);
    }

    public FangsSummonerEntity(LivingEntity owner, World world) {
        this(world, owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        setOwner(owner);
    }

    {
        pickup = PickupStatus.DISALLOWED;
        setBaseDamage(ENTITY_DAMAGE);
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult ray) {
        if (!level.isClientSide()) {
            BlockPos hitPos = ray.getBlockPos();
            for (int i = 0; i < spawnChildrenCountPerDirection + 1; i++) {
                if (getOwner() != null && getOwner() instanceof MobEntity && ((MobEntity) getOwner()).getTarget() != null) {
                    LivingEntity target = ((MobEntity) getOwner()).getTarget();
                    EntityUtils.createFangs(hitPos.getX() + i, hitPos.getZ(), Math.min(target.getY(), getOwner().getY()), Math.min(target.getY(), getOwner().getY()), 0, 1 + i, (LivingEntity) getOwner(), level, this::acceptFangs);
                    if (i != 0) {
                        EntityUtils.createFangs(hitPos.getX() - i, hitPos.getZ(), Math.min(target.getY(), getOwner().getY()), Math.min(target.getY(), getOwner().getY()), 0, 1 + i, (LivingEntity) getOwner(), level, this::acceptFangs);
                        EntityUtils.createFangs(hitPos.getX(), hitPos.getZ() + i, Math.min(target.getY(), getOwner().getY()), Math.min(target.getY(), getOwner().getY()), 0, 1 + i, (LivingEntity) getOwner(), level, this::acceptFangs);
                        EntityUtils.createFangs(hitPos.getX(), hitPos.getZ() - i, Math.min(target.getY(), getOwner().getY()), Math.min(target.getY(), getOwner().getY()), 0, 1 + i, (LivingEntity) getOwner(), level, this::acceptFangs);
                    }
                }
            }
        }
        super.onHitBlock(ray);
    }

    private void acceptFangs(FangsEntity fangs) {
        fangs.setDamage(fangsDamage);
        ((ArrowEntityAccessor) this).getEffects().forEach(fangs::addEffect);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    public void setDamageMultiplierIfHitEntity(double multiplier) {
        setBaseDamage(ENTITY_DAMAGE * multiplier);
    }

    public int getSpawnChildrenCountPerDirection() {
        return spawnChildrenCountPerDirection;
    }

    public void setSpawnChildrenCountPerDirection(int spawnChildrenCountPerDirection) {
        this.spawnChildrenCountPerDirection = spawnChildrenCountPerDirection;
    }

    public float getFangsDamage() {
        return fangsDamage;
    }

    public void setFangsDamage(float fangsDamage) {
        this.fangsDamage = fangsDamage;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
