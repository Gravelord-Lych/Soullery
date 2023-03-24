package lych.soullery.entity.monster.boss;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.entity.ai.goal.CastingSpellGoal;
import lych.soullery.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soullery.entity.ai.goal.boss.SkeletonKingGoals.*;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.iface.ITieredMob;
import lych.soullery.entity.monster.SkeletonFollowerEntity;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import static lych.soullery.entity.iface.ISpellCastable.SpellType.NONE;

/**
 * Skeleton King is the king of all {@link net.minecraft.entity.monster.SkeletonEntity skeletons}.
 * Unlike common skeletons, he is able to use spells to beat his enemies.<br>
 * Skeletons will be neutral to a player if the player killed the Skeleton King.<br>
 * <br>
 * Spells (T1):
 * <li><b>Skeleton Evocation: </b>Summon 2 {@link SkeletonFollowerEntity skeletons},
 *                                these skeletons will listen to their owner and attack players.</li>
 * <li><b>Dark Arrows: </b>Shoot 4 {@link lych.soullery.entity.projectile.FangsSummonerEntity arrows},
 *                         these arrows can summon fangs when they hit something.</li>
 * <li><b>Self Healing: </b>Heal 10 health, heal more in higher tiers.</li>
 * <br>
 * Abilities gained after tier 1:
 * <li><b>Tier 2: </b>Summon 1 more skeleton. Skeleton King's max health increases. </li>
 * <li><b>Tier 3: </b>Shoot more arrows and arrows summon more fangs. Skeleton King walks faster. </li>
 * <li><b>Tier 4: </b>Summoned skeletons had better weapons. Skeleton King has armors now. </li>
 * <li><b>Tier 5: </b><b>New Spell: Skeleton Boost!</b> All skeletons nearby will be boosted for a while.
 *                       Skeleton King's max health increases again. </li>
 * <li><b>Tier 6: </b>Summon another 1 skeleton. Skeleton King has better armors and his arrows are stronger. </li>
 * <li><b>Tier 7: </b>Skeleton King's max health greatly increases and all skeleton summoned by him will be armored. </li>
 * <li><b>Tier 8: </b>Skeleton boost gives skeletons stronger effects. Arrows and fangs they summoned deals more damage. </li>
 * <li><b>Tier 9: </b>Summon 2 more skeletons. Skeleton King's armor becomes tough and he has higher knockback resistance. </li>
 * <li><b>Tier 10: </b><b>New Spell: Skeleton Clone!</b> Skeleton King clones nearby skeletons and himself,
 *                        but cloned skeletons only have 1 health
 *                        (0.9 if {@link ExtraAbility#MONSTER_SABOTAGE monster sabotage}).
 *                        Skeleton King's comprehensive strength greatly improves as well.</li>
 * <li><b>Tier 11+: </b>Skeleton King's strength will improve per level.</li>
 *
 * @author Gravelord Lych
 */
public class SkeletonKingEntity extends AbstractSkeletonKingEntity {
    public static final float DAMAGE_THRESHOLD_T11 = 50;
    public static final float MIN_MAX_DAMAGE = 20;
    public static final SpellType SUMMON_SKELETON = SpellType.create(1, 0.95, 0.95, 0.95);
    public static final SpellType FANGS = SpellType.create(2, 0.4, 0.3, 0.35);
    public static final SpellType HEAL = SpellType.create(3, 0.8, 0.05, 0);
    public static final SpellType BOOST_SKELETON = SpellType.create(4, 0.8, 0.6, 0.2);
    public static final SpellType CLONE_SKELETON = SpellType.create(5, 0.3, 0.75, 0.2);

    private static final int BASE_XP_REWARD = 200;
    private static final double XP_MULTIPLIER = 1.1;
    private static final int MAX_XP_REWARD = 100000;

