package lych.soullery.entity.monster.voidwalker;

import com.google.common.base.Preconditions;
import lych.soullery.api.shield.ISharedShieldUser;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ai.ModCreatureAttributes;
import lych.soullery.entity.ai.controller.VoidwalkerMovementController;
import lych.soullery.entity.ai.goal.AttackMainTargetGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.FindTargetGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.entity.iface.IEtherealable;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.monster.boss.esv.SoulControllerEntity;
import lych.soullery.entity.passive.IllusoryHorseEntity;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

@OnlyIn(value = Dist.CLIENT, _interface = IChargeableMob.class)
public abstract class AbstractVoidwalkerEntity extends MonsterEntity implements IHasOwner<SoulControllerEntity>, IEtherealable, ISharedShieldUser, IChargeableMob, ESVMob {
    public static final Marker VOIDWALKER = MarkerManager.getMarker("Voidwalker");
    public static final int SHORT_ETHEREAL_COOLDOWN = 20 * 5;
    public static final int LONG_ETHEREAL_COOLDOWN = 20 * 10;
    private static final UUID ILLUSORY_HORSE_SPEED_MODIFIER_UUID = UUID.fromString("974EA2A6-89D2-DA8C-4608-69317DFB960D");
    private static final AttributeModifier ILLUSORY_HORSE_SPEED_MODIFIER = new AttributeModifier(ILLUSORY_HORSE_SPEED_MODIFIER_UUID, "Illusory horse speed modifier", 0.05, AttributeModifier.Operation.ADDITION);
    protected static final DataParameter<Boolean> DATA_SHIELDED = EntityDataManager.defineId(AbstractVoidwalkerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_ETHEREAL = EntityDataManager.defineId(AbstractVoidwalkerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DATA_TIER = EntityDataManager.defineId(AbstractVoidwalkerEntity.class, DataSerializers.INT);
    private GlobalPos lastSafePos;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private UUID mainTarget;
    private int emergencySneakCooldown;
    private int etherealCooldown;
    @Nullable
    private Vector3d sneakTarget;
    private boolean adjustTarget;
    private VoidwalkerTier tier;
    @Nullable
    private VoidwalkerTier strengthenedTo;
    @Nullable
    private UUID shieldProvider;

    protected AbstractVoidwalkerEntity(EntityType<? extends AbstractVoidwalkerEntity> type, World world) {
        super(type, world);
        moveControl = new VoidwalkerMovementController<>(this);
        navigation = new Navigator(this, world);
        Objects.requireNonNull(getBaseTier(), "Base tier should be non-null");
        Objects.requireNonNull(getMaxTier(), "Max tier should be non-null");
        Preconditions.checkState(!getBaseTier().strongerThan(getMaxTier()), "BaseTier must not be stronger than MaxTier");
        entityData.define(DATA_TIER, getBaseTier().getId());
        setTier(getBaseTier(), true);
    }

    @Override
    public boolean startRiding(Entity vehicle, boolean forceRide) {
        boolean succeeded = super.startRiding(vehicle, forceRide);
        if (succeeded && vehicle instanceof IllusoryHorseEntity && isMeleeAttacker()) {
            IllusoryHorseEntity horse = (IllusoryHorseEntity) vehicle;
            if (!EntityUtils.getAttribute(horse, Attributes.MOVEMENT_SPEED).hasModifier(ILLUSORY_HORSE_SPEED_MODIFIER)) {
                EntityUtils.getAttribute(horse, Attributes.MOVEMENT_SPEED).addTransientModifier(ILLUSORY_HORSE_SPEED_MODIFIER);
            }
        }
        return succeeded;
    }

    @Override
    public void removeVehicle() {
        if (getVehicle() instanceof IllusoryHorseEntity && isMeleeAttacker()) {
            IllusoryHorseEntity horse = (IllusoryHorseEntity) getVehicle();
            EntityUtils.getAttribute(horse, Attributes.MOVEMENT_SPEED).removeModifier(ILLUSORY_HORSE_SPEED_MODIFIER);
        }
        super.removeVehicle();
    }

    public boolean isPrimary() {
        return true;
    }

    public abstract boolean isMeleeAttacker();

    public abstract boolean canCreateWeapon();

    public abstract ItemStack createWeapon();

    public static AttributeModifierMap.MutableAttribute createVoidwalkerAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.ATTACK_DAMAGE, 4)
                .add(Attributes.FOLLOW_RANGE, 24);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ETHEREAL, false);
        entityData.define(DATA_SHIELDED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this, ESVMob.class).setAlertOthers(SoulControllerEntity.class));
        targetSelector.addGoal(2, new AttackMainTargetGoal(this, false, this::getMainTargetAsEntity));
        targetSelector.addGoal(3, new FindTargetGoal<>(this, PlayerEntity.class, false));
        targetSelector.addGoal(4, new FindTargetGoal<>(this, IronGolemEntity.class, false));
        targetSelector.addGoal(5, Goals.of(new FindTargetGoal<>(this, MobEntity.class, 20, false, false, ESVMob::nonESVMob)).executeIf(this::canAttackAllMobs).get());
    }

    public boolean canAttack() {
        return true;
    }

    public EntityPredicate customizeTargetConditions(EntityPredicate targetConditions) {
        return targetConditions.allowUnseeable();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        syncShield();
    }

    protected void syncShield() {
        entityData.set(DATA_SHIELDED, getSharedShield() != null);
    }

    @Override
    public void tick() {
        super.tick();
        if (isOnGround() && level.getBlockState(blockPosition().below()).getMaterial().isSolidBlocking()) {
            lastSafePos = GlobalPos.of(level.dimension(), blockPosition());
        }
        if (getEtherealCooldown() > 0) {
            setEtherealCooldown(getEtherealCooldown() - 1);
        }
        if (emergencySneakCooldown > 0) {
            emergencySneakCooldown--;
        }
        if (emergencySneakCooldown == 0 && getActualFallDistance() > 6 && isInVoid()) {
            BlockPos pos = findEmergencySneakPos();
            if (pos != null) {
                doEmergencySneak(pos);
                emergencySneakCooldown = 40;
            }
        }
        if (!level.isClientSide() && getSneakTarget() == null && isEtherealClientSide()) {
            setEthereal(false);
        }
    }

    @Override
    public boolean isPowered() {
        return entityData.get(DATA_SHIELDED);
    }

    public double getActualFallDistance() {
        return getRootVehicle().fallDistance;
    }

    @SuppressWarnings("deprecation")
    protected boolean isInVoid() {
        BlockPos.Mutable pos = blockPosition().mutable();
        for (int i = 0; i < 10; i++) {
            pos.move(Direction.DOWN);
            if (!level.getBlockState(pos).isAir()) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    protected BlockPos findEmergencySneakPos() {
        BlockPos safePos;
        for (int i = 0; i < 10; i++) {
            int searchRadius = 4 + i * 2;
            int x = blockPosition().getX() - searchRadius + random.nextInt(searchRadius * 2 + 1);
            int z = blockPosition().getZ() - searchRadius + random.nextInt(searchRadius * 2 + 1);
            int y = level.getHeight(Heightmap.Type.WORLD_SURFACE, x, z);
            safePos = new BlockPos(x, y, z);
            if (y > 0 && World.isInWorldBounds(safePos)) {
                BlockState state = level.getBlockState(safePos.below());
                if (state.isAir()) {
                    continue;
                }
                if (level.getBlockState(safePos).isAir() && level.getBlockState(safePos.above()).isAir()) {
                    return safePos;
                }
            }
        }
        return loadLastSafePos();
    }

    @Nullable
    protected BlockPos loadLastSafePos() {
        return lastSafePos != null && lastSafePos.dimension() == level.dimension() && stillValid(lastSafePos.pos()) ? lastSafePos.pos() : null;
    }

    protected boolean stillValid(BlockPos pos) {
        return distanceToSqr(Vector3d.atBottomCenterOf(pos)) <= 24 * 24 && level.getBlockState(pos.below()).getMaterial().isSolidBlocking();
    }

    protected void doEmergencySneak(BlockPos pos) {
        setMoveControlSpeedModifier(1);
        forceSneakTo(offsetForVehicle(Vector3d.atCenterOf(pos)));
    }

    protected void forceSneakTo(Vector3d sneakTarget) {
        if (!(getVehicle() instanceof IEtherealable)) {
            stopRiding();
        }
        int oldCooldown = getEtherealCooldown();
        setEtherealCooldown(0);
        setSneakTarget(sneakTarget);
        setEtherealCooldown(oldCooldown);
    }

    @Nullable
    @Override
    public IShieldUser getShieldProvider() {
        if (level.isClientSide()) {
            return null;
        }
        Entity entity = ((ServerWorld) level).getEntity(shieldProvider);
        return entity instanceof IShieldUser ? (IShieldUser) entity : null;
    }

    public void setShieldProvider(@Nullable IShieldUser shieldProvider) {
        if (shieldProvider != null && !(shieldProvider instanceof Entity)) {
            throw new IllegalArgumentException("ShieldProvider must be an entity");
        }
        this.shieldProvider = Utils.applyIfNonnull(shieldProvider, sp -> ((Entity) sp).getUUID());
    }

    protected Vector3d offsetForVehicle(Vector3d pos) {
        return offsetForVehicle(pos, getVehicle());
    }

    protected Vector3d offsetForVehicle(Vector3d pos, @Nullable Entity vehicle) {
        double yOffs = 0;
        if (vehicle != null) {
            yOffs = vehicle.getPassengersRidingOffset() + getMyRidingOffset();
        }
        return pos.add(0, yOffs, 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void floatToAir(BlockPos start) {
        while (!level.getBlockState(start).isAir() || !level.getBlockState(start.above()).isAir()) {
            start = start.above();
        }
        setMoveControlSpeedModifier(1);
        forceSneakTo(offsetForVehicle(Vector3d.atCenterOf(start).add(0, 1, 0)));
    }

    public abstract boolean onSetTarget(LivingEntity target);

    protected void onSetSneakTarget(Vector3d sneakTarget) {}

    @Override
    public double getMyRidingOffset() {
        return -0.3;
    }

    public void setAdjustTarget(boolean adjustTarget) {
        if (getTarget() != null) {
            this.adjustTarget = adjustTarget;
        }
    }

    public boolean shouldAdjustTarget() {
        return adjustTarget;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL) {
            floatToAir(blockPosition());
        }
        if (!source.isBypassMagic() && !source.isBypassInvul()) {
            if (isProtected()) {
                amount *= VoidDefenderEntity.PROTECTED_DAMAGE_MULTIPLIER;
            }
            if (isMagicallyProtected() && source.isMagic()) {
                amount *= VoidAlchemistEntity.PROTECTED_DAMAGE_MULTIPLIER;
            }
        }
        return super.hurt(source, amount);
    }

    public boolean isProtected() {
        if (hasProperVehicle()) {
            return true;
        }
        VoidDefenderEntity nearbyDefender = level.getNearestEntity(VoidDefenderEntity.class, EntityUtils.ALL, this, getX(), getY(), getZ(), getBoundingBox().inflate(VoidDefenderEntity.PROTECTIVE_RANGE));
        if (nearbyDefender != null) {
            return distanceToSqr(nearbyDefender) <= VoidDefenderEntity.PROTECTIVE_RANGE * VoidDefenderEntity.PROTECTIVE_RANGE;
        }
        return false;
    }

    public boolean isMagicallyProtected() {
        VoidAlchemistEntity nearbyAlchemist = level.getNearestEntity(VoidAlchemistEntity.class, EntityUtils.ALL, this, getX(), getY(), getZ(), getBoundingBox().inflate(VoidAlchemistEntity.PROTECTIVE_RANGE));
        if (nearbyAlchemist != null) {
            return distanceToSqr(nearbyAlchemist) <= VoidDefenderEntity.PROTECTIVE_RANGE * VoidDefenderEntity.PROTECTIVE_RANGE;
        }
        return false;
    }

    protected boolean hasProperVehicle() {
        return getVehicle() instanceof IllusoryHorseEntity;
    }

    public boolean isHealable() {
        return getHealth() < getMaxHealth();
    }

    public boolean canHeal(AbstractVoidwalkerEntity voidwalker) {
        return voidwalker.isHealable();
    }

    public boolean isLowHealth() {
        return isLowHealth(this);
    }

    public boolean canAttackAllMobs() {
        return !isLowHealth();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof ESVMob) {
            return getTeam() == entity.getTeam();
        }
        return super.isAlliedTo(entity);
    }

    public boolean isLowHealth(LivingEntity entity) {
        return entity.getHealth() < entity.getMaxHealth() * 0.25f;
    }

    @Nullable
    public <T extends AbstractVoidwalkerEntity> T getNearestVoidwalker(Class<T> type, double range) {
        return getNearestVoidwalker(type, range, v -> true);
    }

    @Nullable
    public <T extends AbstractVoidwalkerEntity> T getNearestVoidwalker(Class<T> type, double range, Predicate<? super T> predicate) {
        List<T> voidwalkers = getNearbyVoidwalkers(type, range, predicate);
        return voidwalkers.stream().min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
    }

    public <T extends AbstractVoidwalkerEntity> List<T> getNearbyVoidwalkers(Class<T> type, double range) {
        return getNearbyVoidwalkers(type, range, v -> true);
    }

    public <T extends AbstractVoidwalkerEntity> List<T> getNearbyVoidwalkers(Class<T> type, double range, Predicate<? super T> predicate) {
        List<T> voidwalkers = level.getEntitiesOfClass(type, getBoundingBox().inflate(range), predicate);
        voidwalkers.removeIf(voidwalker -> voidwalker == this || !voidwalker.isAlive() || distanceToSqr(voidwalker) > range * range);
        return voidwalkers;
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

    public boolean canBeEtherealToAttack() {
        if (getVehicle() != null && !(getVehicle() instanceof IEtherealable)) {
            return false;
        }
        return getEtherealCooldown() == 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        saveOwner(compoundNBT);
        compoundNBT.putBoolean("Ethereal", isEthereal());
        if (lastSafePos != null) {
            compoundNBT.putString("LastSafePosDim", lastSafePos.dimension().location().toString());
            compoundNBT.put("LastSafePos", NBTUtil.writeBlockPos(lastSafePos.pos()));
        }
        compoundNBT.putInt("EtherealCooldown", getEtherealCooldown());
        compoundNBT.putInt("EmergencySneakCooldown", emergencySneakCooldown);
        if (getSneakTarget() != null) {
            Vector3d et = getSneakTarget();
            compoundNBT.put("EtheTarget", newDoubleList(et.x, et.y, et.z));
        }
        if (getMainTarget() != null) {
            compoundNBT.putUUID("MainTarget", getMainTarget());
        }
        compoundNBT.putInt("Tier", getTier().getId());
        if (strengthenedTo != null) {
            compoundNBT.putInt("StrengthenedTo", strengthenedTo.getId());
        }
        if (getShieldProvider() != null && getShieldProvider() != this) {
            compoundNBT.putUUID("ShieldProvider", ((Entity) getShieldProvider()).getUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        loadOwner(compoundNBT);
        if (compoundNBT.contains("LastSafePosDim") && compoundNBT.contains("LastSafePos")) {
            ResourceLocation location = new ResourceLocation(compoundNBT.getString("LastSafePosDim"));
            lastSafePos = GlobalPos.of(RegistryKey.create(Registry.DIMENSION_REGISTRY, location), NBTUtil.readBlockPos(compoundNBT.getCompound("LastSafePos")));
        }
        setEtherealCooldown(compoundNBT.getInt("EtherealCooldown"));
        emergencySneakCooldown = compoundNBT.getInt("EmergencySneakCooldown");
        if (compoundNBT.contains("EtheTarget", Constants.NBT.TAG_LIST)) {
            ListNBT etn = compoundNBT.getList("EtheTarget", Constants.NBT.TAG_DOUBLE);
            setSneakTarget(new Vector3d(etn.getDouble(0), etn.getDouble(1), etn.getDouble(2)), false);
        }
        setEthereal(compoundNBT.getBoolean("Ethereal"));
        if (compoundNBT.contains("MainTarget")) {
            setMainTarget(compoundNBT.getUUID("MainTarget"));
        }
        if (compoundNBT.contains("Tier")) {
            setTier(VoidwalkerTier.byId(compoundNBT.getInt("Tier"), false), true);
        }
        if (compoundNBT.contains("StrengthenedTo")) {
            strengthenedTo = VoidwalkerTier.byId(compoundNBT.getInt("StrengthenedTo"));
        }
        if (compoundNBT.contains("ShieldProvider") && !level.isClientSide()) {
            shieldProvider = compoundNBT.getUUID("ShieldProvider");
        }
    }

    @Override
    public boolean isEthereal() {
        return isEtherealClientSide() && (level.isClientSide() || getSneakTarget() != null);
    }

    private boolean isEtherealClientSide() {
        return entityData.get(DATA_ETHEREAL);
    }

    private void setEthereal(boolean ethereal) {
        entityData.set(DATA_ETHEREAL, ethereal);
        noPhysics = ethereal;
        setNoGravity(ethereal);
    }

    @Override
    @Nullable
    public Vector3d getSneakTarget() {
        return sneakTarget;
    }

    @Override
    public boolean setSneakTarget(@Nullable Vector3d sneakTarget) {
        return setSneakTarget(sneakTarget, true);
    }

    @Override
    public double getSizeForCalculation() {
        double multiplier = isPassenger() ? 1.3 : 1;
        return getBoundingBox().getSize() * multiplier;
    }

    protected boolean setSneakTarget(@Nullable Vector3d sneakTarget, boolean onSet) {
        if (!canBeEtherealToAttack() && sneakTarget != null) {
            return false;
        }
        boolean usedToBeNull = this.sneakTarget == null;
        this.sneakTarget = sneakTarget;
        setEthereal(sneakTarget != null);
        if (sneakTarget != null && onSet && usedToBeNull) {
            onSetSneakTarget(sneakTarget);
        }
        if (getVehicle() instanceof IEtherealable) {
            ((IEtherealable) getVehicle()).setSneakTarget(sneakTarget);
        }
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (isEthereal()) {
            if (source == DamageSource.IN_WALL || !source.isMagic()) {
                return !source.isBypassInvul();
            }
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isInAir() {
        return level.getBlockState(blockPosition()).isAir();
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier) {
        return false;
    }

    public int getEtherealCooldown() {
        return etherealCooldown;
    }

    public void setEtherealCooldown(int etherealCooldown) {
        this.etherealCooldown = etherealCooldown;
    }

    public boolean isCastingSpell() {
        return false;
    }

    @Override
    public CreatureAttribute getMobType() {
        return ModCreatureAttributes.VOIDWALKER;
    }

    @Nullable
    public LivingEntity getMainTargetAsEntity() {
        if (level.isClientSide() || getMainTarget() == null) {
            return null;
        }
        Entity entity = ((ServerWorld) level).getEntity(getMainTarget());
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    @Nullable
    public UUID getMainTarget() {
        return mainTarget;
    }

    public void setMainTarget(@Nullable UUID mainTarget) {
        this.mainTarget = mainTarget;
    }

    public void setMoveControlSpeedModifier(double speedModifier) {
        MovementController controller = getMoveControl();
        if (controller instanceof VoidwalkerMovementController) {
            ((VoidwalkerMovementController<?>) controller).setSpeedModifier(speedModifier);
        }
    }

    public VoidwalkerTier getBaseTier() {
        return VoidwalkerTier.ORDINARY;
    }

    public VoidwalkerTier getMaxTier() {
        return VoidwalkerTier.PARAGON;
    }

    public void doHealTarget(AbstractVoidwalkerEntity healTarget) {}

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        data = super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
        populateDefaultEquipmentSlots(difficulty);
        populateDefaultEquipmentEnchantments(difficulty);
        strengthenSelf(getTier(), getTier(), difficulty);
        return data;
    }

    public final boolean strengthenSelf(VoidwalkerTier tier, VoidwalkerTier oldTier, DifficultyInstance difficulty) {
        if (strengthenedTo != null && !strengthenedTo.weakerThan(tier)) {
            return false;
        }
        if (tier.weakerThan(oldTier)) {
            if (ConfigHelper.shouldFailhard()) {
                throw new IllegalArgumentException(ConfigHelper.FAILHARD_MESSAGE + String.format("The method must not be used to weaken self. OldTier is %s, but newTier is %s", oldTier, tier));
            } else {
                return false;
            }
        }
        doStrengthenSelf(tier, oldTier, difficulty);
        strengthenedTo = tier;
        return true;
    }

    protected void doStrengthenSelf(VoidwalkerTier tier, VoidwalkerTier oldTier, DifficultyInstance difficulty) {
        strengthenSelfByDefault(tier);
    }

    protected void strengthenSelfByDefault(VoidwalkerTier tier) {
        switch (tier) {
            case PARAGON:
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(10);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(48);
                break;
            case ELITE:
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(70);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(4);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(36);
                break;
            case EXTRAORDINARY:
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(45);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30);
                break;
            default:
        }
        setHealth(getMaxHealth());
    }

    protected ModifiableAttributeInstance getNonnullAttribute(Attribute attribute) {
        return EntityUtils.getAttribute(this, attribute);
    }

    public boolean upgrade() {
        if (tier.isInRange(getBaseTier(), getMaxTier())) {
            forceUpgrade();
            return true;
        }
        return false;
    }

    protected void forceUpgrade() {
        setTier(tier.upgraded());
    }

    public VoidwalkerTier getTier() {
        if (level.isClientSide()) {
            return VoidwalkerTier.byId(entityData.get(DATA_TIER));
        }
        return tier;
    }

    public void setTier(VoidwalkerTier tier) {
        setTier(tier, false);
    }

    protected void setTier(VoidwalkerTier tier, boolean initialSet) {
        Objects.requireNonNull(tier, "Tier cannot be null");
        Preconditions.checkArgument(tier.isInRangeClosed(getBaseTier(), getMaxTier()), String.format("Tier %s is not in range [%s, %s]", tier, getBaseTier(), getMaxTier()));
        VoidwalkerTier oldTier = getTier();
        this.tier = tier;
        entityData.set(DATA_TIER, tier.getId());
        if (!level.isClientSide() && !initialSet && tier.strongerThan(oldTier)) {
            strengthenSelf(tier, oldTier, level.getCurrentDifficultyAt(blockPosition()));
        }
    }

    public static void updateBodyAngles(double tx, double ty, double tz, LivingEntity entity, RotlerpFunction function) {
        float yRot = (float) (MathHelper.atan2(tz, tx) * 180 / Math.PI) - 90;
        float xRot = (float) -(MathHelper.atan2(ty, MathHelper.sqrt(tx * tx + tz * tz)) * 180 / Math.PI);
        entity.yRot = function.rotlerp(entity.yRot, yRot, 90);
        entity.xRot = function.rotlerp(entity.xRot, xRot, 90);
        entity.yBodyRot = function.rotlerp(entity.yBodyRot, yRot, 90);
    }

    public interface RotlerpFunction {
        float rotlerp(float oldRot, float newRot, int amount);
    }

    protected static class Navigator extends GroundPathNavigator {
        public Navigator(MobEntity mob, World world) {
            super(mob, world);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void tick() {
            tick++;
            if (hasDelayedRecomputation) {
                recomputePath();
            }
            if (!isDone()) {
                if (canUpdatePath()) {
                    followThePath();
                } else if (path != null && !path.isDone()) {
                    Vector3d pos = getTempMobPos();
                    Vector3d next = getNextEntityPos();
                    if (pos.y > next.y && !mob.isOnGround() && MathHelper.floor(pos.x) == MathHelper.floor(next.x) && MathHelper.floor(pos.z) == MathHelper.floor(next.z)) {
                        path.advance();
                    }
                }
                DebugPacketSender.sendPathFindingPacket(level, mob, path, maxDistanceToWaypoint);
                if (!isDone()) {
                    Vector3d next = getNextEntityPos();
                    BlockPos nextBlock = new BlockPos(next);
                    mob.getMoveControl().setWantedPosition(next.x, level.getBlockState(nextBlock.below()).isAir() ? next.y : WalkNodeProcessor.getFloorLevel(level, nextBlock), next.z, speedModifier);
                }
            }
        }

        @NotNull
        private Vector3d getNextEntityPos() {
            Objects.requireNonNull(path);
            PathPoint point = path.getNode(path.getNextNodeIndex());
            double x = point.x + (int) (getBbWidth() + 1) * 0.5;
            double y = point.y;
            double z = point.z + (int) (getBbWidth() + 1) * 0.5;
            return new Vector3d(x, y, z);
        }

        private float getBbWidth() {
            if (mob.getVehicle() instanceof IllusoryHorseEntity) {
                return mob.getVehicle().getBbWidth();
            }
            return mob.getBbWidth();
        }
    }
}
