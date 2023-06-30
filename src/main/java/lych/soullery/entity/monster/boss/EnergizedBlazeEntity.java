package lych.soullery.entity.monster.boss;

import lych.soullery.advancements.ModCriteriaTriggers;
import lych.soullery.entity.ai.goal.boss.EnergizedBlazeGoals;
import lych.soullery.entity.ai.goal.boss.EnergizedBlazeGoals.FirestormGoal;
import lych.soullery.entity.ai.goal.boss.EnergizedBlazeGoals.PhasedFireballAttackGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.ai.phase.ISkippablePhase;
import lych.soullery.entity.ai.phase.PhaseManager;
import lych.soullery.entity.ai.phase.SkippablePhaseManager;
import lych.soullery.util.*;
import lych.soullery.util.mixin.IGoalSelectorMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EnergizedBlazeEntity extends BlazeEntity implements IHasKillers {
    public static final double FIRESTORM_RANGE = 24;
    private static final float FIRESTORM_DAMAGE = 6;
    private static final int FIRESTORM_FIRE_TIME = 8;
    private static final int FIRESTORM_PIERCE = 10;
    private static final float SWITCH_ATTACK_TYPE_HEALTH = 0.6f;
    private final PhaseManager<Phase> manager;
    private final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.NOTCHED_10);
    private final Set<UUID> killers = new HashSet<>();
    private final Set<UUID> invalidBurningUpKillers = new HashSet<>();
    private boolean constantlyDoFireballAttack;

    public EnergizedBlazeEntity(EntityType<? extends EnergizedBlazeEntity> type, World world) {
        super(type, world);
        manager = new SkippablePhaseManager<>(this::getRandom, Phase::values);
        xpReward = 200;
        if (!level.isClientSide()) {
            registerPhasedGoals();
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return BlazeEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 240)
                .add(Attributes.ATTACK_DAMAGE, 11);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
//      Remove vanilla fireball attack goal
        ((IGoalSelectorMixin) goalSelector).getAvailableGoals().removeIf(goal -> goal.getPriority() == 4);
        goalSelector.addGoal(4, new EnergizedBlazeGoals.FireballAttackGoal(this, true, 10, 4, 4, 0, 0.8f));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    protected void registerPhasedGoals() {
        goalSelector.addGoal(4, Goals.of(new FirestormGoal(this, 20)).phased(manager, Phase.FIRESTORM).get());
        goalSelector.addGoal(4, Goals.of(new PhasedFireballAttackGoal(this, true, 15, 40, 4, 50, 1)).phased(manager, Phase.FIREBALL).get());
        goalSelector.addGoal(4, Goals.of(new PhasedFireballAttackGoal(this, false, 8, 40, 6, 50, 1.2f)).phased(manager, Phase.EXPLOSION).get());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getHealth() < getMaxHealth() * SWITCH_ATTACK_TYPE_HEALTH) {
            setConstantlyDoFireballAttack(true);
        }
        bossInfo.setPercent(getHealth() / getMaxHealth());
        if (isConstantlyDoingFireballAttack()) {
            bossInfo.setColor(BossInfo.Color.RED);
        } else {
            bossInfo.setColor(BossInfo.Color.YELLOW);
        }

        if (isConstantlyDoingFireballAttack() && canAttackTarget()) {
            if (getTarget().fireImmune() && level.getNearestPlayer(this, FIRESTORM_RANGE) == null) {
                return;
            }
            int firestormFreq = 300;
            if (getTarget().hasEffect(Effects.FIRE_RESISTANCE)) {
                firestormFreq = 30;
            } else if (getTarget().isOnFire()) {
                firestormFreq = 600;
            }
            if (random.nextInt(firestormFreq) == 0) {
                firestorm();
            }
        }

        iterateKillers(level, player -> !EntityUtils.isSurvival(player) || !player.isOnFire(), player -> invalidBurningUpKillers.add(player.getUUID()));
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level.isClientSide()) {
            Set<UUID> burningUpKillers = new HashSet<>(killers);
            burningUpKillers.removeAll(invalidBurningUpKillers);
            for (UUID uuid : burningUpKillers) {
                ServerPlayerEntity player = (ServerPlayerEntity) level.getPlayerByUUID(uuid);
                if (EntityUtils.isSurvival(player)) {
                    ModCriteriaTriggers.BURNING_UP.trigger(player);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.put("PhaseManager", manager.save());
        compoundNBT.putBoolean("ConstantlyDoFireballAttack", isConstantlyDoingFireballAttack());
        Utils.saveUUIDSet(compoundNBT, killers, "BurningUpKillers");
        Utils.saveUUIDSet(compoundNBT, invalidBurningUpKillers, "InvalidBurningUpKillers");
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        manager.load(compoundNBT.getCompound("PhaseManager"));
        setConstantlyDoFireballAttack(compoundNBT.getBoolean("ConstantlyDoFireballAttack"));
        Utils.loadUUIDSet(compoundNBT, killers, "BurningUpKillers");
        Utils.loadUUIDSet(compoundNBT, invalidBurningUpKillers, "InvalidBurningUpKillers");
    }

    public boolean canAttackTarget() {
        LivingEntity target = getTarget();
        return target != null && target.isAlive() && canAttack(target);
    }

    public void firestorm() {
        if (level.isClientSide()) {
            return;
        }
        EntityUtils.getEntitiesInRange(LivingEntity.class, this, FIRESTORM_RANGE, entity -> canAttack(entity) && !entity.fireImmune()).stream().sorted(Comparator.comparingDouble(this::distanceToSqr)).limit(FIRESTORM_PIERCE).forEach(entity -> {
            if (entity.hasEffect(Effects.FIRE_RESISTANCE)) {
                entity.removeEffect(Effects.FIRE_RESISTANCE);
            } else {
                entity.hurt(DamageSource.mobAttack(this).setIsFire(), FIRESTORM_DAMAGE);
                entity.setSecondsOnFire(FIRESTORM_FIRE_TIME);
                entity.addEffect(new EffectInstance(Effects.WEAKNESS, 40, 1, false, false, true));
            }
            EntityUtils.clearInvulnerableTime(entity);
            smokesAround(entity, 8);
        });
        smokesAround(this, 16);
    }

    private void smokesAround(Entity entity, int count) {
        for (int i = 0; i < count; i++) {
            double x = getRandom().nextGaussian() * 0.02;
            double y = getRandom().nextGaussian() * 0.02;
            double z = getRandom().nextGaussian() * 0.02;
            ((ServerWorld) level).sendParticles(ParticleTypes.LARGE_SMOKE, entity.getX(1) - x * 10, entity.getRandomY() - y * 10, entity.getRandomZ(1) - z * 10, 1, 0, 0, 0, 0.02);
        }
    }

    public boolean isConstantlyDoingFireballAttack() {
        return constantlyDoFireballAttack;
    }

    public void setConstantlyDoFireballAttack(boolean constantlyDoFireballAttack) {
        this.constantlyDoFireballAttack = constantlyDoFireballAttack;
    }

    @Override
    public void setCustomName(@Nullable ITextComponent component) {
        super.setCustomName(component);
        if (component != null) {
            bossInfo.setName(component);
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
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ENERGIZED_BLAZE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ENERGIZED_BLAZE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENERGIZED_BLAZE_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 1.2f;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public Set<UUID> getKillers() {
        return killers;
    }

    @Override
    public float getAddKillerThreshold() {
        return SWITCH_ATTACK_TYPE_HEALTH;
    }

    public enum Phase implements IIdentifiableEnum, ISkippablePhase {
        FIRESTORM(0),
        FIREBALL(0),
        EXPLOSION(0.5);

        private final double skipProbability;

        Phase(double skipProbability) {
            this.skipProbability = skipProbability;
        }

        @Override
        public double getSkipProbability() {
            return skipProbability;
        }
    }
}