    private static final double ARMOR = 0;
    private static final double ARMOR_T4 = 6;
    private static final double ARMOR_T6 = 12;
    private static final double ARMOR_T10_OR_ABOVE = 15;
    private static final Int2DoubleMap ARMOR_MAP = EntityUtils.doubleChoiceBuilder().range(1, 3).value(ARMOR).range(4, 5).value(ARMOR_T4).range(6, 9).value(ARMOR_T6).build();

    private static final double ARMOR_TOUGHNESS = 0;
    private static final double ARMOR_TOUGHNESS_T9_OR_ABOVE = 4;

    private static final double MAX_HEALTH = 200;
    private static final double MAX_HEALTH_T2 = 220;
    private static final double MAX_HEALTH_T5 = 250;
    private static final double MAX_HEALTH_T7 = 300;
    private static final double MAX_HEALTH_T10 = 400;
    private static final double HEALTH_STEP = 10;
    private static final double MAX_MAX_HEALTH = 800;
    private static final Int2DoubleMap HEALTH_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(MAX_HEALTH).range(2, 4).value(MAX_HEALTH_T2).range(5, 6).value(MAX_HEALTH_T5).range(7, 9).value(MAX_HEALTH_T7).build();

    private static final double KNOCKBACK_RESISTANCE = 0;
    private static final double KNOCKBACK_RESISTANCE_T9_OR_ABOVE = 0.75;

    private static final double SPEED = 0.25;
    private static final double SPEED_T3 = 0.28;
    private static final double SPEED_T10_OR_ABOVE = 0.3;

    private static final double FOLLOW_RANGE = 30;
    private static final ImmutableList<SpellType> POSSIBLE_SPELLS = Stream.of(NONE, SUMMON_SKELETON, FANGS, HEAL, BOOST_SKELETON, CLONE_SKELETON).sorted(Comparator.naturalOrder()).collect(ImmutableList.toImmutableList());

    protected final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
    private boolean killedByCommand;

