package lych.soullery.entity.passive;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.controller.VoidwalkerMovementController;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.entity.iface.IEtherealable;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.util.ModEffectUtils;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

public class IllusoryHorseEntity extends AbstractHorseEntity implements ESVMob, IEtherealable {
    private static final DataParameter<Boolean> DATA_ETHEREAL = EntityDataManager.defineId(IllusoryHorseEntity.class, DataSerializers.BOOLEAN);
    @Nullable
    private Vector3d sneakTarget;

    public IllusoryHorseEntity(EntityType<? extends IllusoryHorseEntity> type, World world) {
        super(type, world);
        moveControl = new VoidwalkerMovementController<>(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ETHEREAL, false);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 24);
    }

    @Override
    public int getTemper() {
        if (isVoidwalkersNearby()) {
            return 0;
        }
        return super.getTemper();
    }

    @Override
    protected boolean isImmobile() {
        if (ESVMob.isESVMob(getControllingPassenger())) {
            return false;
        }
        return super.isImmobile();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        boolean retameable = isTamed() && getOwnerUUID() == null && isVehicle() && getPassengers().get(0) instanceof PlayerEntity;
        if (retameable && random.nextInt(50) == 0) {
            tameWithName((PlayerEntity) getPassengers().get(0));
        }
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity parent) {
        return ModEntities.ILLUSORY_HORSE.create(world);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getOwnerUUID() == null && isVoidwalkersNearby()) {
            return ActionResultType.PASS;
        }
        if (isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (player.isSecondaryUseActive()) {
            openInventory(player);
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        if (isVehicle()) {
            return super.mobInteract(player, hand);
        }
        if (!stack.isEmpty()) {
            if (stack.getItem() == Items.SADDLE && !isSaddled()) {
                openInventory(player);
                return ActionResultType.sidedSuccess(level.isClientSide());
            }
            ActionResultType type = stack.interactLivingEntity(player, this, hand);
            if (type.consumesAction()) {
                return type;
            }
        }
        doPlayerRide(player);
        return ActionResultType.sidedSuccess(level.isClientSide());
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.1875;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        inventory.setItem(0, new ItemStack(Items.SADDLE));
        return super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
    }

    @Override
    protected void randomizeAttributes() {
        EntityUtils.getAttribute(this, Attributes.JUMP_STRENGTH).setBaseValue(generateRandomJumpStrength());
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        if (ModEffectUtils.isHarmful(effect)) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    private boolean isVoidwalkersNearby() {
        double followRange = getAttributeValue(Attributes.FOLLOW_RANGE);
        return level.getNearestEntity(AbstractVoidwalkerEntity.class, EntityUtils.ALL.get(), this, getX(), getY(), getZ(), getBoundingBox().inflate(followRange)) != null;
    }

    @Override
    public boolean isEthereal() {
        return entityData.get(DATA_ETHEREAL) && (level.isClientSide() || getSneakTarget() != null);
    }

    @Override
    public double getSizeForCalculation() {
        return getBoundingBox().getSize();
    }

    private void setEthereal(boolean ethereal) {
        entityData.set(DATA_ETHEREAL, ethereal);
        noPhysics = ethereal;
        setNoGravity(ethereal);
    }

    @Nullable
    @Override
    public Vector3d getSneakTarget() {
        return sneakTarget;
    }

    @Override
    public boolean setSneakTarget(@Nullable Vector3d sneakTarget) {
        this.sneakTarget = sneakTarget;
        setEthereal(sneakTarget != null);
        if (sneakTarget != null) {
            playSound(ModSoundEvents.ETHEMOVE.get(), 0.5f, 1);
        }
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("Ethereal", isEthereal());
        if (getSneakTarget() != null) {
            Vector3d et = getSneakTarget();
            compoundNBT.put("EtheTarget", newDoubleList(et.x, et.y, et.z));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("EtheTarget", Constants.NBT.TAG_LIST)) {
            ListNBT etn = compoundNBT.getList("EtheTarget", Constants.NBT.TAG_DOUBLE);
            setSneakTarget(new Vector3d(etn.getDouble(0), etn.getDouble(1), etn.getDouble(2)));
        }
        setEthereal(compoundNBT.getBoolean("Ethereal"));
    }
}
