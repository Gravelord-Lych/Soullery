package lych.soullery.entity.monster.boss;

import lych.soullery.Soullery;
import lych.soullery.api.IMeta08NonAttackable;
import lych.soullery.api.shield.ISharedShield;
import lych.soullery.api.shield.ISharedShieldProvider;
import lych.soullery.client.ModRenderTypes;
import lych.soullery.config.ConfigHelper;
import lych.soullery.effect.ModEffects;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.boss.Meta08Goals.*;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.ai.phase.PhaseManager;
import lych.soullery.entity.iface.IDamageMultipliable;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.iface.ILaserAttacker;
import lych.soullery.entity.monster.RobotEntity;
import lych.soullery.extension.laser.LaserAttackResult;
import lych.soullery.extension.laser.LaserData;
import lych.soullery.extension.laser.LaserHitPredicate;
import lych.soullery.extension.laser.LaserHitType;
import lych.soullery.extension.shield.SharedShield;
import lych.soullery.util.*;
import lych.soullery.util.redirectable.RegexRedirectable;
import lych.soullery.util.redirectable.StringRedirectable;
import lych.soullery.world.event.manager.WorldTickerManager;
import lych.soullery.world.event.ticker.WorldTickers;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.List;
import java.util.*;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static lych.soullery.util.redirectable.StringRedirector.caseInsensitive;

/**
 * <b>Meta08 the Conceptor</b> (shortened as Meta8) is a large robot that is full of creativity.
 * It uses high-tech weapons such as lasers and also summon robots to attack enemies.<br>
 * <br>
 * Abilities:
 * <li><b>Laser Attack (high health): </b>Meta8 uses laser to attack its enemies:<br>
 * <b>Yellow laser: </b>Deals 6 base damage, plus bonus damage to strong enemies;<br>
 * <b>Red laser: </b>Deals 10 base damage but the damage is lower when damaging strong enemies,
 *                   creates an explosion with fire at the hit position;<br>
 * <b>Blue laser: </b>Hits and summons a {@link RobotEntity robot} at a random position near the target.
 *                    If someone blocks the laser, the laser will also deal 3 damage to the blocker.</li>
 * <li><b>Lightning Line (low health): </b>Meta8 creates lightning in a line that towards its target.</li>
 * <li><b>Robot Support (low health): </b>Meta8 summons 6 robots in order to help it.</li>
 * <li><b>Shield Sharing (low health): </b>Meta8 provides a {@link ISharedShield energy shield} for itself
 *                                         and nearby robots. If anyone that is protected by the shield is
 *                                         damaged, the whole shield will lose health.</li>
 * <br>
 * Abilities (Execute if {@link Meta08Entity#isCreative() creative}):
 * <li><b>Extra laser types: </b<br>
 * <b>Green laser: </b>Heals nearby robots (exclude itself).</li>
 * <b>Magenta laser: </b>Makes its target reversed (make the target move in reverse and see reversed scene).
 *                       Only affects players.<br>
 * <b>Yellow-green laser: </b>Spawns an {@link AreaEffectCloudEntity area effect cloud} nearby. Entities in
 *                            the cloud will be poisoned.<br>
 * <li><b>Emergency Teleport (passive): </b>When hurt, Meta8 may teleport randomly and become invulnerable for a
 *                                          short time. The lower the health, the longer the invulnerable time.</li>
 * <li><b>Purification (passive): </b>Meta8 clears nearby entities' beneficial effects every 3 seconds.</li>
 * <li><b>Shockwave (low health, passive): </b>Sometimes Meta8 uses a shockwave to stun nearby entities.</li>
 *
 * @author Gravelord Lych
 */
@OnlyIn(value = Dist.CLIENT, _interface = IChargeableMob.class)
public class Meta08Entity extends MonsterEntity implements ILaserAttacker, ISharedShieldProvider, IChargeableMob, IMeta08NonAttackable, IDamageMultipliable {
    public static final RedstoneParticleData ATTACK = RedstoneParticles.create(255, 192, 0);
    public static final RedstoneParticleData CHLORINE = RedstoneParticles.YELLOW_GREEN;
    public static final RedstoneParticleData FIRE = RedstoneParticles.create(255, 64, 0);
    public static final RedstoneParticleData HEAL = RedstoneParticles.SPRING_GREEN;
    public static final RedstoneParticleData REVERSION = RedstoneParticles.ERROR;
    public static final RedstoneParticleData SHIELD = RedstoneParticles.create(0, 192, 192);
    public static final RedstoneParticleData SHOCKWAVE = RedstoneParticles.create(0, 0, 192);
    public static final RedstoneParticleData SUMMON = RedstoneParticles.LIGHT_BLUE;
    public static final RedstoneParticleData LIGHTNING = RedstoneParticles.create(128, 255, 255);
    public static final double SHIELD_RANGE = 22;