    public SkeletonKingEntity(EntityType<? extends SkeletonKingEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, SPEED);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new CastingSpellGoal<>(this));
        EntityUtils.directlyAddGoal(goalSelector, Goals.of(new HealSelfGoal(this)).getAsPrioritized(() -> getHealth() < getMaxHealth() * 0.5f ? 3 : 5));
        goalSelector.addGoal(4, new SummonSkeletonGoal(this));
        goalSelector.addGoal(6, new CloneSkeletonGoal(this));
        goalSelector.addGoal(7, new BoostSkeletonGoal(this));
        goalSelector.addGoal(8, new ArrowAttackGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof AbstractSkeletonEntity) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof AbstractSkeletonEntity) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected SpellType getCurrentSpellOnClient() {
        return POSSIBLE_SPELLS.get(getSpellId());
    }

    @Override
    protected void onSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        super.onSpawn(world, instance, reason, data, compoundNBT);
        EntityUtils.getAttribute(this, Attributes.MOVEMENT_SPEED).setBaseValue(getCorrectSpeed());
        EntityUtils.getAttribute(this, Attributes.ARMOR).setBaseValue(getCorrectArmor());
        EntityUtils.getAttribute(this, Attributes.ARMOR_TOUGHNESS).setBaseValue(getCorrectArmorToughness());
        EntityUtils.getAttribute(this, Attributes.MAX_HEALTH).setBaseValue(getCorrectMaxHealth());
        EntityUtils.getAttribute(this, Attributes.KNOCKBACK_RESISTANCE).setBaseValue(getCorrectKnockbackResistance());
        setHealth(getMaxHealth());
    }

    @Override
    public void handleDeath(DamageSource source) {
        if (!killedByCommand) {
            super.handleDeath(source);
        }
        xpReward = reachedTier(100) ? MAX_XP_REWARD : (int) Math.min(Math.round(BASE_XP_REWARD * Math.pow(XP_MULTIPLIER, getTier())), MAX_XP_REWARD);
    }

    private double getCorrectArmor() {
        return ARMOR_MAP.getOrDefault(getTier(), ARMOR_T10_OR_ABOVE);
    }

    private double getCorrectArmorToughness() {
        return reachedTier(9) ? ARMOR_TOUGHNESS_T9_OR_ABOVE : ARMOR_TOUGHNESS;
    }

    private double getCorrectMaxHealth() {
        if (reachedTier(10)) {
            return Math.min(MAX_HEALTH_T10 + HEALTH_STEP * (getTier() - 10), MAX_MAX_HEALTH);
        }
        return HEALTH_MAP.get(getTier());
    }

    private double getCorrectKnockbackResistance() {
        return reachedTier(9) ? KNOCKBACK_RESISTANCE_T9_OR_ABOVE : KNOCKBACK_RESISTANCE;
    }

    private double getCorrectSpeed() {
        if (reachedTier(10)) {
            return SPEED_T10_OR_ABOVE;
        }
        return reachedTier(3) ? SPEED_T3 : SPEED;
    }

    @Override
    public SoundEvent getCastingSoundEvent() {
//      TODO: sound
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected SoundEvent getStepSound() {
//      TODO: sound
        return SoundEvents.SKELETON_STEP;
    }

    @Override
    public void tick() {
        super.tick();
        tickBossInfo();
    }

    protected void tickBossInfo() {
        if (!level.isClientSide()) {
            bossInfo.setName(EntityUtils.getBossNameFor(this));
            bossInfo.setPercent(getHealth() / getMaxHealth());
        }
    }

    public final boolean isCloned() {
        return this instanceof Cloned;
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        resetBossInfoName(name);
    }

    protected void resetBossInfoName(@Nullable ITextComponent name) {
        if (name != null) {
            bossInfo.setName(EntityUtils.getBossNameFor(name, getTier()));
        }
    }

    @Override
    public boolean fireImmune() {
        return super.fireImmune() || reachedTier(10);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier) {
        if (reachedTier(5)) {
            return false;
        }
        return super.causeFallDamage(distance, multiplier);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (reachedTier(15) && equals(target)) {
            BossLoggers.LOGGER.warn("A developer want to set a T15+ Skeleton King's target to himself, that won't work!");
            return;
        }
        super.setTarget(target);
    }

    @Override
    public void kill() {
        killedByCommand = true;
        super.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        if (!isCloned()) {
            bossInfo.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        if (!isCloned()) {
            bossInfo.removePlayer(player);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("KilledByCommand", killedByCommand);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        killedByCommand = compoundNBT.getBoolean("KilledByCommand");
    }

    @Override
    public float getDamageMultiplier() {
        if (reachedTier(11)) {
            return getTier() * 0.1f;
        }
        return 1;
    }

    public static class Cloned extends SkeletonKingEntity implements IHasOwner<SkeletonKingEntity> {
        @Nullable
        private UUID ownerUUID;

        public Cloned(EntityType<? extends Cloned> skeleton, World world) {
            super(skeleton, world);
            xpReward = 5;
        }

        @Override
        protected void registerGoals() {
            super.registerGoals();
            targetSelector.addGoal(0, new CopyOwnerTargetGoal<>(this));
        }

        @Override
        public void handleHurt(DamageSource source) {
            if (getOwner() != null) {
                getOwner().handleHurt(source);
            }
        }

        @Override
        public void handleDeath(DamageSource source) {}

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
        public int getTier() {
            return getOwner() == null ? ITieredMob.MIN_TIER : getOwner().getTier();
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
        protected void onSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
            if (reason != SpawnReason.MOB_SUMMONED) {
                remove();
                return;
            }
            super.onSpawn(world, instance, reason, data, compoundNBT);
        }

        @Override
        public float getResistance() {
            return 0;
        }

        @Override
        public float getDamageMultiplier() {
            return getOwner() == null ? 1 : getOwner().getDamageMultiplier();
        }
    }
}
