package lych.soullery.entity.monster.voidwalker;

import com.google.common.collect.ImmutableList;
import lych.soullery.api.shield.ISharedShield;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.FollowVoidwalkerGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.HealOthersGoal;
import lych.soullery.extension.shield.SharedShield;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.RedstoneParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VoidDefenderEntity extends VoidwalkerEntity {
    static final double PROTECTIVE_RANGE = 6;
    static final float PROTECTED_DAMAGE_MULTIPLIER = 0.8f;
    private static final double HEAL_RANGE = 5;
    private static final int HEAL_INTERVAL = 30;
    private static final float HEAL_AMOUNT = 2;
    private static final float HEAL_AMOUNT_ELITE = 3;

    @Nullable
    private ISharedShield myShield;
    private boolean shieldValid = true;

    public VoidDefenderEntity(EntityType<? extends VoidDefenderEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createVoidwalkerAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 5);
    }

    @Override
    protected void syncShield() {
        entityData.set(DATA_SHIELDED, getRealShieldProvider() != null && getRealShieldProvider().getSharedShield() != null);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new FollowVoidwalkerGoal<>(this, VoidwalkerEntity.class, 10));
        goalSelector.addGoal(4, new FollowVoidwalkerGoal<>(this, VoidArcherEntity.class, 20));
        goalSelector.addGoal(6, new HealOthersGoal(this, HEAL_RANGE, HEAL_INTERVAL));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getSharedShield() != null && !isShieldValid() && getSharedShield().getHealth() > getSharedShield().getPassiveDefense() * 0.5) {
            shieldValid = true;
            onShieldRegenerated();
        }
    }

    @Override
    public void doHealTarget(AbstractVoidwalkerEntity healTarget) {
        healTarget.heal(getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? HEAL_AMOUNT_ELITE : HEAL_AMOUNT);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {}

    @Override
    protected void doStrengthenSelf(VoidwalkerTier tier, VoidwalkerTier oldTier, DifficultyInstance difficulty) {
        float absoluteDefense = 0;
        float regenAmount = 1;
        switch (tier) {
            case PARAGON:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(15);
                getNonnullAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(3);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(48);
                getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
                regenAmount = 2;
                absoluteDefense = 1;
                break;
            case ELITE:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(65);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(10);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(36);
                getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8);
                absoluteDefense = 1;
                break;
            case EXTRAORDINARY:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(35);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30);
                getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6);
                break;
            default:
        }
        float passiveDefense = getMaxHealth();
        setHealth(getMaxHealth());
        myShield = new SharedShield(absoluteDefense, passiveDefense, 40, regenAmount, false);
    }

    @Override
    public boolean isLowHealth(LivingEntity entity) {
        if (entity == this) {
            return !isShieldValid() && getHealth() < getMaxHealth() * 0.5f;
        }
        return super.isLowHealth(entity);
    }

    @Override
    public double getAttackReachRadiusMultiplier() {
        switch (getTier()) {
            case PARAGON:
                return 1.5;
            case ELITE:
                return 1.2;
            case EXTRAORDINARY:
            case ORDINARY:
            default:
                return 1;
        }
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Nullable
    @Override
    public ISharedShield getSharedShield() {
        if (getRealShieldProvider() != null && getRealShieldProvider().getSharedShield() != null) {
            return getRealShieldProvider().getSharedShield();
        }
        return myShield;
    }

    @NotNull
    @Override
    public IShieldUser getShieldProvider() {
        IShieldUser provider = getRealShieldProvider();
        return provider == null ? this : provider;
    }

    @Override
    public void setSharedShield(@Nullable ISharedShield sharedShield) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasConsumableShield() {
        return myShield != null && myShield.canBeConsumed();
    }

    @Override
    public boolean isShieldValid() {
        return super.isShieldValid() && shieldValid;
    }

    @Override
    public boolean showHitParticles(DamageSource source, float amount) {
        return true;
    }

    @Override
    public void onShieldExhausted() {
        if (!level.isClientSide()) {
            shieldValid = false;
            ((ServerWorld) level).sendParticles(ParticleTypes.EXPLOSION, getRandomX(1), getY(0.4 + random.nextDouble() * 0.2), getRandomZ(1), 1, 0, 0, 0, 0);
        }
    }

    private void onShieldRegenerated() {
        EntityUtils.addParticlesAroundSelfServerside(this, (ServerWorld) level, RedstoneParticles.CYAN, 6 + random.nextInt(5));
    }

    @Override
    public List<ISharedShield> getAllShields() {
        ImmutableList.Builder<ISharedShield> builder = ImmutableList.builder();
        if (getRealShieldProvider() != null && getRealShieldProvider().getSharedShield() != null) {
            builder.add(getRealShieldProvider().getSharedShield());
        }
        if (myShield != null) {
            builder.add(myShield);
        }
        return builder.build();
    }

    @Nullable
    private IShieldUser getRealShieldProvider() {
        return super.getShieldProvider();
    }

    @Nullable
    @Override
    public ISharedShield getMainShield() {
        return myShield;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        if (!level.isClientSide() && getSharedShield() != null) {
            compoundNBT.put("MySharedShield", getSharedShield().save());
        }
        compoundNBT.putBoolean("ShieldValid", isShieldValid());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (!level.isClientSide() && compoundNBT.contains("MySharedShield")) {
            myShield = new SharedShield(compoundNBT.getCompound("MySharedShield"));
        }
        if (compoundNBT.contains("ShieldValid")) {
            shieldValid = compoundNBT.getBoolean("ShieldValid");
        }
    }
}
