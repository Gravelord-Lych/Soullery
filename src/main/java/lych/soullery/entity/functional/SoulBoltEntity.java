package lych.soullery.entity.functional;

import com.google.common.base.Preconditions;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.util.BoundingBoxUtils;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SoulBoltEntity extends Entity implements IHasOwner<LivingEntity> {
    private int life = 15;
    private boolean firstTick = true;
    private boolean spawnsFire = true;
    private int fireCount = 2;
    private int fireRadius = 1;
    private UUID ownerUUID;
    private double knockbackStrength = 0.5;
    private double knockbackModifier;
    private float damage = 10;

    public SoulBoltEntity(EntityType<? extends SoulBoltEntity> type, World world) {
        super(type, world);
        noCulling = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (firstTick) {
            spawnFire();
            doShockwaveAttack();
            firstTick = false;
        }
        life--;
        if (life <= 0) {
            remove();
        }
    }

    protected void spawnFire() {
        if (!level.isClientSide() && level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            LivingEntity owner = getOwner();
            if (owner != null && !ForgeEventFactory.getMobGriefingEvent(level, owner)) {
                return;
            }
            BlockPos pos = blockPosition();
            createFire(pos);
            doShockwaveAttack();
            for (int i = 0; i < fireCount; ++i) {
                BlockPos newPos = pos.offset(random.nextInt(fireRadius * 2) - fireRadius, random.nextInt(fireRadius * 2) - fireRadius, random.nextInt(fireRadius * 2) - fireRadius);
                createFire(newPos);
            }
        }
    }

    @SuppressWarnings("deprecation")
    protected void createFire(BlockPos pos) {
        BlockState fireType = AbstractFireBlock.getState(level, pos);
        if (level.getBlockState(pos).isAir() && fireType.canSurvive(level, pos)) {
            level.setBlockAndUpdate(pos, fireType);
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("Life")) {
            life = compoundNBT.getInt("Life");
        }
        if (compoundNBT.contains("FirstTickSoulBolt")) {
            firstTick = compoundNBT.getBoolean("FirstTickSoulBolt");
        }
        if (compoundNBT.contains("SpawnsFire")) {
            spawnsFire = compoundNBT.getBoolean("SpawnsFire");
        }
        if (compoundNBT.contains("FireCount")) {
            fireCount = compoundNBT.getInt("FireCount");
        }
        if (compoundNBT.contains("FireRadius")) {
            fireRadius = compoundNBT.getInt("FireRadius");
        }
        if (compoundNBT.contains("KnockbackStrength")) {
            knockbackStrength = compoundNBT.getDouble("KnockbackStrength");
        }
        knockbackModifier = compoundNBT.getDouble("KnockbackModifier");
        if (compoundNBT.contains("Damage")) {
            setDamage(compoundNBT.getFloat("Damage"));
        }
        loadOwner(compoundNBT);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        compoundNBT.putInt("Life", life);
        compoundNBT.putBoolean("FirstTickSoulBolt", firstTick);
        compoundNBT.putBoolean("SpawnsFire", spawnsFire);
        compoundNBT.putInt("FireCount", fireCount);
        compoundNBT.putInt("FireRadius", fireRadius);
        compoundNBT.putDouble("KnockbackStrength", knockbackStrength);
        compoundNBT.putDouble("KnockbackModifier", knockbackModifier);
        compoundNBT.putFloat("Damage", getDamage());
        saveOwner(compoundNBT);
    }

    protected void doShockwaveAttack() {
        if (knockbackStrength <= 0) {
            return;
        }
        double knockbackRadius = 5 + knockbackStrength * 0.8;
        Vector3d pos = position();
        LivingEntity owner = getOwner();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, BoundingBoxUtils.inflate(pos, knockbackRadius, knockbackRadius / 2, knockbackRadius))) {
            if ((owner == null || entity != owner && owner.canAttack(entity)) && EntityPredicates.ATTACK_ALLOWED.test(entity)) {
                double distance = distanceTo(entity);
                if (distance > knockbackRadius) {
                    continue;
                }

                entity.hurt(owner == null ? DamageSource.MAGIC : DamageSource.indirectMagic(this, owner), damage);
                entity.setSecondsOnFire(4);
                ((IEntityMixin) entity).setOnSoulFire(true);

                Vector3d vecToThis = pos.vectorTo(entity.position()).normalize();
                double strength = MathHelper.lerp(knockbackRadius - distance / knockbackRadius, 0.2, 1);
                strength *= MathHelper.lerp(entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 1, knockbackModifier);
                vecToThis = vecToThis.scale(0.5).scale(strength);
                entity.push(vecToThis.x, vecToThis.y, vecToThis.z);
            }
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSqr) {
        double scale = 256 * getViewScale();
        return distanceSqr < scale * scale;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void setSpawnsFire(boolean spawnsFire) {
        this.spawnsFire = spawnsFire;
    }

    public void setFireCount(int fireCount) {
        this.fireCount = fireCount;
    }

    public void setFireRadius(int fireRadius) {
        this.fireRadius = fireRadius;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public void setKnockbackStrength(double knockbackStrength) {
        this.knockbackStrength = knockbackStrength;
    }

    public void setKnockbackModifier(double knockbackModifier) {
        Preconditions.checkArgument(knockbackStrength >= 0 && knockbackModifier <= 1, "knockbackModifier should be in range [0, 1]");
        this.knockbackModifier = knockbackModifier;
    }
}
