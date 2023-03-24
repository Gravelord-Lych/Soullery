package lych.soullery.entity.monster.voidwalker;

import com.google.common.collect.Streams;
import lych.soullery.entity.ai.EtheArmorerAttackType;
import lych.soullery.entity.ai.goal.AdvancedVoidwalkerGoals.*;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.FindTargetExpiringGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.RetreatGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.VoidwalkerRandomWalkingGoal;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.util.InventoryUtils;
import lych.soullery.util.WeightedRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EtheArmorerEntity extends AbstractVoidLasererEntity<EtheArmorerEntity> {
    private FindTargetExpiringGoal<AbstractVoidwalkerEntity> reinforceVoidwalkersGoal;
    private boolean canAttack;

    public EtheArmorerEntity(EntityType<? extends EtheArmorerEntity> type, World world) {
        super(type, world);
    }

    @Override
    @Nullable
    public ILaserProvider<EtheArmorerEntity> provideLaser() {
        return getAttackType();
    }

    @Nullable
    public EtheArmorerAttackType getAttackType() {
        return EtheArmorerAttackType.byId(entityData.get(DATA_LASER_ID));
    }

    public void setAttackType(@Nullable EtheArmorerAttackType attackType) {
        entityData.set(DATA_LASER_ID, attackType == null ? -1 : attackType.getId());
    }

    public boolean canReinforce(LivingEntity entity) {
        if (ESVMob.nonESVMob(entity)) {
            return false;
        }
        if (entity instanceof AbstractVoidwalkerEntity && ((AbstractVoidwalkerEntity) entity).canCreateWeapon()) {
            return !entity.getMainHandItem().isEmpty() && !hasTooManyEnchantments(entity.getMainHandItem());
        }
        return false;
    }

    private boolean hasTooManyEnchantments(ItemStack stack) {
        if (!stack.isEnchanted()) {
            return false;
        }
        int maxLevel;
        switch (getTier()) {
            case PARAGON:
                maxLevel = 9;
                break;
            case ELITE:
                maxLevel = 6;
                break;
            case EXTRAORDINARY:
                maxLevel = 4;
                break;
            default:
                maxLevel = 3;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        return calculateEnchantmentsStrength(enchantments) >= maxLevel;
    }

    public boolean canDamage(LivingEntity entity) {
        if (ESVMob.isESVMob(entity)) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            return InventoryUtils.getList(((PlayerEntity) entity).inventory).stream().anyMatch(ItemStack::isDamageableItem);
        }
        return Streams.stream(entity.getAllSlots()).anyMatch(ItemStack::isDamageableItem);
    }

    public boolean canReconstruct(LivingEntity entity) {
        if (ESVMob.nonESVMob(entity)) {
            return false;
        }
        if (entity instanceof AbstractVoidwalkerEntity && ((AbstractVoidwalkerEntity) entity).canCreateWeapon()) {
            return entity.getMainHandItem().isEmpty();
        }
        return false;
    }

    public boolean canRename(LivingEntity entity) {
        if (ESVMob.isESVMob(entity)) {
            return false;
        }
        return Streams.stream(entity.getAllSlots()).anyMatch(EtheArmorerEntity::renameable);
    }

    public static boolean renameable(ItemStack stack) {
        return !stack.isEmpty() && !stack.isStackable();
    }

    public boolean canCurse(LivingEntity entity) {
        if (ESVMob.isESVMob(entity)) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            return InventoryUtils.getList(((PlayerEntity) entity).inventory).stream().anyMatch(this::canCurse);
        }
        return Streams.stream(entity.getAllSlots()).anyMatch(this::canCurse);
    }

    private boolean canCurse(ItemStack stack) {
        return hasEnoughEnchantments(stack) || hasEnoughReinforcements(stack);
    }

    private boolean hasEnoughEnchantments(ItemStack stack) {
        if (!stack.isEnchanted()) {
            return false;
        }
        if (getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY)) {
            return true;
        }
        int keepEnchantmentThreshold;
        if (getTier() == VoidwalkerTier.EXTRAORDINARY) {
            keepEnchantmentThreshold = 1;
        } else {
            keepEnchantmentThreshold = 2;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.keySet().stream().allMatch(Enchantment::isCurse)) {
            return false;
        }
        return calculateEnchantmentsStrength(enchantments) > keepEnchantmentThreshold;
    }

    private boolean hasEnoughReinforcements(ItemStack stack) {
        if (!ReinforcementHelper.hasReinforcements(stack)) {
            return false;
        }
        if (getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY)) {
            return true;
        }
        int keepReinforcementThreshold;
        if (getTier() == VoidwalkerTier.EXTRAORDINARY) {
            keepReinforcementThreshold = 1;
        } else {
            keepReinforcementThreshold = 2;
        }
        Map<Reinforcement, Integer> enchantments = ReinforcementHelper.getReinforcements(stack);
        return calculateReinforcementsStrength(enchantments) > keepReinforcementThreshold;
    }

    private static int calculateEnchantmentsStrength(Map<Enchantment, Integer> enchantments) {
        return enchantments.entrySet().stream().mapToInt(EtheArmorerEntity::calculateSingleEnchantmentValue).sum();
    }

    private static int calculateReinforcementsStrength(Map<Reinforcement, Integer> enchantments) {
        return enchantments.entrySet().stream().mapToInt(EtheArmorerEntity::calculateSingleReinforcementValue).sum();
    }

    private static int calculateSingleEnchantmentValue(Map.Entry<Enchantment, Integer> e) {
        if (e.getKey().isCurse()) {
            return 0;
        }
        if (e.getKey().getMaxLevel() == 1 || e.getKey().isTreasureOnly()) {
            return e.getValue() * 2;
        }
        return e.getValue();
    }

    private static int calculateSingleReinforcementValue(Map.Entry<Reinforcement, Integer> e) {
        if (e.getKey().getMaxLevel() == 1 || e.getKey().isSpecial()) {
            return e.getValue() * 2;
        }
        return e.getValue();
    }

    public boolean canWoodify(LivingEntity entity) {
        if (ESVMob.isESVMob(entity)) {
            return false;
        }
        if (getTier().weakerThan(VoidwalkerTier.EXTRAORDINARY)) {
            return false;
        }
        return !entity.getMainHandItem().isEmpty() && !isWood(entity.getMainHandItem());
    }

    public static boolean isWood(ItemStack mainHandItem) {
        return mainHandItem.getItem() == Items.STICK || mainHandItem.getItem().is(ItemTags.PLANKS);
    }

    @Override
    public EntityPredicate customizeTargetConditions(EntityPredicate targetConditions) {
        return targetConditions.allowUnseeable().allowSameTeam();
    }

    @Override
    protected double getTargetYOffset() {
        return 0.38;
    }

    @Override
    public boolean canAttack() {
        return canAttack;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new FindAttackTypeGoal(this));
