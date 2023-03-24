package lych.soullery.entity.monster;

import lych.soullery.api.IMeta08NonAttackable;
import lych.soullery.api.shield.ISharedShieldUser;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soullery.entity.ai.goal.FollowOwnerGoal;
import lych.soullery.entity.iface.IDamageMultipliable;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.monster.boss.Meta08Entity;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@OnlyIn(value = Dist.CLIENT, _interface = IChargeableMob.class)
public class RobotEntity extends MonsterEntity implements IMeta08NonAttackable, IHasOwner<Meta08Entity>, ISharedShieldUser, IChargeableMob, IDamageMultipliable {
    private static final DataParameter<Boolean> DATA_SHIELDED = EntityDataManager.defineId(RobotEntity.class, DataSerializers.BOOLEAN);
    @Nullable
    private UUID ownerUUID;

    public RobotEntity(EntityType<? extends RobotEntity> robot, World world) {
        super(robot, world);
        xpReward = 1;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FOLLOW_RANGE, 22)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.26);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SHIELDED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.addGoal(4, new FollowOwnerGoal<>(this, 1, 12, 4, 33, false));
        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.9));
        goalSelector.addGoal(7, new LookAtGoal(this, Meta08Entity.class, 5));
        goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 3));
        goalSelector.addGoal(9, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, IMeta08NonAttackable.class).setAlertOthers(Meta08Entity.class));
        targetSelector.addGoal(2, new CopyOwnerTargetGoal<>(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getSharedShield() != null) {
            entityData.set(DATA_SHIELDED, true);
        } else {
            entityData.set(DATA_SHIELDED, false);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof IMeta08NonAttackable) {
            return false;
        }
        return super.hurt(source, amount);
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

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        saveOwner(compoundNBT);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        loadOwner(compoundNBT);
    }

    @Override
    public void setOwner(@Nullable Meta08Entity owner) {
        IHasOwner.super.setOwner(owner);
        if (owner != null) {
            owner.addModifiersTo(this);
        }
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        populateDefaultEquipmentSlots(difficulty);
        return super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(difficulty);
        setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        if (effect.getEffect() == Effects.POISON) {
            return level.getDifficulty() != Difficulty.HARD && EntityUtils.shouldApplyEffect(this, effect, false);
        }
        return super.canBeAffected(effect);
    }

    @Nullable
    @Override
    public IShieldUser getShieldProvider() {
        if (getOwner() == null) {
            return null;
        }
        if (distanceToSqr(getOwner()) > Meta08Entity.SHIELD_RANGE * Meta08Entity.SHIELD_RANGE) {
            return null;
        }
        return getOwner();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof RobotEntity && ((RobotEntity) target).isOwnedBy(getOwner())) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public boolean isPowered() {
        return entityData.get(DATA_SHIELDED);
    }

    @Override
    public float getDamageMultiplier() {
        return getOwner() == null ? 1 : getOwner().getDamageMultiplier();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROBOT_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROBOT_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(ModSoundEvents.ROBOT_STEP.get(), 0.15f, 1);
    }
}
