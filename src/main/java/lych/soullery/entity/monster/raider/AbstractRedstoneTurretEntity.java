package lych.soullery.entity.monster.raider;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.iface.INoDefaultHurtAnimationEntity;
import lych.soullery.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soullery.entity.ai.goal.MultiRangedAttackGoal;
import lych.soullery.util.EntityUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@OnlyIn(value = Dist.CLIENT, _interface = INoDefaultHurtAnimationEntity.class)
public abstract class AbstractRedstoneTurretEntity extends MonsterEntity implements IHasOwner<EngineerEntity>, IRangedAttackMob, INoDefaultHurtAnimationEntity {
    private static final int DEATH_DELAY_TICKS = 40;

    @NotNull
    private OptionalBoolean elite = OptionalBoolean.ANY;
    @Nullable
    private UUID ownerUUID;
    private int life = Math.max(0, getMaxLife());

    protected AbstractRedstoneTurretEntity(EntityType<? extends AbstractRedstoneTurretEntity> type, World world) {
        super(type, world);
        blocksBuilding = true;
        maxUpStep = 1;
    }

    @Override
    public void move(MoverType type, Vector3d vector) {
        super.move(type, type == MoverType.SELF ? new Vector3d(0, vector.y > 0 ? vector.y * 0.1 : vector.y, 0) : vector);
    }

    public static AttributeModifierMap.MutableAttribute createTurretAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.MOVEMENT_SPEED, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        addRangedAttackGoal();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new CopyOwnerTargetGoal<>(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, mustSeeTarget()));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, mustSeeTarget()));
    }

    protected boolean mustSeeTarget() {
        return true;
    }

    protected void addRangedAttackGoal() {
        goalSelector.addGoal(1, new MultiRangedAttackGoal<>(this, 0, this::getAttackInterval, this::getAttackRadius, this::getAttackTimes, this::getAttackIntervalOfPerAttack));
    }

    @Override
    public void tick() {
        super.tick();
        if (life == 0) {
            hurt(DamageSource.STARVE, 1);
            life = 20;
        } else {
            life--;
        }
    }

    @Override
    protected void tickDeath() {
        deathTime++;
        EntityUtils.addParticlesAroundSelf(this, getHitParticle(), 0.013, 2 + random.nextInt(2));
        if (deathTime == DEATH_DELAY_TICKS) {
            remove();
            EntityUtils.addParticlesAroundSelf(this, getHitParticle(), 0.1, 35 + random.nextInt(6));
        }
    }

    protected int getAttackInterval() {
        return 40;
    }

    protected int getAttackTimes() {
        return 1;
    }

    protected float getAttackRadius() {
        return 16;
    }

    protected int getAttackIntervalOfPerAttack() {
        return 0;
    }

    protected int getMaxLife() {
        return 20 * 30 + random.nextInt(20 * 15);
    }

    @Deprecated
    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Deprecated
    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Nullable
    @Override
    public EngineerEntity getOwner() {
        EngineerEntity owner = IHasOwner.super.getOwner();
        if (owner != null && elite == OptionalBoolean.ANY) {
            elite = owner.isElite() ? OptionalBoolean.TRUE : OptionalBoolean.FALSE;
        }
        return owner;
    }

    @Override
    public void setOwner(@Nullable EngineerEntity owner) {
        if (owner != null && elite == OptionalBoolean.ANY) {
            elite = owner.isElite() ? OptionalBoolean.TRUE : OptionalBoolean.FALSE;
        }
        IHasOwner.super.setOwner(owner);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        if (reason == SpawnReason.MOB_SUMMONED) {
            EngineerEntity owner = getOwner();
            if (owner != null) {
                if (elite == OptionalBoolean.ANY) {
                    elite = owner.isElite() ? OptionalBoolean.TRUE : OptionalBoolean.FALSE;
                }
                if (owner.getTurretMap().remove(this) < 0) {
                    if (ConfigHelper.shouldFailhard()) {
                        throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + String.format("An engineer is trying to build %s without any turrets in his turret map", getType().getRegistryName()));
                    }
                    LOGGER.error("An engineer is trying to build {} without any turrets in his turret map", getType().getRegistryName());
                }
            }
        }
        return super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level instanceof ServerWorld) {
            EntityUtils.addParticlesAroundSelfServerside(this, (ServerWorld) level, new BlockParticleData(ParticleTypes.BLOCK, Blocks.DARK_OAK_FENCE.defaultBlockState()), 10);
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level.isClientSide()) {
            EntityUtils.addParticlesAroundSelfServerside(this, (ServerWorld) level, getHitParticle(), 15 + random.nextInt(6));
        }
        return hurt;
    }

    protected IParticleData getHitParticle() {
        return new BlockParticleData(ParticleTypes.BLOCK, Blocks.DARK_OAK_FENCE.defaultBlockState());
    }

    public boolean isElite() {
        return elite == OptionalBoolean.TRUE;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Life", life);
        compoundNBT.putInt("Elite", elite.ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("Life", Constants.NBT.TAG_INT)) {
            life = compoundNBT.getInt("Life");
        }
        if (compoundNBT.contains("Elite", Constants.NBT.TAG_INT)) {
            int ordinal = compoundNBT.getInt("Elite");
            for (OptionalBoolean ob : OptionalBoolean.values()) {
                if (ob.ordinal() == ordinal) {
                    elite = ob;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public EntityType<? extends AbstractRedstoneTurretEntity> getType() {
        return (EntityType<? extends AbstractRedstoneTurretEntity>) super.getType();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
