package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public class EnchantedArmorStandEntity extends MonsterEntity implements IRangedAttackMob, IHasOwner<EnchanterEntity> {
    public static final double HEALTH = 5;
    public static final double DAMAGE = 2;
    private static final int ATTACK_GOAL_PRIORITY = 2;
    private static final int MAX_SPAWN_INVUL_TICKS = 20;
    private static final DataParameter<Integer> DATA_SPECIAL_TYPE = EntityDataManager.defineId(EnchantedArmorStandEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> DATA_SPAWN_INVUL_TICKS = EntityDataManager.defineId(EnchantedArmorStandEntity.class, DataSerializers.INT);
    private final Deque<ItemStack> itemsCarried = new ArrayDeque<>();
    private Goal attackGoal;
    private boolean rangedAttack;
    private EASType specialType;
    @Nullable
    private UUID ownerUUID;

    public EnchantedArmorStandEntity(EntityType<? extends EnchantedArmorStandEntity> type, World world) {
        super(type, world);
        xpReward = 0;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 20);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SPECIAL_TYPE, -1);
        entityData.define(DATA_SPAWN_INVUL_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        attackGoal = meleeGoal();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(ATTACK_GOAL_PRIORITY, attackGoal);
        goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.95, 0.0005f));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, EnchantedArmorStandEntity.class, EnchanterEntity.class).setAlertOthers(EnchanterEntity.class));
        targetSelector.addGoal(2, new CopyOwnerTargetGoal<>(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    private MeleeAttackGoal meleeGoal() {
        return new MeleeAttackGoal(this, 1, true);
    }

    private RangedAttackGoal rangedGoal() {
        return new RangedAttackGoal(this, 1, 40, 14);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ENCHANTED_ARMOR_STAND_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENCHANTED_ARMOR_STAND_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ENCHANTED_ARMOR_STAND_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(ModSoundEvents.ENCHANTED_ARMOR_STAND_STEP.get(), 0.15f, 1);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (getSpecialType() != null && target instanceof LivingEntity) {
            getSpecialType().onEASAttack(this, (LivingEntity) target);
        }
        return super.doHurtTarget(target);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        int spawnInvulTicks = getSpawnInvulTicks();
        if (spawnInvulTicks > 0) {
            setSpawnInvulTicks(spawnInvulTicks - 1);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getSpawnInvulTicks() > 0) {
            return false;
        }
        boolean hurt = super.hurt(source, amount);
        if (getSpecialType() != null && hurt) {
            getSpecialType().onEASHurt(this, source, amount);
        }
        return hurt;
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, source.getEntity() instanceof EnchanterEntity || source.getEntity() instanceof EnchantedArmorStandEntity ? amount * 0.1f : amount);
    }

    public boolean isMelee() {
        return !rangedAttack;
    }

    public void setRangedAttack(boolean rangedAttack) {
        if (this.rangedAttack == rangedAttack) {
            return;
        }
        this.rangedAttack = rangedAttack;
        goalSelector.removeGoal(attackGoal);
        if (rangedAttack) {
            attackGoal = rangedGoal();
        } else {
            attackGoal = meleeGoal();
        }
        goalSelector.addGoal(ATTACK_GOAL_PRIORITY, attackGoal);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        AbstractArrowEntity arrow = new ArrowEntity(level, this);
        arrow.setPos(arrow.getX(), getY(0.9), arrow.getZ());
        double tx = target.getX() - getX();
        double ty = target.getY(0.3333333333333333) - arrow.getY();
        double tz = target.getZ() - getZ();
        double dis = Math.sqrt(tx * tx + tz * tz);
        arrow.shoot(tx, ty + dis * 0.2, tz, 1.7f, 18 - level.getDifficulty().getId() * 4);
        level.addFreshEntity(arrow);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (getSpecialType() != null) {
            getSpecialType().onEASDie(this, source);
        }
        EnchanterEntity enchanter = getOwner();
        if (enchanter != null && getKillCredit() instanceof ServerPlayerEntity) {
            enchanter.addKill((ServerPlayerEntity) getKillCredit());
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int lootingLevel, boolean killedByPlayer) {
        super.dropCustomDeathLoot(source, lootingLevel, killedByPlayer);
        if (getSpecialType() != null && killedByPlayer && getSpecialType().hasDrop() && random.nextInt(Math.max(1, 8 - lootingLevel)) == 0) {
            spawnAtLocation(getSpecialType().getRepresentation());
        }
        itemsCarried.forEach(this::spawnAtLocation);
    }

    public int getSpawnInvulTicks() {
        return entityData.get(DATA_SPAWN_INVUL_TICKS);
    }

    @OnlyIn(Dist.CLIENT)
    public float getSpawnPercent(float partialTicks) {
        return (MAX_SPAWN_INVUL_TICKS - getSpawnInvulTicks() + partialTicks) / MAX_SPAWN_INVUL_TICKS;
    }

    public void setSpawnInvul() {
        setSpawnInvulTicks(MAX_SPAWN_INVUL_TICKS);
    }

    private void setSpawnInvulTicks(int spawnInvulTicks) {
        entityData.set(DATA_SPAWN_INVUL_TICKS, Math.min(spawnInvulTicks, MAX_SPAWN_INVUL_TICKS));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("RangedAttack", rangedAttack);
        if (getSpecialType() != null) {
            compoundNBT.putString("SpecialType", getSpecialType().getName().toString());
        }
        ListNBT itemsNBT = new ListNBT();
        for (ItemStack stack : itemsCarried) {
            itemsNBT.add(stack.save(new CompoundNBT()));
        }
        compoundNBT.put("ItemsCarried", itemsNBT);
        saveOwner(compoundNBT);
        compoundNBT.putInt("SpawnInvulTicks", getSpawnInvulTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("RangedAttack")) {
            setRangedAttack(compoundNBT.getBoolean("RangedAttack"));
        }
        if (compoundNBT.contains("SpecialType")) {
            String name = compoundNBT.getString("SpecialType");
            @Nullable
            ResourceLocation location;
            try {
                location = new ResourceLocation(name);
            } catch (ResourceLocationException e) {
                LOGGER.warn("EASType is not loaded because of invalid ResourceLocation", e);
                location = null;
            }
            if (location != null) {
                EASType type = EASTypes.get(location);
                if (type == null) {
                    LOGGER.warn("Invalid EASType: {}", location);
                } else {
                    setSpecialType(type, false);
                }
            }
        }
        if (compoundNBT.contains("ItemsCarried", Constants.NBT.TAG_LIST)) {
            ListNBT itemsNBT = compoundNBT.getList("ItemsCarried", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemsNBT.size(); i++) {
                CompoundNBT itemNBT = itemsNBT.getCompound(i);
                carry(ItemStack.of(itemNBT));
            }
        }
        loadOwner(compoundNBT);
        if (compoundNBT.contains("SpawnInvulTicks", Constants.NBT.TAG_INT)) {
            setSpawnInvulTicks(compoundNBT.getInt("SpawnInvulTicks"));
        }
    }

    @Nullable
    public EASType getSpecialType() {
        if (level.isClientSide()) {
            return EASTypes.byId(entityData.get(DATA_SPECIAL_TYPE));
        }
        return specialType;
    }

    public void setSpecialType(@Nullable EASType specialType) {
        setSpecialType(specialType, true);
    }

    private void setSpecialType(@Nullable EASType specialType, boolean update) {
        if (!level.isClientSide()) {
            if (update && this.specialType != null) {
                this.specialType.stopApplyingTo(this);
            }
            this.specialType = specialType;
            if (update && this.specialType != null) {
                this.specialType.startApplyingTo(this);
            }
        }
        entityData.set(DATA_SPECIAL_TYPE, specialType == null ? -1 : specialType.getId());
    }

    public boolean carry(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        itemsCarried.addFirst(stack);
        return true;
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
}