//      Priority is not important now because the Ethe-Armorer uses a new "goal selector"
        goalSelector.addGoal(3, new ReinforceFriendlyGoal(this, 1));
        goalSelector.addGoal(3, new DamageWeaponGoal(this, 1));
        goalSelector.addGoal(3, new ReconstructWeaponGoal(this, 1));
        goalSelector.addGoal(3, new RandomlyRenameGoal(this, 1));
        goalSelector.addGoal(3, new WoodifyMainHandItemGoal(this, 1));
        goalSelector.addGoal(3, new DisenchantAndCurseGoal(this, 1));
        goalSelector.addGoal(4, new RetreatGoal(this, 200));
        goalSelector.addGoal(6, new VoidwalkerRandomWalkingGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtGoal(this, AbstractVoidwalkerEntity.class, 12));
        goalSelector.addGoal(9, new LookRandomlyGoal(this));
        reinforceVoidwalkersGoal = new FindTargetExpiringGoal<>(this, AbstractVoidwalkerEntity.class, 100, true, false, entity -> canReconstruct(entity) || canReinforce(entity));
        targetSelector.addGoal(1, reinforceVoidwalkersGoal);
    }

    @Override
    public boolean isLowHealth(LivingEntity entity) {
        if (entity == this) {
            return entity.getHealth() < entity.getMaxHealth() * 0.5f;
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level.isClientSide() && isAlive()) {
            reinforceVoidwalkersGoal.decrementCooldown();
            setCanAttack(reinforceVoidwalkersGoal.getCooldown() <= FindTargetExpiringGoal.CAN_ATTACK_COOLDOWN);
            if (getAttackType() != null && !getAttackType().canUse(this, getTarget())) {
                setTarget(null);
            }
        }
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    @Override
    public boolean canAttackAllMobs() {
        return super.canAttackAllMobs() && canAttack();
    }

    @Nullable
    public EtheArmorerAttackType findAttackType(LivingEntity target) {
        List<EtheArmorerAttackType> attackTypes = EtheArmorerAttackType.getAttackTypes().stream().filter(type -> type.canUse(this, target)).collect(Collectors.toList());
        if (attackTypes.isEmpty()) {
            return null;
        }
        if (attackTypes.size() == 1) {
            return attackTypes.get(0);
        }
        return WeightedRandom.getRandomItem(random, attackTypes);
    }
}
