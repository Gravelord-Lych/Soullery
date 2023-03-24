package lych.soullery.entity.monster.voidwalker;

import lych.soullery.entity.ai.goal.VoidwalkerGoals.AttackGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.RetreatGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.VoidwalkerRandomWalkingGoal;
import lych.soullery.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class VoidwalkerEntity extends AbstractVoidwalkerEntity {
    private int attackCooldown;

    public VoidwalkerEntity(EntityType<? extends VoidwalkerEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createVoidwalkerAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
                .add(Attributes.ATTACK_KNOCKBACK, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new AttackGoal(this, 1, true));
        goalSelector.addGoal(5, new RetreatGoal(this, 200));
        goalSelector.addGoal(8, new VoidwalkerRandomWalkingGoal(this, 0.8));
        goalSelector.addGoal(9, new LookRandomlyGoal(this));
    }

    @Override
    public boolean onSetTarget(LivingEntity target) {
        if (getSensing().canSee(target)) {
            return false;
        }
        return setSneakTarget(target.position());
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        setItemInHand(Hand.MAIN_HAND, createWeapon());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getAttackCooldown() > 0) {
            setAttackCooldown(getAttackCooldown() - 1);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            setAttackCooldown(60);
        }
        return hurt;
    }

    @Override
    public boolean canCreateWeapon() {
        return true;
    }

    @Override
    public ItemStack createWeapon() {
        return new ItemStack(ModItems.REFINED_SOUL_METAL_SWORD);
    }

    @Override
    public boolean isMeleeAttacker() {
        return true;
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("AttackCooldown", attackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("AttackCooldown", attackCooldown);
    }

    @Override
    protected void doStrengthenSelf(VoidwalkerTier tier, VoidwalkerTier oldTier, DifficultyInstance difficulty) {
        switch (tier) {
            case PARAGON:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(150);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(15);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.33);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(48);
                getNonnullAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(2);
                break;
            case ELITE:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(10);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.315);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(36);
                getNonnullAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(1.4);
                break;
            case EXTRAORDINARY:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(60);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30);
                break;
            default:
        }
        setHealth(getMaxHealth());
    }

    public double getAttackReachRadiusMultiplier() {
        switch (getTier()) {
            case PARAGON:
                return 2;
            case ELITE:
                return 1.5;
            case EXTRAORDINARY:
                return 1.2;
            case ORDINARY:
            default:
                return 1;
        }
    }
}
