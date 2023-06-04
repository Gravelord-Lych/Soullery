package lych.soullery.entity.monster;

import lych.soullery.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soullery.entity.ai.goal.FollowOwnerGoal;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.monster.boss.SoulSkeletonKingEntity;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.extension.fire.Fires;
import lych.soullery.item.ModItems;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.util.mixin.IEntityMixin;
import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.sll.SLLayer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SoulSkeletonEntity extends AbstractSkeletonEntity implements IHasOwner<SoulSkeletonKingEntity>, IPurifiable {
    private static final DataParameter<Boolean> DATA_PURIFIED = EntityDataManager.defineId(SoulSkeletonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_CLIMBING = EntityDataManager.defineId(SoulSkeletonEntity.class, DataSerializers.BOOLEAN);
    private UUID ownerUUID;
    private boolean climbable;

    public SoulSkeletonEntity(EntityType<? extends SoulSkeletonEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PURIFIED, false);
        entityData.define(DATA_CLIMBING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new RestrictSunGoal(this));
        goalSelector.addGoal(3, new FleeSunGoal(this, 1));
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, WolfEntity.class, 6, 1, 1.2));
        goalSelector.addGoal(4, new FollowOwnerGoal<>(this, 1, 10, 4, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(6, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, SoulSkeletonKingEntity.class, SoulSkeletonEntity.class, SoulDragonEntity.class));
        targetSelector.addGoal(2, new CopyOwnerTargetGoal<>(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            return super.canAttack(entity) && !ExtraAbility.INTIMIDATOR.isOn((PlayerEntity) entity);
        }
        return super.canAttack(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide() && isClimbable()) {
            setClimbing(horizontalCollision);
        }
    }

    @Override
    public boolean isPurified() {
        return entityData.get(DATA_PURIFIED);
    }

    @Override
    public void setPurified(boolean purified) {
        entityData.set(DATA_PURIFIED, purified);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractSkeletonEntity.createAttributes();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.SOUL_SKELETON_AMBIENT.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSoundEvents.SOUL_SKELETON_STEP.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.SOUL_SKELETON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_SKELETON_DEATH.get();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (!target.fireImmune()) {
                target.setSecondsOnFire(5);
                ((IEntityMixin) target).setFireOnSelf(isPurified() ? Fires.PURE_SOUL_FIRE : Fires.SOUL_FIRE);
            }
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        if (world.getBiomeName(blockPosition()).map(ModBiomes::getId).map(SLLayer::isPure).orElse(false)) {
            setPurified(true);
        }
        return super.finalizeSpawn(world, instance, reason, data, compoundNBT);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance instance) {
        setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(random.nextBoolean() ? ModItems.REFINED_SOUL_METAL_SWORD : ModItems.SOUL_BOW));
    }

    @Override
    protected AbstractArrowEntity getArrow(ItemStack stack, float power) {
        AbstractArrowEntity arrow = ModItems.SOUL_ARROW.createArrow(level, stack, this);
        arrow.setEnchantmentEffectsFromEntity(this, power);
        if (stack.getItem() == Items.TIPPED_ARROW && arrow instanceof ArrowEntity) {
            ((ArrowEntity) arrow).setEffectsFromItem(stack);
        }
        arrow.setSecondsOnFire(100);
        ((IEntityMixin) arrow).setFireOnSelf(isPurified() ? Fires.PURE_SOUL_FIRE : Fires.SOUL_FIRE);
        return arrow;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(sound == SoundEvents.SKELETON_SHOOT ? ModSoundEvents.SOUL_SKELETON_SHOOT.get() : sound, volume, pitch);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        saveOwner(compoundNBT);
        compoundNBT.putBoolean("Purified", isPurified());
        compoundNBT.putBoolean("Climbable", isClimbable());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        loadOwner(compoundNBT);
        setPurified(compoundNBT.getBoolean("Purified"));
        if (compoundNBT.contains("Climbable")) {
            setClimbable(compoundNBT.getBoolean("Climbable"));
        }
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
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

    public boolean isClimbable() {
        return climbable;
    }

    public void setClimbable(boolean climbable) {
        this.climbable = climbable;
        navigation = climbable ? new ClimberPathNavigator(this, level) : createNavigation(level);
    }

    @Override
    public boolean onClimbable() {
        return isClimbing();
    }

    public boolean isClimbing() {
        return entityData.get(DATA_CLIMBING);
    }

    public void setClimbing(boolean climbing) {
        entityData.set(DATA_CLIMBING, climbing);
    }
}
