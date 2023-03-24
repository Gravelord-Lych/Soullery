package lych.soullery.entity.monster.boss;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.controller.LockableLookController;
import lych.soullery.entity.ai.goal.boss.GiantXGoals.JumpAttackGoal;
import lych.soullery.entity.ai.goal.boss.GiantXGoals.RushAttackGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.ai.phase.PhaseManager;
import lych.soullery.entity.iface.ITieredBoss;
import lych.soullery.entity.iface.ITieredMob;
import lych.soullery.entity.monster.SubZombieEntity;
import lych.soullery.util.BoundingBoxUtils;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.IIdentifiableEnum;
import lych.soullery.util.Vectors;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Giant X, a {@link GiantEntity giant} which has AI, is a powerful boss that uses his
 * huge and strong body to knockback and damage his targets (Even Iron Golems can be knockbacked
 * slightly because the {@link LivingEntity#knockback(float, double, double) knockback} method is
 * not used) Giant X has the same armor as {@link ZombieEntity#createAttributes() zombies'}.
 * However, he has much more health than the other zombies.<br>
 * Zombies will be neutral to a player if the player killed Giant X.<br>
 * <br>
 * Abilities (T1):
 * <li><b>Rush Attack: </b>Giant X rushes to his target and damages the entities which intersects him.</li>
 * <li><b>Jump Attack: </b>Giant jumps and damages nearby entities. Will only jump if his target is nearby.</li>
 * <br>
 * Giant X's damage increases every tier. Other abilities gained after tier 1:
 * <li><b>Tier 2: </b>Giant X rushes faster. </li>
 * <li><b>Tier 3: </b>Giant X has higher health and higher knockback power. </li>
 * <li><b>Tier 4: </b>Giant X knockback resistance increases. </li>
 * <li><b>Tier 5: </b>Giant X can damage entities in a larger radius. </li>
 * <li><b>Tier 6: </b>Giant X rushes even faster. </li>
 * <li><b>Tier 7: </b>Entities which are damaged by Giant X will be weak. Stacks 2 times. </li>
 * <li><b>Tier 8: </b>Giant X's damage radius and knockback power increases again. </li>
 * <li><b>Tier 9: </b>Giant X's health increases and he can NEVER be knockbacked again. </li>
 * <li><b>Tier 10: </b>Giant X summons zombie when he is damaged. Entities which are damaged by
 *                     Giant X will be weak nearly PERMANENTLY and weakness stacks 5 times. </li>
 * <li><b>Tier 11+: </b>Giant X's strength will improve per level.</li>
 *
 * @author Gravelord Lych
 */
public class GiantXEntity extends ZombieEntity implements ITieredBoss {
    private static final double MAX_HEALTH = 300;
    private static final double MAX_HEALTH_T3 = 375;
    private static final double MAX_HEALTH_T9 = 500;
    private static final double HEALTH_STEP = 20;
    private static final double MAX_MAX_HEALTH = 1000;
    private static final Int2DoubleMap HEALTH_MAP = EntityUtils.doubleChoiceBuilder().range(1, 2).value(MAX_HEALTH).range(3, 8).value(MAX_HEALTH_T3).range(9).value(MAX_HEALTH_T9).build();

    private static final double KNOCKBACK_RESISTANCE = 0.5;
    private static final double KNOCKBACK_RESISTANCE_T4 = 0.75;
    private static final double KNOCKBACK_RESISTANCE_T9_OR_ABOVE = 1;
    private static final Int2DoubleMap KNOCKBACK_RESISTANCE_MAP = EntityUtils.doubleChoiceBuilder().range(1, 3).value(KNOCKBACK_RESISTANCE).range(4, 8).value(KNOCKBACK_RESISTANCE_T4).range(9).value(KNOCKBACK_RESISTANCE_T9_OR_ABOVE).build();

//  T1's xp reward is 220, T2's is 242 and so on.
    private static final int BASE_XP_REWARD = 200;
    private static final double XP_MULTIPLIER = 1.1;
    private static final int MAX_XP_REWARD = 100000;

    private static final double RUSH_SPEED_MODIFIER = 2;
    private static final double RUSH_SPEED_MODIFIER_T2 = 2.3;
    private static final double RUSH_SPEED_MODIFIER_T6 = 2.6;
    private static final double RUSH_SPEED_MODIFIER_T10 = 3;
    private static final double RUSH_SPEED_MODIFIER_STEP = 0.1;
    private static final double MAX_RUSH_SPEED_MODIFIER = 4;
    private static final Int2DoubleMap RUSH_SPEED_MODIFIER_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(RUSH_SPEED_MODIFIER).range(2, 5).value(RUSH_SPEED_MODIFIER_T2).range(6, 9).value(RUSH_SPEED_MODIFIER_T6).build();

    private static final double KNOCKBACK_POWER = 2;
    private static final double KNOCKBACK_POWER_T3 = 3;
    private static final double KNOCKBACK_POWER_T8_OR_ABOVE = 4;
    private static final Int2DoubleMap KNOCKBACK_POWER_MAP = EntityUtils.doubleChoiceBuilder().range(1, 2).value(KNOCKBACK_POWER).range(3, 7).value(KNOCKBACK_POWER_T3).range(8, 9).value(KNOCKBACK_POWER_T8_OR_ABOVE).build();

    private static final double EXTRA_DAMAGE_RADIUS = 0;
    private static final double EXTRA_DAMAGE_RADIUS_T5 = 1.5;
    private static final double EXTRA_DAMAGE_RADIUS_T8_OR_ABOVE = 3;
    private static final Int2DoubleMap EXTRA_DAMAGE_RADIUS_MAP = EntityUtils.doubleChoiceBuilder().range(1, 4).value(EXTRA_DAMAGE_RADIUS).range(5, 7).value(EXTRA_DAMAGE_RADIUS_T5).range(8, 9).value(EXTRA_DAMAGE_RADIUS_T8_OR_ABOVE).build();

    private static final float SPAWN_ZOMBIE_DAMAGE = 20;
    private static final int MAX_ZOMBIE_COUNT = 100;

    private static final Supplier<List<EffectInstance>> WEAKNESS_EFFECTS_SUPPLIER = () -> {
        List<EffectInstance> list = new ArrayList<>();
        list.add(new EffectInstance(Effects.WEAKNESS, 20 * 10, 0, true, false));
        list.add(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 30, 0, true, false));
        return list;
    };
    private static final Supplier<List<EffectInstance>> WEAKNESS_EFFECTS_SUPPLIER_T10_OR_ABOVE = () -> {
        List<EffectInstance> list = new ArrayList<>();
        list.add(new EffectInstance(Effects.WEAKNESS, 20 * 30, 0, true, false));
        list.add(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 120, 0, true, false));
        return list;
    };
    private static final DataParameter<Boolean> DATA_RUSHING = EntityDataManager.defineId(GiantXEntity.class, DataSerializers.BOOLEAN);

    private final Set<UUID> killerUUIDSet = new HashSet<>();
    private boolean falling;
    private boolean jumped;
    private boolean canModifyTier = true;
    private int tier = ITieredMob.MIN_TIER;
    private final PhaseManager<Phase> manager;
    private final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);
    private int totalZombieCount;

    public GiantXEntity(EntityType<? extends GiantXEntity> giant, World world) {
        super(giant, world);
        manager = new PhaseManager<>(this::getRandom, Phase::values);
        if (!level.isClientSide()) {
            registerPhasedGoals();
        }
        lookControl = new LockableLookController(this);
        xpReward = reachedTier(100) ? MAX_XP_REWARD : (int) Math.min(Math.round(BASE_XP_REWARD * Math.pow(XP_MULTIPLIER, getTier())), MAX_XP_REWARD);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 50)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.ATTACK_DAMAGE, 8)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(8, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, SubZombieEntity.class, GiantXEntity.class));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    protected void registerPhasedGoals() {
        goalSelector.addGoal(1, Goals.of(new RushAttackGoal(this, this::getCorrectRushSpeedModifier)).phased(manager, Phase.RUSH).get());
        goalSelector.addGoal(1, Goals.of(new JumpAttackGoal(this)).phased(manager, Phase.JUMP).get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_RUSHING, false);
    }

    private double getCorrectHealth() {
        if (reachedTier(10)) {
            return Math.min(MAX_HEALTH_T9 + HEALTH_STEP * (getTier() - 10), MAX_MAX_HEALTH);
        }
        return HEALTH_MAP.get(getTier());
    }

    private double getCorrectKnockbackResistance() {
        return KNOCKBACK_RESISTANCE_MAP.getOrDefault(getTier(), KNOCKBACK_RESISTANCE_T9_OR_ABOVE);
    }

    private double getCorrectRushSpeedModifier() {
        if (reachedTier(10)) {
            return Math.min(RUSH_SPEED_MODIFIER_T10 + RUSH_SPEED_MODIFIER_STEP * (getTier() - 10), MAX_RUSH_SPEED_MODIFIER);
        }
        return RUSH_SPEED_MODIFIER_MAP.get(getTier());
    }

    private double getCorrectKnockbackPower() {
        return KNOCKBACK_POWER_MAP.getOrDefault(getTier(), KNOCKBACK_POWER_T8_OR_ABOVE);
    }

    private double getCorrectExtraDamageRadius() {
        return EXTRA_DAMAGE_RADIUS_MAP.getOrDefault(getTier(), EXTRA_DAMAGE_RADIUS_T8_OR_ABOVE);
    }

    private List<EffectInstance> getEffectList() {
        return reachedTier(10) ? WEAKNESS_EFFECTS_SUPPLIER_T10_OR_ABOVE.get() : WEAKNESS_EFFECTS_SUPPLIER.get();
    }

    @Override
    public LockableLookController getLookControl() {
        return (LockableLookController) super.getLookControl();
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        if (!canModifyTier) {
            throw new UnsupportedOperationException("You can't modify tier now");
        }
        this.tier = MathHelper.clamp(tier, ITieredMob.MIN_TIER, ITieredMob.MAX_TIER);
    }

    @Override
    public Set<UUID> getKillerUUIDSet() {
        return killerUUIDSet;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof ZombieEntity) {
            return false;
        }
        if (source == DamageSource.IN_WALL) {
            BoundingBoxUtils.getBlockPosInside(getBoundingBox()).forEach(this::destroyBlockAt);
        }
        float oldHealth = getHealth();
        boolean hurt = super.hurt(source, amount);
        if (hurt) {
            int zombieCount = calculateZombieCount(oldHealth - getHealth());
            if (level instanceof ServerWorld) {
                totalZombieCount += spawnZombie(zombieCount, (ServerWorld) level);
            }
        }
        return hurt;
    }

    private int calculateZombieCount(float damage) {
        if (!reachedTier(10) || damage <= 0 || totalZombieCount > MAX_ZOMBIE_COUNT) {
            return 0;
        }
        int constantCount = MathHelper.floor(damage / SPAWN_ZOMBIE_DAMAGE);
        return constantCount + random.nextDouble() < (damage / SPAWN_ZOMBIE_DAMAGE - constantCount) ? 1 : 0;
    }

    private int spawnZombie(int zombieCount, ServerWorld world) {
        int spawnedZombieCount = 0;
        for (int i = 0; i < zombieCount; i++) {
            SubZombieEntity zombie = ModEntities.SUB_ZOMBIE.create(world);
            if (zombie != null) {
                zombie.moveTo(getRandomPosition());
                zombie.yRot = (float) (random.nextFloat() * 2 * Math.PI);
                zombie.finalizeSpawn(world, world.getCurrentDifficultyAt(blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
                if (zombie.getVehicle() != null) {
                    Entity vehicle = zombie.getVehicle();
                    zombie.removeVehicle();
                    vehicle.remove();
                }
                if (world.addFreshEntity(zombie)) {
                    spawnedZombieCount++;
                }
                if (getTarget() != null) {
                    zombie.setTarget(getTarget());
                }
                EntityUtils.spawnAnimServerside(zombie, world);
            }
        }
        return spawnedZombieCount;
    }

    private Vector3d getRandomPosition() {
        double randomLength = getBbWidth() * 1.5 + random.nextDouble() * 8;
        float randomAngle = (float) (random.nextFloat() * 2 * Math.PI);
        double x = getX() + MathHelper.cos(randomAngle) * randomLength;
        double z = getZ() + MathHelper.sin(randomAngle) * randomLength;
        return Vectors.moveToGround(new Vector3d(x, 0, z), level);
    }

    @Override
    protected float getJumpPower() {
        return super.getJumpPower() * (isFalling() ? 3 : 1);
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        return EntityUtils.shouldApplyEffect(this, effect, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        for (LivingEntity entity : level.getNearbyEntities(LivingEntity.class, EntityUtils.ALL_ATTACKABLE.selector(this::isAttackable), this, getBoundingBox().inflate(6, 6, 6))) {
            if (isRushing() && getBoundingBox().inflate(getCorrectExtraDamageRadius()).intersects(entity.getBoundingBox())) {
                doHurtTarget(entity);
                knockback(entity, getCorrectKnockbackPower());
            }
        }
        if (isRushing()) {
            BoundingBoxUtils.getBlockPosInside(getBoundingBox().move(0, 1, 0)).forEach(this::destroyBlockAt);
        }
    }

    private void knockback(LivingEntity entity, double knockbackPower) {
        entity.setDeltaMovement(EntityUtils.bottomOf(this).vectorTo(EntityUtils.centerOf(entity)).normalize().scale(1 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) * 0.6).scale(knockbackPower));
    }

    private boolean isAttackable(LivingEntity entity) {
        return !(entity instanceof ZombieEntity) || getTarget() instanceof ZombieEntity;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide()) {
            bossInfo.setName(EntityUtils.getBossNameFor(this));
            bossInfo.setPercent(getHealth() / getMaxHealth());
        }
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        setTierAccordingToPlayers();
        canModifyTier = false;
        ILivingEntityData result = super.finalizeSpawn(world, instance, reason, data, compoundNBT);
        for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
            setItemSlot(slotType, ItemStack.EMPTY);
        }
        handleAttributes();
        setHealth(getMaxHealth());
        return result;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            List<EffectInstance> effects = getEffectList();
            for (EffectInstance effect : effects) {
                int amplifier = Math.max(Optional.ofNullable(living.getEffect(effect.getEffect())).map(EffectInstance::getAmplifier).orElse(-1), effect.getAmplifier() - 1);
                if (amplifier < (reachedTier(10) ? 4 : 1)) {
                    amplifier++;
                }
                living.addEffect(new EffectInstance(effect.getEffect(), effect.getDuration(), amplifier, effect.isAmbient(), effect.isVisible()));
            }
        }
        return hurt;
    }

    protected void handleAttributes() {
        EntityUtils.getAttribute(this, Attributes.MAX_HEALTH).setBaseValue(getCorrectHealth());
        EntityUtils.getAttribute(this, Attributes.KNOCKBACK_RESISTANCE).setBaseValue(getCorrectKnockbackResistance());
    }

    @Override
    protected void randomizeReinforcementsChance() {
        EntityUtils.getAttribute(this, Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Tier", tier);
        compoundNBT.putBoolean("CanModifyTier", canModifyTier);
        compoundNBT.putBoolean("Falling", falling);
        saveKillers(compoundNBT);
        compoundNBT.putInt("TotalZombieCount", totalZombieCount);
        compoundNBT.put("PhaseManager", manager.save());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("Tier")) {
            tier = compoundNBT.getInt("Tier");
        }
        falling = compoundNBT.getBoolean("Falling");
        if (compoundNBT.contains("CanModifyTier")) {
            canModifyTier = compoundNBT.getBoolean("CanModifyTier");
        }
        totalZombieCount = compoundNBT.getInt("TotalZombieCount");
        if (compoundNBT.contains("PhaseManager", Constants.NBT.TAG_COMPOUND)) {
            manager.load(compoundNBT.getCompound("PhaseManager"));
        }
        loadKillers(compoundNBT);
    }

    public boolean isRushing() {
        return entityData.get(DATA_RUSHING);
    }

    public void setRushing(boolean rushing) {
        entityData.set(DATA_RUSHING, rushing);
        setSprinting(rushing);
    }

    @Override
    public boolean isSprinting() {
        return super.isSprinting() || isRushing();
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (name != null) {
            bossInfo.setName(name);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        bossInfo.removePlayer(player);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean baby) {
        super.setBaby(false);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        if (isFalling()) {
            setFalling(false);
            doFallAttack();
        }
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    private void doFallAttack() {
        AxisAlignedBB bb = getBoundingBox().inflate(6, 0, 6);
        bb = new AxisAlignedBB(bb.minX, bb.minY - 3, bb.minZ, bb.maxX, bb.minY + 3, bb.maxZ);
        for (LivingEntity entity : level.getNearbyEntities(LivingEntity.class, EntityUtils.ALL_ATTACKABLE.selector(this::isAttackable), this, bb.inflate(getCorrectExtraDamageRadius()))) {
            doHurtTarget(entity);
            knockback(entity, getCorrectKnockbackPower() * 2);
        }
        AxisAlignedBB pbb = bb.inflate(-3, 0, -3);
        pbb = new AxisAlignedBB(pbb.minX, pbb.minY + 3, pbb.minZ, pbb.maxX, pbb.minY + 3 + getBbHeight(), pbb.maxZ);
        level.getEntitiesOfClass(ProjectileEntity.class, pbb.inflate(getCorrectExtraDamageRadius()), projectile -> !Objects.equals(projectile.getOwner(), this)).forEach(Entity::remove);
        bb = bb.move(0, 1, 0).inflate(-4, -2, -4);
        bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + getBbHeight(), bb.maxZ);
        BoundingBoxUtils.getBlockPosInside(bb).forEach(this::destroyBlockAt);
    }

    private void destroyBlockAt(BlockPos pos) {
        if (EntityUtils.canMobGrief(this, pos)) {
            BlockState state = level.getBlockState(pos);
            if (state.getDestroySpeed(level, pos) >= 0) {
                level.destroyBlock(pos, !state.getMaterial().isReplaceable());
            }
        }
    }

    @Override
    public float getDamageMultiplier() {
        return 1 + getTier() * 0.1f;
    }

    public boolean isJumped() {
        return jumped;
    }

    public void setJumped(boolean jumped) {
        this.jumped = jumped;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize size) {
        return super.getStandingEyeHeight(pose, size) * 6;
    }

    public enum Phase implements IIdentifiableEnum {
        RUSH,
        JUMP
    }
}