    private static final double CLEAR_EFFECT_RANGE = 33;
    private static final int MAX_INVULNERABLE_TIME = 20 * 5;
    private static final int MIN_INVULNERABLE_TIME = 20 * 2;
    private static final double TELEPORT_PROBABILITY = 0.15;
    private static final Pattern PREDECESSORS = Pattern.compile("(?i)M[e3]ta(0*)[1-7]");
    private static final Pattern GRAVELORD_LYCH = Pattern.compile("(?i)Gravelord([ \\-_—]*)Lych");
    private static final RedstoneParticleData SPEEDY_META8_PARTICLE = RedstoneParticles.create(255, 245, 128);
    private static final DataParameter<Boolean> DATA_ATTACKING = EntityDataManager.defineId(Meta08Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DATA_PHASE = EntityDataManager.defineId(Meta08Entity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> DATA_LOW_HEALTH = EntityDataManager.defineId(Meta08Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_SHIELDED = EntityDataManager.defineId(Meta08Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_CREATIVE = EntityDataManager.defineId(Meta08Entity.class, DataSerializers.BOOLEAN);

    @Nullable
    private ISharedShield sharedShield;
    @Nullable
    private SpecialTrait trait;
    @NotNull
    private BossInfo.Color color = BossInfo.Color.BLUE;
    @NotNull
    private BossInfo.Overlay overlay = BossInfo.Overlay.NOTCHED_10;
    private final PhaseManager<HiPhase> hiManager;
    private final PhaseManager<LoPhase> loManager;
    private final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), color, overlay);

    public Meta08Entity(EntityType<? extends Meta08Entity> meta8, World world) {
        super(meta8, world);
        xpReward = 233;
        hiManager = new Meta08PhaseManager.Hi(this);
        loManager = new Meta08PhaseManager.Lo(this);
        if (!level.isClientSide()) {
            registerPhasedGoals();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ATTACKING, false);
        entityData.define(DATA_CREATIVE, false);
        entityData.define(DATA_PHASE, 0);
        entityData.define(DATA_LOW_HEALTH, false);
        entityData.define(DATA_SHIELDED, false);
    }

    @OnlyIn(Dist.CLIENT)
    public IMeta08Phase getPhaseClientSide() {
        if (isHighHealth()) {
            for (HiPhase phase : getHighHealthPhases()) {
                if (entityData.get(DATA_PHASE) == phase.getId()) {
                    return phase;
                }
            }
            return getHighHealthPhases()[0];
        } else {
            for (LoPhase phase : LoPhase.values()) {
                if (entityData.get(DATA_PHASE) == phase.getId()) {
                    return phase;
                }
            }
            return LoPhase.values()[0];
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.FOLLOW_RANGE, CLEAR_EFFECT_RANGE);
    }

    private static boolean isValidMeta08Target(Entity entity) {
        return entity instanceof LivingEntity && !(entity instanceof IMeta08NonAttackable);
    }

    private static LaserHitPredicate<LivingEntity> nonAttackable() {
        return LaserHitPredicate.by(LaserHitType.ENTITY)
                .makerFunction(Lasers.entities())
                .predicate(entity -> entity instanceof IMeta08NonAttackable)
                .noResult()
                .build(10);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, IMeta08NonAttackable.class).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, MobEntity.class, 10, true, false, mob -> isValidMeta08Target(mob) && Stream.of("b", "t", "d").anyMatch(Utils.getRegistryName(mob.getType()).getPath()::contains)));
    }

    protected void registerPhasedGoals() {
        goalSelector.addGoal(2, Goals.of(new Meta08LaserAttackGoal(this, 1, getAttackInterval(40), 6, 9, 15))
                .phased(hiManager, HiPhase.ATTACK)
                .executeIf(this::isHighHealth)
                .get());
        goalSelector.addGoal(2, Goals.of(new Meta08LaserAttackGoal(this, 1, getAttackInterval(60), 2, 2, 15))
                .phased(hiManager, HiPhase.FIRE)
                .executeIf(this::isHighHealth)
                .get());
        goalSelector.addGoal(2, Goals.of(new Meta08LaserAttackGoal(this, 1, getAttackInterval(20), 2, 3, 15))
                .phased(hiManager, HiPhase.SUMMON)
                .executeIf(this::isHighHealth)
                .get());
        goalSelector.addGoal(2, Goals.of(new SpawnLightningGoal(this, 0.8, 18, getAttackInterval(5), 4, 8, () -> 3 + random.nextInt(2)))
                .phased(loManager, LoPhase.LIGHTNING)
                .executeIf(this::isLowHealth)
                .get());
        goalSelector.addGoal(2, Goals.of(new SummonRobotWhenLowHealthGoal(this, 0.6, 12))
                .phased(loManager, LoPhase.SUMMON)
                .executeIf(this::isLowHealth)
                .get());
        goalSelector.addGoal(2, Goals.of(new ShieldRobotsGoal(this))
                .phased(loManager, LoPhase.SHIELD)
                .executeIf(this::isLowHealth)
                .get());

        goalSelector.addGoal(2, Goals.of(new Meta08LaserAttackGoal(this, 1, getAttackInterval(20), 1, 1, 18, () -> isCreative() ? 0 : 1))
                .phased(hiManager, HiPhase.CHLORINE)
                .executeIf(this::isHighHealth)
                .get());
        goalSelector.addGoal(2, Goals.of(new Meta08LaserAttackGoal(this, 1, getAttackInterval(20), 2, 3, 15, () -> isCreative() ? 0 : 1))
                .phased(hiManager, HiPhase.REVERSION)
                .executeIf(this::isHighHealth)
                .get());
        goalSelector.addGoal(2, Goals.of(new HealRobotGoal(this, 1, 2))
                .phased(hiManager, HiPhase.HEAL)
                .executeIf(this::isHighHealth)
                .get());
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        if (getTrait() != null) {
            xpReward *= 3;
        }
        if (getTrait() == SpecialTrait.ALL_ROUND) {
            xpReward *= 3;
        }
        return super.getExperienceReward(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getSharedShield() != null) {
            bossInfo.setColor(BossInfo.Color.PURPLE);
            bossInfo.setOverlay(BossInfo.Overlay.PROGRESS);
            bossInfo.setPercent(getSharedShield().getHealth() / getSharedShield().getPassiveDefense());
        } else {
            bossInfo.setColor(color);
            bossInfo.setOverlay(overlay);
            bossInfo.setPercent(getHealth() / getMaxHealth());
        }
        if (getTarget() != null && isCreative() && isLowHealth() && distanceToSqr(getTarget()) <= 6 * 6 && random.nextDouble() < 0.01) {
            doShockwaveAttack(level.getNearbyEntities(LivingEntity.class, EntityUtils.ALL_ATTACKABLE.get().range(6).selector(entity -> !(entity instanceof IMeta08NonAttackable)), this, getBoundingBox().inflate(6)));
        }
        if (isCreative() && tickCount % 60 == 0) {
            level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(CLEAR_EFFECT_RANGE), this::canClearEffect).forEach(this::clearEffect);
        }
        if (getTrait() != null && getTrait().isSpeedy() && random.nextDouble() < 0.25) {
            EntityUtils.addParticlesAroundSelfServerside(this, (ServerWorld) level, SPEEDY_META8_PARTICLE, 0.1, 1);
        }
    }

    private boolean canClearEffect(LivingEntity entity) {
        return entity.distanceToSqr(this) <= CLEAR_EFFECT_RANGE * CLEAR_EFFECT_RANGE && isValidMeta08Target(entity) && EntityPredicates.ATTACK_ALLOWED.test(entity);
    }

    private void clearEffect(LivingEntity entity) {
        if (!level.isClientSide()) {
            if (EntityUtils.removeEffect(entity, ModEffectUtils::isBeneficial) > 0) {
                EntityUtils.addParticlesAroundSelfServerside(entity, (ServerWorld) level, ParticleTypes.HAPPY_VILLAGER, 4 + random.nextInt(3));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (getHealth() <= getMaxHealth() * 0.5f && isHighHealth()) {
            entityData.set(DATA_LOW_HEALTH, true);
            loManager.setPhase(LoPhase.LIGHTNING);
            entityData.set(DATA_PHASE, 0);
        }
        if (level.isClientSide() && isLowHealth()) {
            if (random.nextDouble() < 0.2) {
                EntityUtils.addParticlesAroundSelf(this, ParticleTypes.LARGE_SMOKE, 1);
            }
        }
    }

    @Override
    protected void tickDeath() {
        deathTime++;
        if (bossInfo.getPercent() != 0) {
            bossInfo.setPercent(0);
        }
        if (deathTime == 100) {
            remove();
            deathParticles();
            return;
        }
        if (random.nextBoolean()) {
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0, 0);
        } else {
            level.addParticle(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0, 0);
        }
    }

    private void deathParticles() {
        for (int i = 0; i < 20; ++i) {
            double xSpeed = random.nextGaussian() * 0.02D;
            double ySpeed = random.nextGaussian() * 0.02D;
            double zSpeed = random.nextGaussian() * 0.02D;
            level.addParticle(ParticleTypes.POOF, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), xSpeed, ySpeed, zSpeed);
        }
    }

    public boolean isLowHealth() {
        return entityData.get(DATA_LOW_HEALTH);
    }

    public final boolean isHighHealth() {
        return !isLowHealth();
    }

    @Override
    public LaserData getLaserData(LivingEntity target) {
        return getPhaseManager().getPhase().getLaserData(this, target);
    }

    @Override
    public float getLaserDamage(LivingEntity target) {
        return getPhaseManager().getPhase().getLaserDamage(this, target);
    }

    @Override
    public Vector3d getAttackerPosition() {
        return getPhaseManager().getPhase().getAttackerPosition(this);
    }

    @Override
    public Vector3d getTargetPosition(LivingEntity target) {
        return getPhaseManager().getPhase().getTargetPosition(this, target);
    }

    @Override
    public double getBreakThreshold(LivingEntity target, Vector3d truePosition, float power) {
        return getPhaseManager().getPhase().getBreakThreshold(this, target, truePosition, power);
    }

    @Override
    public double getAttackDeviation(LivingEntity target) {
        return getPhaseManager().getPhase().getAttackDeviation(this, target);
    }

    @Override
    public int getLaserRenderTickCount(LivingEntity target) {
        return getTrait() != null && getTrait().isSpeedy() ? 15 : 30;
    }

    @Override
    public void onLaserAttack(LivingEntity target, int attackTime) {
        ILaserAttacker.super.onLaserAttack(target, attackTime);
        prepareAttack();
    }

    @Override
    public void postLaserAttack(LivingEntity target, LaserAttackResult result) {
        ILaserAttacker.super.postLaserAttack(target, result);
        getPhaseManager().getPhase().postAttack(this, result);
    }

    public void prepareAttack() {
        if (level instanceof ServerWorld && getPhaseManager().getPhase().getParticle() != null) {
            EntityUtils.addParticlesAroundSelfServerside(this, (ServerWorld) level, getPhaseManager().getPhase().getParticle(), 7 + random.nextInt(6));
        }
    }

    @Override
    public boolean isAttacking() {
        return entityData.get(DATA_ATTACKING);
    }

    @Override
    public void setAttacking(boolean attacking) {
        entityData.set(DATA_ATTACKING, attacking);
    }

    @Override
    public void playLaserSound() {
        playSound(ModSoundEvents.META8_LASER.get(), 2, 0.9f + random.nextFloat() * 0.1f);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return getTrait() != null && getTrait().isSpeedy() ? SoundEvents.LIGHTNING_BOLT_IMPACT : null;
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
        playSound(ModSoundEvents.ROBOT_STEP.get(), 0.3f, 0.9f);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("LowHealth", isLowHealth());
        compoundNBT.put("HiPhaseManager", hiManager.save());
        compoundNBT.put("LoPhaseManager", loManager.save());
        if (!level.isClientSide() && getSharedShield() != null) {
            compoundNBT.put("SharedShield", getSharedShield().save());
        }
        compoundNBT.putString("Color", color.getName());
        compoundNBT.putString("Overlay", overlay.getName());
        if (getTrait() != null) {
            compoundNBT.putInt("SpecialTrait", getTrait().ordinal());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("LowHealth")) {
            entityData.set(DATA_LOW_HEALTH, compoundNBT.getBoolean("LowHealth"));
        }
        hiManager.load(compoundNBT.getCompound("HiPhaseManager"));
        loManager.load(compoundNBT.getCompound("LoPhaseManager"));
        if (!level.isClientSide() && compoundNBT.contains("SharedShield")) {
            setSharedShield(new SharedShield(compoundNBT.getCompound("SharedShield")));
            Objects.requireNonNull(getSharedShield(), "SharedShield not present").setInvulnerableTicks(20);
        }
        if (compoundNBT.contains("Color")) {
            setColor(BossInfo.Color.byName(compoundNBT.getString("Color")));
        }
        if (compoundNBT.contains("Overlay")) {
            setOverlay(BossInfo.Overlay.byName(compoundNBT.getString("Overlay")));
        }
    }

