package lych.soullery.entity.monster.voidwalker;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.FindTargetExpiringGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.HealOthersGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.RetreatGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.VoidwalkerRandomWalkingGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.potion.ModPotions;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ModEffectUtils;
import lych.soullery.util.WeightedRandom;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoidAlchemistEntity extends AbstractVoidwalkerEntity implements IRangedAttackMob {
    static final float PROTECTED_DAMAGE_MULTIPLIER = 0.5f;
    static final double PROTECTIVE_RANGE = 8;
    private static final double HEAL_RANGE = 5;
    private static final int HEAL_INTERVAL = 60;
    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("852B017E-EC50-BB62-92B4-94ADB10CD67F");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25, AttributeModifier.Operation.ADDITION);
    private static final DataParameter<Boolean> DATA_USING_POTION = EntityDataManager.defineId(VoidAlchemistEntity.class, DataSerializers.BOOLEAN);
    private final List<PotionPicker> potions = new ArrayList<>();
    private final List<TargetablePotionPicker> healablePotions = new ArrayList<>();
    private final List<TargetablePotionPicker> attackablePotions = new ArrayList<>();
    private FindTargetExpiringGoal<AbstractVoidwalkerEntity> healVoidwalkersGoal;
    private PotionPicker activePotion;
    private int usingTime;
    private boolean canAttack;

    {
        potions.add(new WaterBreathingPotionPicker());
        potions.add(new FireResistancePotionPicker());
        potions.add(new HealingPotionPicker());
        potions.add(new SwiftnessPotionPicker());
        potions.add(new TurtleMasterPotionPicker());
        potions.add(new RegenerationPotionPicker());
        healablePotions.add(new HealFriendlyPotionPicker());
        healablePotions.add(new StrengthenFriendlyPotionPicker());
        healablePotions.add(new GiveFriendlyResistancePotionPicker());
        attackablePotions.add(new SlowdownEnemyPotionPicker());
        attackablePotions.add(new WeakenEnemyPotionPicker());
        attackablePotions.add(new PoisonEnemyPotionPicker());
        attackablePotions.add(new HarmEnemyPotionPicker());
        attackablePotions.add(new StarvationPotionPicker());
        attackablePotions.add(new ReversionPotionPicker());
        attackablePotions.add(new LevitationPotionPicker());
        attackablePotions.add(new UltraboostPotionPicker());
        attackablePotions.add(new DecayEnemyPotionPicker());
        attackablePotions.add(new ComprehensivelyHarmEnemyPotionPicker());
    }

    public VoidAlchemistEntity(EntityType<? extends VoidAlchemistEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createVoidwalkerAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_USING_POTION, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        healVoidwalkersGoal = new FindTargetExpiringGoal<>(this, AbstractVoidwalkerEntity.class, 100, true, false, entity -> entity != null && entity.getType() != ModEntities.VOID_ALCHEMIST);
        goalSelector.addGoal(2, new RangedAttackGoal(this, 1, 60, 10));
        EntityUtils.directlyAddGoal(goalSelector, Goals.of(new RetreatGoal(this, 150)).getAsPrioritized(() -> isLowHealth() ? 1 : 4));
        goalSelector.addGoal(5, Goals.of(new MoveTowardsTargetGoal(this, 1.2, 48)).executeIf(() -> getAttributeValue(Attributes.FOLLOW_RANGE) > 30).get());
        goalSelector.addGoal(6, new HealOthersGoal(this, HEAL_RANGE, HEAL_INTERVAL));
        goalSelector.addGoal(8, new VoidwalkerRandomWalkingGoal(this, 0.75));
        goalSelector.addGoal(9, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, healVoidwalkersGoal);
    }

    @Override
    public boolean isMeleeAttacker() {
        return false;
    }

    @Override
    public boolean canCreateWeapon() {
        return false;
    }

    @Override
    public ItemStack createWeapon() {
        return ItemStack.EMPTY;
    }

    @Override
    public void doHealTarget(AbstractVoidwalkerEntity healTarget) {
        Optional<EffectInstance> effect = healTarget.getActiveEffects().stream().filter(ModEffectUtils::isHarmful).reduce(ModEffectUtils::chooseStronger);
        effect.map(EffectInstance::getEffect).ifPresent(healTarget::removeEffect);
    }

    @Override
    public boolean canHeal(AbstractVoidwalkerEntity voidwalker) {
        return voidwalker.getActiveEffects().stream().anyMatch(ModEffectUtils::isHarmful);
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float amount) {
        amount = super.getDamageAfterMagicAbsorb(source, amount);
        if (source.getEntity() == this) {
            amount = 0;
        }
        if (source.isMagic()) {
            amount *= 0.1f;
        }
        return amount;
    }

    @Override
    public EntityPredicate customizeTargetConditions(EntityPredicate targetConditions) {
        return targetConditions.allowSameTeam();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level.isClientSide() && isAlive()) {
            healVoidwalkersGoal.decrementCooldown();
            setCanAttack(healVoidwalkersGoal.getCooldown() <= FindTargetExpiringGoal.CAN_ATTACK_COOLDOWN);

            if (isUsingPotion()) {
                usingTime--;
                if (usingTime <= 0) {
                    setUsingPotion(false);
                    ItemStack stack = getMainHandItem();
                    setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                    if (stack.getItem() == Items.POTION) {
                        activePotion.applyEffects(stack).stream().map(EffectInstance::new).forEach(this::addEffect);
                    }
                    EntityUtils.getAttribute(this, Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
                }
            } else {
                if (!isEthereal()) {
                    Potion potion = null;
                    for (PotionPicker picker : potions) {
                        if (picker.check()) {
                            potion = picker.getType();
                            activePotion = picker;
                            break;
                        }
                    }
                    if (potion != null) {
                        setItemSlot(EquipmentSlotType.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
                        usingTime = getMainHandItem().getUseDuration();
                        setUsingPotion(true);
                        if (!isSilent()) {
    //                      TODO - sound
                            level.playSound(null, getX(), getY(), getZ(), SoundEvents.WITCH_DRINK, getSoundSource(), 1, 0.8f + random.nextFloat() * 0.4f);
                        }
                        ModifiableAttributeInstance speed = EntityUtils.getAttribute(this, Attributes.MOVEMENT_SPEED);
                        speed.removeModifier(SPEED_MODIFIER_DRINKING);
                        speed.addTransientModifier(SPEED_MODIFIER_DRINKING);
                    }
                }
            }
            if (random.nextInt(20) == 0) {
                tryToSneakIfCannotReachAttackTarget();
            }
        }
    }

    private boolean enableAdvancedPotionPickers() {
        return getTier().strongerThan(VoidwalkerTier.ORDINARY);
    }

    private boolean strengthenHealAbility() {
        return getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY);
    }

    private boolean strengthenHarmAbility() {
        return getTier().strongerThan(VoidwalkerTier.ELITE);
    }

    private void tryToSneakIfCannotReachAttackTarget() {
        if (getTarget() != null && !EntityUtils.canReach(this, getTarget())) {
            if (setSneakTarget(tryToFindSneakTarget(getTarget()))) {
                setEtherealCooldown(LONG_ETHEREAL_COOLDOWN);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (isUsingPotion()) {
            return;
        }

        Vector3d movement = target.getDeltaMovement();
        double tx = target.getX() + movement.x - getX();
        double ty = target.getEyeY() - 1.1 - getY();
        double tz = target.getZ() + movement.z - getZ();
        float dist = MathHelper.sqrt(tx * tx + tz * tz);

        TargetType type = getTypeOf(target);
        boolean heal = type == TargetType.FRIENDLY;
        List<TargetablePotionPicker> potions;

        if (heal) {
            potions = healablePotions.stream().filter(p -> p.canUse(target)).collect(Collectors.toList());
        } else {
            potions = attackablePotions.stream().filter(p -> p.canUse(target)).collect(Collectors.toList());
        }
        if (potions.isEmpty()) {
            return;
        }

        Pair<List<Potion>, Integer> pair = WeightedRandom.getRandomItem(random, potions).findPotion(target, type);
        boolean lingering = pair.getSecond() > 0;

        for (Potion potion : pair.getFirst()) {
            PotionEntity potionEntity = new PotionEntity(level, this);
            potionEntity.setItem(PotionUtils.setPotion(new ItemStack(lingering ? Items.LINGERING_POTION : Items.SPLASH_POTION), potion));
            potionEntity.xRot -= -20;
            potionEntity.shoot(tx, ty + (double) (dist * 0.2F), tz, 0.75F, 8);
            if (!isSilent()) {
//            TODO - sound
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.WITCH_THROW, getSoundSource(), 1, 0.8f + random.nextFloat() * 0.4f);
            }
            level.addFreshEntity(potionEntity);
        }

        setAdjustTarget(true);
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        if (ModEffectUtils.isHarmful(effect)) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    public boolean isUsingPotion() {
        return entityData.get(DATA_USING_POTION);
    }

    public void setUsingPotion(boolean usingPotion) {
        entityData.set(DATA_USING_POTION, usingPotion);
    }

    @Override
    public boolean onSetTarget(LivingEntity target) {
        if (distanceToSqr(target) <= 10 * 10 && EntityUtils.canReach(this, target)) {
            return false;
        }
        Vector3d finalPos = tryToFindSneakTarget(target);
        return setSneakTarget(finalPos);
    }

    private Vector3d tryToFindSneakTarget(LivingEntity target) {
        Vector3d finalPos = null;
        for (int i = 0; i < 5; i++) {
            finalPos = RandomPositionGenerator.getPosTowards(this, 6, 6, target.position());
            if (finalPos != null && finalPos.distanceToSqr(target.position()) <= 5 * 5) {
                break;
            }
        }
        if (finalPos == null) {
            finalPos = target.position();
        }
        return finalPos;
    }

    public ScanData scanAroundTarget(LivingEntity target) {
        return scanAroundTarget(target, 5);
    }

    public ScanData scanAroundTarget(LivingEntity target, double range) {
        List<LivingEntity> enemies = target.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range));
        enemies.removeIf(mob -> ESVMob.isESVMob(mob) || distanceToSqr(mob) > range * range);
        List<MobEntity> friendlies = target.level.getEntitiesOfClass(MobEntity.class, target.getBoundingBox().inflate(range));
        friendlies.removeIf(mob -> ESVMob.nonESVMob(mob) || distanceToSqr(mob) > range * range);
        return new ScanData(enemies, friendlies);
    }

    private static TargetType getTypeOf(LivingEntity target) {
        if (ESVMob.isESVMob(target)) {
            return TargetType.FRIENDLY;
        }
        if (target.getMobType() == CreatureAttribute.UNDEAD) {
            return TargetType.UNDEAD;
        }
        return TargetType.ENEMY;
    }

    @Override
    public boolean canAttackAllMobs() {
        return super.canAttackAllMobs() && canAttack();
    }

    @Override
    public boolean canAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public class HealFriendlyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return target.getHealth() < target.getMaxHealth();
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type == TargetType.FRIENDLY);
            Potion healingPotion = isLowHealth(target) ? Potions.STRONG_HEALING : Potions.HEALING;
            List<Potion> potions;
            if (target.getHealth() > target.getMaxHealth() * 0.8) {
                potions = mayThrowExtra(strengthenHealAbility() || randomBoolean(0.4) ? Potions.LONG_REGENERATION : Potions.REGENERATION, healingPotion);
            } else {
                potions = mayThrowExtra(healingPotion, strengthenHealAbility() || randomBoolean(0.4) ? Potions.STRONG_REGENERATION : Potions.REGENERATION);
            }
            ScanData data = scanAroundTarget(target);
            boolean lingering = data.getEnemies().isEmpty() && data.getFriendlies().size() > 2 && random.nextBoolean();
            return Pair.of(potions, lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 120;
        }
    }

    public class StrengthenFriendlyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return target.getHealth() >= target.getMaxHealth() * 0.3;
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type == TargetType.FRIENDLY);

            ScanData data = scanAroundTarget(target);
            boolean lingering = data.getEnemies().isEmpty() && data.getFriendlies().size() > 2 && random.nextBoolean();

            Potion potion;
            boolean strong = hasDuplicate(Potions.STRENGTH, target);
            boolean fast = hasDuplicate(Potions.STRONG_SWIFTNESS, target);
            if (strong && !fast) {
                potion = Potions.STRONG_SWIFTNESS;
            } else if (fast) {
                potion = Potions.STRENGTH;
            } else {
                double strengthProb = 0.6;
                if (!data.getEnemies().isEmpty()) {
                    strengthProb += data.getEnemies().size() * 0.2;
                }

                ScanData largerData = scanAroundTarget(target, 12);
                if (largerData.getEnemies().isEmpty()) {
                    strengthProb -= 0.5;
                }
                potion = randomBoolean(strengthProb) ? Potions.STRENGTH : Potions.STRONG_SWIFTNESS;
            }
            if (potion == Potions.STRENGTH && strengthenHealAbility()) {
                potion = Potions.STRONG_STRENGTH;
            }
            return Pair.of(mayThrowExtra(potion, strengthenHealAbility() ? Potions.LONG_SWIFTNESS : Potions.SWIFTNESS), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 80;
        }
    }

    public class GiveFriendlyResistancePotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return target.isOnFire() && !hasDuplicate(Potions.FIRE_RESISTANCE, target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            ScanData data = scanAroundTarget(target);
            boolean lingering = data.getEnemies().isEmpty() && (data.getFriendlies().size() > 2 || randomBoolean(0.2));
            return Pair.of(ImmutableList.of(strengthenHealAbility() ? Potions.LONG_FIRE_RESISTANCE : Potions.FIRE_RESISTANCE), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 300;
        }
    }

    public class ReversionPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return target instanceof PlayerEntity && enableAdvancedPotionPickers() && !hasDuplicate(ModPotions.REVERSION.get(), target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            boolean lingering = randomBoolean(0.3);
            return Pair.of(ImmutableList.of(strengthenHarmAbility() ? ModPotions.LONG_REVERSION.get() : ModPotions.REVERSION.get()), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 50;
        }
    }

    public class LevitationPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return target instanceof WitchEntity || enableAdvancedPotionPickers() && !hasDuplicate(ModPotions.LEVITATION.get(), target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            boolean lingering = target.getMainHandItem().getItem() instanceof SwordItem && randomBoolean(0.3);
            return Pair.of(ImmutableList.of(strengthenHarmAbility() || target instanceof WitchEntity ? ModPotions.STRONG_LEVITATION.get() : ModPotions.LEVITATION.get()), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 50;
        }
    }

    public class StarvationPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return target instanceof PlayerEntity && enableAdvancedPotionPickers() && !hasDuplicate(ModPotions.STRONG_STARVATION.get(), target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            assertTrue(target instanceof PlayerEntity);
            @SuppressWarnings("ConstantConditions")
            PlayerEntity playerTarget = (PlayerEntity) target;
            boolean strongTarget = !playerTarget.getFoodData().needsFood() || randomBoolean(0.4);
            if (strengthenHarmAbility()) {
                strongTarget = true;
            }
            boolean lingering = randomBoolean(0.3);
            return Pair.of(ImmutableList.of(strongTarget ? (random.nextBoolean() ? ModPotions.STRONG_STARVATION.get() : ModPotions.LONG_STARVATION.get()) : ModPotions.STARVATION.get()), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 80;
        }
    }

    public class UltraboostPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return target instanceof PlayerEntity && enableAdvancedPotionPickers() && !hasDuplicate(ModPotions.ULTRABOOST.get(), target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
//          Ultraboost potion cannot be lingering.
            return Pair.of(ImmutableList.of(strengthenHarmAbility() ? ModPotions.STRONG_ULTRABOOST.get() : ModPotions.ULTRABOOST.get()), 0);
        }

        @Override
        public int getWeight() {
            return 50;
        }
    }

    public class SlowdownEnemyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return distanceToSqr(target) > 8 * 8 && !hasDuplicate(Potions.SLOWNESS, target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            boolean lingering = randomBoolean(0.3) && scanAroundTarget(target).getFriendlies().isEmpty();
            return Pair.of(ImmutableList.of(getRandom().nextBoolean() ? Potions.STRONG_SLOWNESS : Potions.LONG_SLOWNESS), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 100;
        }
    }

    public class WeakenEnemyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            if (target instanceof WitchEntity) {
                return randomBoolean(0.15) && distanceToSqr(target) < 3 * 3 && !hasDuplicate(Potions.WEAKNESS, target);
            }
            return distanceToSqr(target) < 6 * 6 && !hasDuplicate(Potions.WEAKNESS, target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            boolean lingering = randomBoolean(0.3) && scanAroundTarget(target).getFriendlies().isEmpty();
            return Pair.of(mayThrowExtra(strengthenHarmAbility() ? ModPotions.STRONG_WEAKNESS.get() : Potions.WEAKNESS, Potions.SLOWNESS), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 60;
        }
    }

    public class PoisonEnemyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            if (getTypeOf(target) == TargetType.UNDEAD) {
                return false;
            }
            if (target instanceof WitchEntity) {
                return randomBoolean(0.2);
            }
            return target.getHealth() >= target.getMaxHealth() * 0.3 && !hasDuplicate(Potions.POISON, target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            boolean lingering = randomBoolean(0.3) && scanAroundTarget(target).getFriendlies().isEmpty();
            boolean strongTarget = target.getHealth() > target.getMaxHealth() * 0.8 || randomBoolean(0.4);
            if (strengthenHarmAbility()) {
                strongTarget = true;
            }
            return Pair.of(mayThrowExtra(strongTarget ? Potions.STRONG_POISON : Potions.POISON, type == TargetType.UNDEAD ? Potions.HEALING : Potions.HARMING), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 80;
        }
    }

    public class DecayEnemyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return !hasDuplicate(ModPotions.DECAY.get(), target);
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            boolean lingering = randomBoolean(0.3) && scanAroundTarget(target).getFriendlies().isEmpty();
            boolean strongTarget = target.getHealth() > target.getMaxHealth() * 0.8 || randomBoolean(0.4);
            if (strengthenHarmAbility()) {
                strongTarget = true;
            }
            return Pair.of(mayThrowExtra(strongTarget ? ModPotions.STRONG_DECAY.get() : ModPotions.DECAY.get(), type == TargetType.UNDEAD ? Potions.HEALING : Potions.HARMING), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return 65;
        }
    }

    public class HarmEnemyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return true;
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            boolean lingering = randomBoolean(0.1) && scanAroundTarget(target).getFriendlies().isEmpty();
            boolean strongTarget = target.getHealth() > target.getMaxHealth() * 0.8 || randomBoolean(0.4);
            if (strengthenHarmAbility()) {
                strongTarget = true;
            }
            if (type == TargetType.UNDEAD) {
                return Pair.of(ImmutableList.of(strongTarget ? Potions.STRONG_HEALING : Potions.HEALING), lingering ? 100 : 0);
            } else {
                return Pair.of(ImmutableList.of(strongTarget ? Potions.STRONG_HARMING : Potions.HARMING), lingering ? 100 : 0);
            }
        }

        @Override
        public int getWeight() {
            return 120;
        }
    }

    public class ComprehensivelyHarmEnemyPotionPicker extends TargetablePotionPicker {
        @Override
        public boolean canUse(LivingEntity target) {
            return enableAdvancedPotionPickers();
        }

        @Override
        public Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type) {
            assertTrue(type != TargetType.FRIENDLY);
            ImmutableList.Builder<Potion> builder = ImmutableList.builder();
            boolean strongTarget = target.getHealth() > target.getMaxHealth() * 0.8 && random.nextBoolean() || randomBoolean(0.1);
            if (strengthenHarmAbility() && !strongTarget) {
                strongTarget = random.nextBoolean();
            }
            if (type == TargetType.UNDEAD) {
                builder.add(strongTarget ? Potions.STRONG_HEALING : Potions.HEALING);
            } else {
                builder.add(strongTarget ? Potions.STRONG_HARMING : Potions.HARMING);
            }
            if (random.nextBoolean()) {
                builder.add(strongTarget ? ModPotions.STRONG_WEAKNESS.get() : Potions.WEAKNESS);
                builder.add(strongTarget ? (random.nextBoolean() ? Potions.STRONG_SLOWNESS : Potions.LONG_SLOWNESS) : Potions.SLOWNESS);
            } else {
                builder.add(strongTarget ? Potions.STRONG_POISON : Potions.POISON);
                builder.add(strongTarget ? ModPotions.STRONG_DECAY.get() : ModPotions.DECAY.get());
            }
            boolean lingering = randomBoolean(0.25) && scanAroundTarget(target, 9).getFriendlies().isEmpty();
            return Pair.of(builder.build(), lingering ? 100 : 0);
        }

        @Override
        public int getWeight() {
            return strengthenHarmAbility() ? 100 : 20;
        }
    }

    public abstract class TargetablePotionPicker implements WeightedRandom.Item {
        private static final double THROW_EXTRA_PROBABILITY_PARAGON = 1.5;
        private static final double THROW_EXTRA_PROBABILITY_ELITE = 1;
        private static final double THROW_EXTRA_PROBABILITY_EXTRAORDINARY = 0.6;
        private static final double THROW_EXTRA_PROBABILITY = 0.3;

        public abstract boolean canUse(LivingEntity target);

        public abstract Pair<List<Potion>, Integer> findPotion(LivingEntity target, TargetType type);

        protected void assertTrue(boolean expression) {
            if (!expression) {
                throw new AssertionError();
            }
        }

        protected ImmutableList<Potion> mayThrowExtra(Potion potion, Potion anotherPotion) {
            double throwExtraAbility;
            if (getTier().strongerThan(VoidwalkerTier.ELITE)) {
                throwExtraAbility = THROW_EXTRA_PROBABILITY_PARAGON;
            } else if (getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY)) {
                throwExtraAbility = THROW_EXTRA_PROBABILITY_ELITE;
            } else if (getTier().strongerThan(VoidwalkerTier.ORDINARY)) {
                throwExtraAbility = THROW_EXTRA_PROBABILITY_EXTRAORDINARY;
            } else {
                throwExtraAbility = THROW_EXTRA_PROBABILITY;
            }
            ImmutableList.Builder<Potion> builder = ImmutableList.builder();
            builder.add(potion);
            while (randomBoolean(throwExtraAbility)) {
                builder.add(anotherPotion);
                throwExtraAbility--;
            }
            return builder.build();
        }

        protected boolean hasDuplicate(Potion potion, LivingEntity entity) {
            return potion.getEffects().stream().map(EffectInstance::getEffect).anyMatch(entity::hasEffect);
        }

        protected boolean randomBoolean(double trueProb) {
            return getRandom().nextDouble() < trueProb;
        }
    }

    public class WaterBreathingPotionPicker extends PotionPicker {
        @Override
        protected double getPickProbability() {
            return 0.25;
        }

        @Override
        protected boolean canUse() {
            return isEyeInFluid(FluidTags.WATER);
        }

        @Override
        public Potion getType() {
            return strengthenHealAbility() ? Potions.LONG_WATER_BREATHING : Potions.WATER_BREATHING;
        }
    }

    public class FireResistancePotionPicker extends PotionPicker {
        @Override
        protected double getPickProbability() {
            return 0.5;
        }

        @Override
        protected boolean canUse() {
            return isOnFire() || getLastDamageSource() != null && getLastDamageSource().isFire();
        }

        @Override
        public Potion getType() {
            return strengthenHealAbility() ? Potions.LONG_FIRE_RESISTANCE : Potions.FIRE_RESISTANCE;
        }
    }

    public class HealingPotionPicker extends PotionPicker {
        @Override
        protected double getPickProbability() {
            return MathHelper.lerp(getHealth() / getMaxHealth(), 0.08, 0.02);
        }

        @Override
        protected boolean canUse() {
            return getHealth() < getMaxHealth();
        }

        @Override
        public Potion getType() {
            return strengthenHealAbility() ? Potions.STRONG_HEALING : Potions.HEALING;
        }
    }

    public class SwiftnessPotionPicker extends PotionPicker {
        @Override
        protected double getPickProbability() {
            return 0.1;
        }

        @Override
        protected boolean canUse() {
            if (getTarget() == null) {
                return false;
            }
            return distanceToSqr(getTarget()) >= 12 * 12;
        }

        @Override
        public Potion getType() {
            return Potions.STRONG_SWIFTNESS;
        }
    }

    public class TurtleMasterPotionPicker extends PotionPicker {
        @Override
        protected double getPickProbability() {
            return 0.2;
        }

        @Override
        protected boolean canUse() {
            if (getTarget() == null) {
                return false;
            }
            double maxDist;
            if (isRangedAttacker(getTarget())) {
                maxDist = 11;
            } else {
                maxDist = 5;
            }
            return isLowHealth() && distanceToSqr(getTarget()) <= maxDist * maxDist;
        }

        @Override
        public Potion getType() {
            return Potions.TURTLE_MASTER;
        }

        private boolean isRangedAttacker(LivingEntity target) {
            return target.getItemInHand(Hand.MAIN_HAND).getItem() instanceof ShootableItem;
        }
    }

    public class RegenerationPotionPicker extends PotionPicker {
        @Override
        protected double getPickProbability() {
            return 0.025;
        }

        @Override
        protected boolean canUse() {
            return getHealth() < getMaxHealth() * 0.75 && (getTarget() == null || getTypeOf(getTarget()) == TargetType.FRIENDLY);
        }

        @Override
        public Potion getType() {
            return strengthenHealAbility() ? Potions.STRONG_REGENERATION : Potions.REGENERATION;
        }
    }

    public abstract class PotionPicker {
        protected abstract double getPickProbability();

        protected abstract boolean canUse();

        public final boolean check() {
            if (allowDuplicateEffects() || !getType().getEffects().stream().map(EffectInstance::getEffect).allMatch(VoidAlchemistEntity.this::hasEffect)) {
                if (random.nextDouble() < getPickProbability()) {
                    return canUse();
                }
            }
            return false;
        }

        protected boolean allowDuplicateEffects() {
            return false;
        }

        public abstract Potion getType();

        public List<EffectInstance> applyEffects(ItemStack stack) {
            return PotionUtils.getMobEffects(stack);
        }
    }

    public static class ScanData {
        private final List<LivingEntity> enemies;
        private final List<MobEntity> friendlies;

        public ScanData(List<LivingEntity> enemies, List<MobEntity> friendlies) {
            this.enemies = enemies;
            this.friendlies = friendlies;
        }

        public List<LivingEntity> getEnemies() {
            return enemies;
        }

        public List<MobEntity> getFriendlies() {
            return friendlies;
        }
    }

    public enum TargetType {
        FRIENDLY,
        UNDEAD,
        ENEMY
    }
}
