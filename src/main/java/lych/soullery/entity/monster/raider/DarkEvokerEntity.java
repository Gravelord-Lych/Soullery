package lych.soullery.entity.monster.raider;

import com.google.common.collect.ImmutableList;
import lych.soullery.entity.iface.INecromancer;
import lych.soullery.entity.iface.ISpellCastable;
import lych.soullery.entity.Soul;
import lych.soullery.entity.ai.goal.CastingSpellGoal;
import lych.soullery.entity.ai.goal.DarkEvokerGoals.ConvertVillagerGoal;
import lych.soullery.entity.ai.goal.DarkEvokerGoals.InfectEnemiesGoal;
import lych.soullery.entity.ai.goal.DarkEvokerGoals.ReviveRaidersGoal;
import lych.soullery.entity.ai.goal.DarkEvokerGoals.ShootWitherSkullGoal;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Stream;

import static lych.soullery.entity.iface.ISpellCastable.SpellType.NONE;

public class DarkEvokerEntity extends AbstractIllagerEntity implements ISpellCastable, INecromancer<AbstractRaiderEntity, Soul<AbstractRaiderEntity>> {
    public static final SpellType INFECT = SpellType.create(1, 0.4, 0, 0.6);
    public static final SpellType WITHER_SKULL = SpellType.create(2, 0.2, 0.18, 0.16);
    public static final SpellType CONVERT = SpellType.create(3, 0.3, 0.75, 0.05);
    public static final SpellType REVIVE = SpellType.create(4, 0.95, 0.1, 0.7);
    public static final int MAX_REVIVE = 3;
    private static final DataParameter<Integer> DATA_SPELL_CASTING_ID = EntityDataManager.defineId(DarkEvokerEntity.class, DataSerializers.INT);
    private static final ImmutableList<SpellType> POSSIBLE_SPELLS = Stream.of(NONE, INFECT, WITHER_SKULL, CONVERT, REVIVE).sorted(Comparator.naturalOrder()).collect(ImmutableList.toImmutableList());

    private final Queue<Soul<AbstractRaiderEntity>> souls = new ArrayDeque<>(MAX_REVIVE);
    private SpellType currentSpell = SpellType.NONE;
    private int spellCastingTickCount;

    public DarkEvokerEntity(EntityType<? extends AbstractIllagerEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SPELL_CASTING_ID, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new CastingSpellGoal<>(this));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 4, 0.6, 0.9));
        goalSelector.addGoal(4, new InfectEnemiesGoal(this));
        goalSelector.addGoal(5, new ConvertVillagerGoal(this));
        goalSelector.addGoal(6, new ReviveRaidersGoal(this));
        goalSelector.addGoal(7, new ShootWitherSkullGoal(this));
        goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6));
        goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3, 1));
        goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, AbstractRaiderEntity.class).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isAlliedTo(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }
        if (super.isAlliedTo(entity)) {
            return true;
        }
        if (entity instanceof LivingEntity && ((LivingEntity) entity).getMobType() == CreatureAttribute.ILLAGER) {
            return getTeam() == null && entity.getTeam() == null;
        }
        return false;
    }

    public static boolean isRaider(LivingEntity entity) {
        return entity instanceof AbstractRaiderEntity;
    }

    @Override
    public void applyRaidBuffs(int wave, boolean alwaysFalse) {}

//    TODO: sound
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        if (effect.getEffect() == Effects.WITHER) {
            return EntityUtils.shouldApplyEffect(this, effect, false);
        }
        return super.canBeAffected(effect);
    }

    @Override
    public boolean isCastingSpell() {
        if (level.isClientSide()) {
            return getSpellId() > 0;
        }
        return spellCastingTickCount > 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ArmPose getArmPose() {
        if (isCastingSpell()) {
            return ArmPose.SPELLCASTING;
        }
        return this.isCelebrating() ? ArmPose.CELEBRATING : ArmPose.CROSSED;
    }

    @Override
    public SpellType getCurrentSpell() {
        return level.isClientSide() ? getCurrentSpellOnClient() : currentSpell;
    }

    @OnlyIn(Dist.CLIENT)
    private SpellType getCurrentSpellOnClient() {
        return POSSIBLE_SPELLS.get(getSpellId());
    }

    @Override
    public void setCastingSpell(SpellType type) {
        currentSpell = type;
        setSpellId(type.getId());
    }

    private int getSpellId() {
        return entityData.get(DATA_SPELL_CASTING_ID);
    }

    private void setSpellId(int id) {
        entityData.set(DATA_SPELL_CASTING_ID, id);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (spellCastingTickCount > 0) {
            spellCastingTickCount--;
        }
    }

    //  TODO: sound
    @Override
    public SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public int getSpellCastingTickCount() {
        return spellCastingTickCount;
    }

    @Override
    public void setSpellCastingTickCount(int spellCastingTickCount) {
        this.spellCastingTickCount = spellCastingTickCount;
    }

    @Override
    public double getReviveDistance() {
        return 20;
    }

    @Override
    public boolean canAddSoul(AbstractRaiderEntity raider, ServerWorld world) {
        if (raider instanceof DarkEvokerEntity) {
            return false;
        }
        return hasActiveRaid() && Objects.equals(getCurrentRaid(), raider.getCurrentRaid());
    }

    @Override
    public Queue<Soul<AbstractRaiderEntity>> getSouls() {
        return souls;
    }

    @Override
    public Soul<AbstractRaiderEntity> createSoul(AbstractRaiderEntity entity) {
        return new Soul<>(entity);
    }

    @Nullable
    @Override
    public Soul<AbstractRaiderEntity> loadSoul(CompoundNBT compoundNBT) {
        return Soul.load(compoundNBT);
    }

    @Override
    public boolean addSoulDirectly(Soul<AbstractRaiderEntity> soul) {
        boolean added = INecromancer.super.addSoulDirectly(soul);
        if (!added) {
            getSouls().poll();
            INecromancer.super.addSoulDirectly(soul);
        }
        return true;
    }
}
