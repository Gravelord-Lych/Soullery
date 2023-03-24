package lych.soullery.entity.monster.raider;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.EngineerGoals.BuildRedstoneMortarGoal;
import lych.soullery.entity.ai.goal.EngineerGoals.BuildRedstoneTurretGoal;
import lych.soullery.entity.ai.goal.EngineerGoals.SendRedstoneBombGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.ai.phase.PhaseManager;
import lych.soullery.entity.projectile.RedstoneBombEntity;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.IIdentifiableEnum;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

public class EngineerEntity extends AbstractIllagerEntity implements IRangedAttackMob {
    private static final DataParameter<Boolean> DATA_DELAYING = EntityDataManager.defineId(EngineerEntity.class, DataSerializers.BOOLEAN);
    private final EngineerTurretMap turretMap = new EngineerTurretMap();
    private final PhaseManager<Phase> manager;
    private boolean attacked;
    private boolean elite;

    public EngineerEntity(EntityType<? extends AbstractIllagerEntity> type, World world) {
        super(type, world);
        manager = new PhaseManager<>(Phase::values);
        if (!level.isClientSide()) {
            registerPhasedGoals();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_DELAYING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide() && isDelaying()) {
            double r = 1;
            double g = 0;
            double b = 0;
            float rot = yBodyRot * ((float) Math.PI / 180F) + MathHelper.cos(tickCount * 0.6662F) * 0.25F;
            float xOffset = MathHelper.cos(rot);
            float zOffset = MathHelper.sin(rot);
            if (isLeftHanded()) {
                level.addParticle(ParticleTypes.ENTITY_EFFECT, getX() + (double) xOffset * 0.6, getY() + 1.8, getZ() + (double) zOffset * 0.6, r, g, b);
            } else {
                level.addParticle(ParticleTypes.ENTITY_EFFECT, getX() - (double) xOffset * 0.6, getY() + 1.8, getZ() - (double) zOffset * 0.6, r, g, b);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ArmPose getArmPose() {
        if (isDelaying()) {
            return ArmPose.SPELLCASTING;
        }
        return isCelebrating() ? ArmPose.CELEBRATING : ArmPose.CROSSED;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.ATTACK_DAMAGE, 4);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.8));
        goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 3, 1));
        goalSelector.addGoal(8, new LookAtGoal(this, MobEntity.class, 8));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, AbstractRaiderEntity.class).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true).setUnseenMemoryTicks(400));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true).setUnseenMemoryTicks(400));
    }

    protected void registerPhasedGoals() {
        goalSelector.addGoal(2, Goals.of(new BuildRedstoneMortarGoal(this, 1)).phased(manager, Phase.BUILD_TURRET).get());
        goalSelector.addGoal(3, Goals.of(new BuildRedstoneTurretGoal(this, 1)).phased(manager, Phase.BUILD_TURRET).get());
        goalSelector.addGoal(3, Goals.of(new SendRedstoneBombGoal(this, new RangedAttackGoal(this, 1, 60, 16))).phased(manager, Phase.SEND_BOMB).get());
    }

    @Override
    public void applyRaidBuffs(int wave, boolean alwaysFalse) {
        if (wave > 6) {
            int difficultyId = level.getDifficulty().getId();
            setElite(random.nextDouble() < 0.35 * difficultyId);
        }
        getTurretMap().add(ModEntities.REDSTONE_TURRET, isElite() || random.nextBoolean() ? 1 : 0);
    }

//    TODO: sound
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    public boolean isElite() {
        return elite;
    }

    public void setElite(boolean elite) {
        this.elite = elite;
    }

    public void setDelaying(boolean delaying) {
        entityData.set(DATA_DELAYING, delaying);
    }

    public boolean isDelaying() {
        return entityData.get(DATA_DELAYING);
    }

    public EngineerTurretMap getTurretMap() {
        return turretMap;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.put("PhaseManager", manager.save());
        compoundNBT.put("TurretMap", turretMap.save());
        compoundNBT.putBoolean("Attacked", isAttacked());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        manager.load(compoundNBT.getCompound("PhaseManager"));
        if (compoundNBT.contains("TurretMap", Constants.NBT.TAG_LIST)) {
            turretMap.load(compoundNBT.getList("TurretMap", Constants.NBT.TAG_COMPOUND));
        }
        setAttacked(compoundNBT.getBoolean("Attacked"));
    }

    public boolean isAttacked() {
        return attacked;
    }

    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        setDelaying(false);
        Vector3d targetPos = EntityUtils.centerOf(target);
        targetPos.add(0, target.getBbHeight() * 0.1, 0);
        Vector3d vecToTarget = new Vector3d(getX(), getEyeY(), getZ()).vectorTo(targetPos);
        RedstoneBombEntity bomb = new RedstoneBombEntity(this, vecToTarget.x, vecToTarget.y, vecToTarget.z, level);
        bomb.setPos(getX(), getEyeY(), getZ());
        level.addFreshEntity(bomb);
        if (!getTurretMap().isEmpty() && random.nextBoolean()) {
            setAttacked(true);
        }
    }

    public enum Phase implements IIdentifiableEnum {
        SEND_BOMB,
        BUILD_TURRET
    }
}