//  Load the trait first.
    @Override
    public void load(CompoundNBT compoundNBT) {
        try {
            if (compoundNBT.contains("SpecialTrait")) {
                setTrait(SpecialTrait.byOrdinal(compoundNBT.getInt("SpecialTrait")));
            }
        } catch (EnumConstantNotFoundException e) {
            if (ConfigHelper.shouldFailhard()) {
                crash(new IllegalStateException(ConfigHelper.FAILHARD_MESSAGE + String.format("Trait indexed %s for Meta08 was not found", e.getId())));
                return;
            }
            LOGGER.warn("Trait indexed {} was not found, so ignore the trait", e.getId());
            setTrait(null);
        } catch (Throwable throwable) {
            crash(throwable);
        }
        super.load(compoundNBT);
    }

    private void crash(Throwable throwable) {
        CrashReport report = CrashReport.forThrowable(throwable, String.format("%s - Loading Meta08's Trait", Soullery.MOD_NAME));
        CrashReportCategory category = report.addCategory("Meta08 being loaded");
        fillCrashReportCategory(category);
        throw new ReportedException(report);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
//      Disable custom name setting for traited Meta8
        if (getTrait() != null) {
            if (name != null && getCustomName() == null) {
                setCustomNameDirectly(new StringTextComponent(getTrait().getName(name)));
            }
            return;
        }
        setCustomNameDirectly(name);
        if (getTrait() == null && name != null) {
            SpecialTrait trait = SpecialTrait.find(name);
            if (trait != null) {
                spawnTraitedMeta8(trait, name);
            }
        }
    }

    private void setCustomNameDirectly(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (name != null) {
            bossInfo.setName(name);
        }
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    private void spawnTraitedMeta8(SpecialTrait trait, ITextComponent name) {
        if (level instanceof ServerWorld) {
            Meta08Entity meta8 = ModEntities.META8.create(level);
            if (meta8 != null) {
                meta8.setCustomNameDirectly(new StringTextComponent(trait.getName(name)));
                meta8.setTrait(trait);
                trait.applyTo(meta8);
                meta8.setHealth(meta8.getMaxHealth());
                ((ServerWorld) level).getEntities()
                        .filter(entity -> entity instanceof RobotEntity)
                        .map(entity -> (RobotEntity) entity)
                        .filter(robot -> Objects.equals(robot.getOwnerUUID(), getUUID()))
                        .forEach(robot -> robot.setOwner(meta8));
                meta8.copyPosition(this);
                meta8.finalizeSpawn((ServerWorld) level, level.getCurrentDifficultyAt(blockPosition()), SpawnReason.CONVERSION, null, null);
                ForgeEventFactory.onLivingConvert(this, meta8);
                WorldTickerManager.start(WorldTickers.EXPLOSION, (ServerWorld) level, meta8.blockPosition());
                remove();
                level.addFreshEntity(meta8);
            }
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
    public boolean canBeAffected(EffectInstance effect) {
        if (ModEffectUtils.isHarmful(effect)) {
            return level.getDifficulty() != Difficulty.HARD && EntityUtils.shouldApplyEffect(this, effect, false);
        }
        return super.canBeAffected(effect);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (getPhaseManager().getPhase().isInvulnerableTo(this, source)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean isPowered() {
        return entityData.get(DATA_SHIELDED);
    }

    @Nullable
    @Override
    public ISharedShield getSharedShield() {
        return sharedShield;
    }

    @Override
    public void setSharedShield(@Nullable ISharedShield sharedShield) {
        this.sharedShield = sharedShield;
        entityData.set(DATA_SHIELDED, sharedShield != null);
    }

    public PhaseManager<? extends IMeta08Phase> getPhaseManager() {
        return isLowHealth() ? loManager : hiManager;
    }

    @Override
    public void thunderHit(ServerWorld world, LightningBoltEntity bolt) {
        if (((IHasOwner<?>) bolt).getOwner() instanceof IMeta08NonAttackable) {
            return;
        }
        super.thunderHit(world, bolt);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && isCreative() && random.nextDouble() < TELEPORT_PROBABILITY) {
            randomTeleport();
        }
        return hurt;
    }

    private void randomTeleport() {
        for (int i = 0; i < 5; i++) {
            double x = getX() + (random.nextDouble() - 0.5) * 8;
            double y = MathHelper.clamp(getY() + (random.nextInt(9) - 4), 0, level.getHeight() - 1);
            double z = getZ() + (random.nextDouble() - 0.5) * 8;
            if (randomTeleport(x, y, z, true)) {
                addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, (int) MathHelper.lerp(1 - getHealth() / getMaxHealth(), MIN_INVULNERABLE_TIME, MAX_INVULNERABLE_TIME), 4, false, false, false));
                break;
            }
        }
    }

    @Override
    public float getDamageMultiplier() {
        return getTrait() != null && getTrait().multipliesDamage() ? 3 : 1;
    }

    @Nullable
    public SpecialTrait getTrait() {
        return trait;
    }

    public void setTrait(@Nullable SpecialTrait trait) {
        if (getTrait() != null && trait != getTrait()) {
            throw new UnsupportedOperationException(String.format("Trait %s exists", getTrait()));
        }
        if (trait != null && trait.isCreative()) {
            entityData.set(DATA_CREATIVE, true);
        }
        this.trait = trait;
    }

    public void setColor(BossInfo.Color color) {
        this.color = color;
    }

    public void setOverlay(BossInfo.Overlay overlay) {
        this.overlay = overlay;
    }

    private IntSupplier getAttackInterval(int baseInterval) {
        return () -> baseInterval / (getTrait() != null && getTrait().isSpeedy() ? 2 : 1);
    }

    public boolean isCreative() {
        return level.isClientSide() ? entityData.get(DATA_CREATIVE) : (getTrait() != null && getTrait().isCreative());
    }

    public void addModifiersTo(RobotEntity robot) {
        if (getTrait() == null) {
            return;
        }
        getTrait().addModifiersTo(robot, this);
    }

    public void doShockwaveAttack(List<? extends LivingEntity> victims) {
        if (level.isClientSide()) {
            return;
        }
        for (int i = 0; i < 12 + random.nextInt(8); i++) {
            double x = getRandomX(1);
            double y = getY() + 0.5 + random.nextDouble() * 1.5;
            double z = getRandomZ(1);
            Vector3d vector = new Vector3d(3, 1, 0);
            vector = Vectors.rotateTo(vector, random.nextDouble() * 2 * Math.PI, true).normalize().scale(2 + random.nextDouble() * 2);
            x += vector.x;
            y += vector.y;
            z += vector.z;
            vector = vector.scale(3);
            ((ServerWorld) level).sendParticles(Meta08Entity.SHOCKWAVE, x, y, z, 1, vector.x, vector.y, vector.z, 2);
        }
        for (LivingEntity entity : victims) {
            entity.knockback(10, entity.getX() - getX(), entity.getZ() - getZ());
            entity.hurt(DamageSource.mobAttack(this).bypassArmor().bypassMagic(), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
            EntityUtils.addParticlesAroundSelfServerside(entity, (ServerWorld) level, ParticleTypes.POOF, 3 + random.nextInt(3));
            int duration = (int) MathHelper.clamp(20 * (10 - distanceTo(entity)), 20 * 4, 20 * 10);
            entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, duration, 6));
            entity.addEffect(new EffectInstance(Effects.WEAKNESS, duration, 2));
            entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, duration * 4, 1));
        }
    }

    private HiPhase[] getHighHealthPhases() {
        return isCreative() ? HiPhase.values() : HiPhase.commonValues();
    }

    @Override
    public RenderType getLaserRenderType() {
        return ModRenderTypes.laser(4);
    }

    private static Color makeColor(RedstoneParticleData data) {
        return new Color(data.getR(), data.getG(), data.getB());
    }

    private static abstract class Meta08PhaseManager<E extends Enum<E> & IMeta08Phase> extends PhaseManager<E> {
        protected final Meta08Entity meta8;

        private Meta08PhaseManager(Meta08Entity meta8, @Nullable Supplier<? extends Random> randomSupplier, Supplier<? extends E[]> values) {
            super(randomSupplier, values);
            this.meta8 = meta8;
        }

        @Override
        public void setPhaseId(int phaseId) {
            super.setPhaseId(phaseId);
            meta8.entityData.set(DATA_PHASE, phaseId);
        }

        private static class Hi extends Meta08PhaseManager<HiPhase> {
            private Hi(Meta08Entity meta8) {
                super(meta8, () -> meta8.isCreative() ? meta8.random : null, meta8::getHighHealthPhases);
            }
        }

        private static class Lo extends Meta08PhaseManager<LoPhase> {
            private Lo(Meta08Entity meta8) {
                super(meta8, null, LoPhase::values);
            }
        }
    }

    public enum HiPhase implements IMeta08Phase {
        ATTACK(Meta08Entity.ATTACK, 0, false) {
            private static final float BASE_DAMAGE = 5;
            private static final double DEVIATION = 0.4;

            @Override
            public LaserData getLaserData(Meta08Entity meta8, LivingEntity target) {
                return new LaserData.Builder()
                        .durability(4000)
                        .color(makeColor(Meta08Entity.ATTACK))
                        .predicate(Lasers.entity(), 4000)
                        .predicate(nonAttackable(), 1)
                        .build();
            }

            @Override
            public float getLaserDamage(Meta08Entity meta8, LivingEntity target) {
                return BASE_DAMAGE + target.getMaxHealth() * 0.05f;
            }

            @Override
            public double getBreakThreshold(Meta08Entity meta8, LivingEntity target, Vector3d truePosition, float power) {
                return Double.NaN;
            }

            @Override
            public double getAttackDeviation(Meta08Entity meta8, LivingEntity target) {
                return DEVIATION;
            }

            @Override
            public Vector3d getTargetPosition(Meta08Entity meta8, LivingEntity target) {
                return EntityUtils.centerOf(target);
            }

            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return EntityUtils.isMelee(source);
            }
        },
        CHLORINE(Meta08Entity.CHLORINE, 3, true) {
            @Override
            public LaserData getLaserData(Meta08Entity meta8, LivingEntity target) {
                return new LaserData.Builder()
                        .durability(3000)
                        .color(makeColor(Meta08Entity.CHLORINE))
                        .predicate(Lasers.entity(), 3000)
                        .predicate(nonAttackable(), 1)
                        .build();
            }

            @Override
            public float getLaserDamage(Meta08Entity meta8, LivingEntity target) {
                return 5;
            }

            @Override
            public double getBreakThreshold(Meta08Entity meta8, LivingEntity target, Vector3d truePosition, float power) {
                return 3;
            }

            @Override
            public double getAttackDeviation(Meta08Entity meta8, LivingEntity target) {
                return 2;
            }

            @Override
            public Vector3d getTargetPosition(Meta08Entity meta8, LivingEntity target) {
                return EntityUtils.centerOf(target);
            }

            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return source.isProjectile();
            }

            @Override
            public void postAttack(Meta08Entity meta8, LaserAttackResult result) {
                super.postAttack(meta8, result);
                result.getLastHitPos().ifPresent(vec -> {
                    AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(meta8.level, vec.x, vec.y, vec.z);
                    cloud.setOwner(meta8);
                    cloud.setFixedColor(Effects.POISON.getColor());
                    cloud.addEffect(new EffectInstance(Effects.POISON, 20 * (15 + meta8.getRandom().nextInt(16)), 0));
                    cloud.addEffect(new EffectInstance(Effects.POISON, 20 * (5 + meta8.getRandom().nextInt(6)), 1));
                    cloud.setRadius(3);
                    cloud.setRadiusOnUse(-0.3f);
                    cloud.setDuration(20 * (10 + meta8.getRandom().nextInt(11)));
                    meta8.level.addFreshEntity(cloud);
                });
            }
        },
        FIRE(Meta08Entity.FIRE, 1, false) {
            private static final float BASE_DAMAGE = 10;
            private static final float MIN_DAMAGE = 4;
            private static final double DEVIATION = 1.5;

            @Override
            public LaserData getLaserData(Meta08Entity meta8, LivingEntity target) {
                return new LaserData.Builder()
                        .durability(3000)
                        .color(makeColor(Meta08Entity.FIRE))
                        .predicate(Lasers.entity(), 3000)
                        .predicate(nonAttackable(), 1)
                        .build();
            }

            @Override
            public float getLaserDamage(Meta08Entity meta8, LivingEntity target) {
                return Math.max(MIN_DAMAGE, BASE_DAMAGE - target.getMaxHealth() * 0.05f);
            }

            @Override
            public double getBreakThreshold(Meta08Entity meta8, LivingEntity target, Vector3d truePosition, float power) {
                return Double.NaN;
            }

            @Override
            public double getAttackDeviation(Meta08Entity meta8, LivingEntity target) {
                return DEVIATION;
            }

            @Override
            public Vector3d getTargetPosition(Meta08Entity meta8, LivingEntity target) {
                return EntityUtils.bottomOf(target);
            }

            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return source.isProjectile();
            }

            @Override
            public void postAttack(Meta08Entity meta8, LaserAttackResult result) {
                super.postAttack(meta8, result);
                result.getLastHitPos().ifPresent(vec -> meta8.level.explode(meta8, vec.x, vec.y, vec.z, 2, EntityUtils.canMobGrief(meta8), Explosion.Mode.NONE));
            }
        },
        HEAL(Meta08Entity.HEAL, 4, true) {
            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return source.isProjectile();
            }

            @Override
            public LaserData getLaserData(Meta08Entity meta8, LivingEntity target) {
                return new LaserData.Builder()
                        .durability(4000)
                        .color(makeColor(Meta08Entity.HEAL))
                        .predicate(Lasers.entity(true), 20)
                        .predicate(Lasers.monster(meta8, false), 4000)
                        .build();
            }

            @Override
            public float getLaserDamage(Meta08Entity meta8, LivingEntity target) {
                return 0;
            }

            @Override
            public double getBreakThreshold(Meta08Entity meta8, LivingEntity target, Vector3d truePosition, float power) {
                return Double.NaN;
            }

            @Override
            public double getAttackDeviation(Meta08Entity meta8, LivingEntity target) {
                return 0;
            }

            @Override
            public Vector3d getTargetPosition(Meta08Entity meta8, LivingEntity target) {
                return target.getBoundingBox().getCenter();
            }

            @Override
            public void postAttack(Meta08Entity meta8, LaserAttackResult result) {
                super.postAttack(meta8, result);
                result.getLastHitPos()
                        .map(vec -> meta8.level.getEntitiesOfClass(LivingEntity.class, BoundingBoxUtils.inflate(vec, 4), entityIn -> entityIn instanceof IMeta08NonAttackable && !Objects.equals(entityIn, meta8)))
                        .orElse(Collections.emptyList())
                        .forEach(this::heal);
            }

            private void heal(LivingEntity entity) {
                entity.heal(10);
                EntityUtils.addParticlesAroundSelfServerside(entity, (ServerWorld) entity.level, ParticleTypes.HEART, 3);
            }
        },
        REVERSION(Meta08Entity.REVERSION, 5, true) {
            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return source.isProjectile();
            }

            @Override
            public LaserData getLaserData(Meta08Entity meta8, LivingEntity target) {
                return new LaserData.Builder()
                        .durability(4000)
                        .color(makeColor(Meta08Entity.REVERSION))
                        .predicate(Lasers.entity(), 4000)
                        .predicate(nonAttackable(), 1)
                        .build();
            }

            @Override
            public float getLaserDamage(Meta08Entity meta8, LivingEntity target) {
                return 3;
            }

            @Override
            public double getBreakThreshold(Meta08Entity meta8, LivingEntity target, Vector3d truePosition, float power) {
                return Double.NaN;
            }

            @Override
            public double getAttackDeviation(Meta08Entity meta8, LivingEntity target) {
                return 1.5;
            }

            @Override
            public Vector3d getTargetPosition(Meta08Entity meta8, LivingEntity target) {
                return EntityUtils.centerOf(target);
            }

            @Override
            public void postAttack(Meta08Entity meta8, LaserAttackResult result) {
                super.postAttack(meta8, result);
                Lasers.acceptHitEntities(result, entity -> entity.addEffect(new EffectInstance(ModEffects.REVERSION, 20 * 10, 0)));
            }
        },
        SUMMON(Meta08Entity.SUMMON, 2, false) {
            @Override
            public LaserData getLaserData(Meta08Entity meta8, LivingEntity target) {
                return new LaserData.Builder()
                        .durability(2500)
                        .color(makeColor(Meta08Entity.SUMMON))
                        .predicate(Lasers.entity(), 2500)
                        .predicate(nonAttackable(), 1)
                        .build();
            }

            @Override
            public float getLaserDamage(Meta08Entity meta8, LivingEntity target) {
                return 3;
            }

            @Override
            public double getBreakThreshold(Meta08Entity meta8, LivingEntity target, Vector3d truePosition, float power) {
                return 5;
            }

            @Override
            public double getAttackDeviation(Meta08Entity meta8, LivingEntity target) {
                return 5 + meta8.getRandom().nextDouble() * 5;
            }

            @Override
            public Vector3d getTargetPosition(Meta08Entity meta8, LivingEntity target) {
                return EntityUtils.bottomOf(target);
            }

            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return source.isProjectile();
            }

            @Override
            public void postAttack(Meta08Entity meta8, LaserAttackResult result) {
                super.postAttack(meta8, result);
                RobotEntity robot = ModEntities.ROBOT.create(result.getWorld());
                if (robot != null && result.getLastHitPos().isPresent()) {
                    robot.setOwner(meta8);
                    robot.moveTo(result.getLastHitPos().get().add(0, 1, 0));
                    robot.finalizeSpawn((IServerWorld) meta8.level, meta8.level.getCurrentDifficultyAt(new BlockPos(result.getLastHitPos().get())), SpawnReason.MOB_SUMMONED, null, null);
                    robot.addEffect(new EffectInstance(Effects.SLOW_FALLING, 20 * 2, 0, false, false, false));
                    meta8.level.addFreshEntity(robot);
                    EntityUtils.spawnAnimServerside(robot, (ServerWorld) meta8.level);
                }
            }
        };

        @Nullable
        private final RedstoneParticleData particle;
        private final int id;
        private final boolean onlyForCreativeMeta8;

        HiPhase(@Nullable RedstoneParticleData particle, int id, boolean onlyForCreativeMeta8) {
            this.particle = particle;
            this.id = id;
            this.onlyForCreativeMeta8 = onlyForCreativeMeta8;
        }

        @Nullable
        @Override
        public RedstoneParticleData getParticle() {
            return particle;
        }

        @Override
        public Vector3d getAttackerPosition(Meta08Entity meta8) {
            return new Vector3d(meta8.getX(), meta8.getEyeY(), meta8.getZ());
        }

        @Override
        public void postAttack(Meta08Entity meta8, LaserAttackResult result) {}

        public static HiPhase[] commonValues() {
            return Arrays.stream(values()).filter(phase -> !phase.onlyForCreativeMeta8).toArray(HiPhase[]::new);
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public enum LoPhase implements IMeta08Phase {
        LIGHTNING(Meta08Entity.LIGHTNING, 0) {
            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return EntityUtils.isMelee(source);
            }
        },
        SUMMON(Meta08Entity.SUMMON, 1) {
            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return source.isProjectile();
            }
        },
        SHIELD(Meta08Entity.SHIELD, 2) {
            @Override
            public boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source) {
                return source.isProjectile();
            }
        };

        @Nullable
        private final RedstoneParticleData particle;
        private final int id;

        LoPhase(@Nullable RedstoneParticleData particle, int id) {
            this.particle = particle;
            this.id = id;
        }

        @Nullable
        @Override
        public RedstoneParticleData getParticle() {
            return particle;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public interface IMeta08Phase extends IIdentifiableEnum {
        boolean isInvulnerableTo(Meta08Entity meta8, DamageSource source);

        String name();

        default LaserData getLaserData(Meta08Entity meta8, LivingEntity target) {
            throw new UnsupportedOperationException();
        }

        default float getLaserDamage(Meta08Entity meta8, LivingEntity target) {
            throw new UnsupportedOperationException();
        }

        default double getBreakThreshold(Meta08Entity meta8, LivingEntity target, Vector3d truePosition, float power) {
            throw new UnsupportedOperationException();
        }

        default double getAttackDeviation(Meta08Entity meta8, LivingEntity target) {
            throw new UnsupportedOperationException();
        }

        default Vector3d getTargetPosition(Meta08Entity meta8, LivingEntity target) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        default RedstoneParticleData getParticle() {
            return null;
        }

        default double[] getColor() {
            if (getParticle() == null) {
                return new double[]{0, 0, 0};
            }
            return new double[]{getParticle().getR(), getParticle().getG(), getParticle().getB()};
        }

        default Vector3d getAttackerPosition(Meta08Entity meta8) {
            return meta8.getEyePosition(1);
        }

        default void postAttack(Meta08Entity meta8, LaserAttackResult result) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Traited Meta8 is much stronger than the non-traited one.
     */
    public enum SpecialTrait {
        AGGRESSIVE(caseInsensitive("Mandy"), true, false, false, false) {
            @Override
            public void applyTo(Meta08Entity meta8) {
                super.applyTo(meta8);
                meta8.setColor(BossInfo.Color.RED);
            }
        },
        DEFENSIVE(caseInsensitive("Constance").or(caseInsensitive("Teddy")), false, true, false, false) {
            private final AttributeModifier healthBonus = new AttributeModifier(UUID.fromString("621C6C42-4EC0-10F2-93AD-E9CDF04766C7"), "Defensive Meta8 health bonus", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

            @Override
            public void applyTo(Meta08Entity meta8) {
                super.applyTo(meta8);
                EntityUtils.addPermanentModifierIfAbsent(meta8, Attributes.MAX_HEALTH, healthBonus);
                EntityUtils.getAttribute(meta8, Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
                meta8.setOverlay(BossInfo.Overlay.NOTCHED_20);
            }

            @Override
            public void addModifiersTo(RobotEntity robot, Meta08Entity meta8) {
                super.addModifiersTo(robot, meta8);
                EntityUtils.getAttribute(robot, Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Defensive robot health bonus", 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
                ModifiableAttributeInstance knockbackResistance = EntityUtils.getAttribute(robot, Attributes.KNOCKBACK_RESISTANCE);
                knockbackResistance.setBaseValue(Math.max(0.5, knockbackResistance.getBaseValue()));
                robot.setHealth(robot.getMaxHealth());
            }
        },
        SPEEDY(caseInsensitive("AJAYA").or(caseInsensitive("Vortex")), false, false, true, false) {
            private final AttributeModifier speedBonus = new AttributeModifier(UUID.fromString("D1776DE7-6453-891C-9903-8B6C358CEF6F"), "Speedy Meta8 movement speed bonus", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
            private final AttributeModifier followRangeBonus = new AttributeModifier(UUID.fromString("7A6FC8E6-6C48-A12A-A093-F48B628B1993"), "Speedy Meta8 follow range bonus", 15, AttributeModifier.Operation.ADDITION);

            @Override
            public void applyTo(Meta08Entity meta8) {
                super.applyTo(meta8);
                EntityUtils.addPermanentModifierIfAbsent(meta8, Attributes.FOLLOW_RANGE, followRangeBonus);
                EntityUtils.addPermanentModifierIfAbsent(meta8, Attributes.MOVEMENT_SPEED, speedBonus);
            }

            @Override
            public void addModifiersTo(RobotEntity robot, Meta08Entity meta8) {
                super.addModifiersTo(robot, meta8);
                EntityUtils.getAttribute(robot, Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier("Speedy robot movement speed bonus", 0.75, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        },
        CREATIVE(caseInsensitive("Tim").or(new RegexRedirectable(PREDECESSORS, s -> "Meta0" + s.charAt(s.length() - 1))), false, false, false, true),
        ALL_ROUND(caseInsensitive("Lych").or(caseInsensitive("Lych233")).or(new RegexRedirectable(GRAVELORD_LYCH, s -> "Gravelord Lych")), true, true, true, true) {
            @Override
            public void applyTo(Meta08Entity meta8) {
                Arrays.stream(NON_ALL_ROUND_TRAITS.get()).forEach(t -> t.applyTo(meta8));
            }

            @Override
            public void addModifiersTo(RobotEntity robot, Meta08Entity meta8) {
                Arrays.stream(NON_ALL_ROUND_TRAITS.get()).forEach(t -> t.addModifiersTo(robot, meta8));
            }
        };

        private static final LazyValue<SpecialTrait[]> NON_ALL_ROUND_TRAITS = new LazyValue<>(() -> Arrays.stream(values()).filter(t -> t != ALL_ROUND).toArray(SpecialTrait[]::new));
        private final StringRedirectable matchName;
        private final boolean multipliesDamage;
        private final boolean enhancesShield;
        private final boolean speedy;
        private final boolean creative;

        SpecialTrait(StringRedirectable matchName, boolean multipliesDamage, boolean enhancesShield, boolean speedy, boolean creative) {
            this.multipliesDamage = multipliesDamage;
            this.enhancesShield = enhancesShield;
            this.speedy = speedy;
            this.creative = creative;
            Objects.requireNonNull(matchName);
            this.matchName = matchName;
        }

        private boolean canRedirect(ITextComponent component) {
            return matchName.test(component.getString());
        }

        public void applyTo(Meta08Entity meta8) {}

        public void addModifiersTo(RobotEntity robot, Meta08Entity meta8) {}

        public String getName(ITextComponent name) {
            return matchName.redirect(name.getString(), s -> { throw new IllegalStateException("Name was not found"); });
        }

        @Nullable
        public static SpecialTrait find(ITextComponent component) {
            return Arrays.stream(values()).filter(trait -> trait.canRedirect(component)).findFirst().orElse(null);
        }

        public static SpecialTrait byOrdinal(int ordinal) throws EnumConstantNotFoundException {
            for (SpecialTrait trait : values()) {
                if (trait.ordinal() == ordinal) {
                    return trait;
                }
            }
            throw new EnumConstantNotFoundException(ordinal);
        }

        public boolean isSpeedy() {
            return speedy;
        }

        public boolean isCreative() {
            return creative;
        }

        public boolean multipliesDamage() {
            return multipliesDamage;
        }

        public boolean enhancesShield() {
            return enhancesShield;
        }
    }
}
