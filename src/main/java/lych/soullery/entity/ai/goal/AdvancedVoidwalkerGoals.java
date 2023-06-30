package lych.soullery.entity.ai.goal;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ai.ComputerOperation;
import lych.soullery.entity.ai.EtheArmorerAttackType;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.entity.monster.voidwalker.ComputerScientistEntity;
import lych.soullery.entity.monster.voidwalker.EtheArmorerEntity;
import lych.soullery.entity.monster.voidwalker.VoidwalkerTier;
import lych.soullery.extension.shield.SharedShield;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.util.*;
import lych.soullery.util.redirectable.PredicateRedirectable;
import lych.soullery.util.redirectable.Redirectable;
import lych.soullery.util.redirectable.Redirector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * For non-{@link AbstractVoidwalkerEntity#isPrimary() primary} voidwalkers
 */
public final class AdvancedVoidwalkerGoals {
    private AdvancedVoidwalkerGoals() {}

    public static class FindAttackTypeGoal extends Goal {
        private final EtheArmorerEntity armorer;
        private EtheArmorerAttackType attackType;

        public FindAttackTypeGoal(EtheArmorerEntity armorer) {
            this.armorer = armorer;
        }

        @Override
        public boolean canUse() {
            if (armorer.getAttackType() == null && EntityUtils.isAlive(armorer.getTarget())) {
                attackType = armorer.findAttackType(armorer.getTarget());
                if (attackType != null) {
                    return true;
                }
                armorer.setTarget(null);
                return false;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            armorer.setAttackType(attackType);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }

    public static abstract class EtheArmorerGoal extends Goal {
        protected final EtheArmorerEntity armorer;
        protected final Random random;
        protected final double speedModifier;
        protected LivingEntity target;
        private int maxAttackTime;
        private int attackIntervalTicks;
        private int attackTicks;
        private int attackTime;

        protected EtheArmorerGoal(EtheArmorerEntity armorer, double speedModifier) {
            this.armorer = armorer;
            this.random = armorer.getRandom();
            this.speedModifier = speedModifier;
            setFlags(makeFlags());
            Objects.requireNonNull(getAttackType());
        }

        protected abstract EnumSet<Flag> makeFlags();

        @Override
        public boolean canUse() {
            if (armorer.getAttackType() != getAttackType()) {
                return false;
            }
            LivingEntity target = armorer.getTarget();
//          Double-check
            if (EntityUtils.isAlive(target) && getAttackType().canUse(armorer, target)) {
                this.target = target;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            maxAttackTime = getMaxAttackTime();
            attackTicks = getAttackDuration();
            armorer.setAttackType(getAttackType());
            armorer.setLaserTarget(target);
        }

        @Override
        public void tick() {
            super.tick();
            armorer.getLookControl().setLookAt(target, 30, 30);
            if (armorer.distanceToSqr(target) > getAttackRadius() * getAttackRadius()) {
                armorer.getNavigation().moveTo(target, speedModifier);
            } else {
                armorer.getNavigation().stop();
            }
            if (attackIntervalTicks > 0) {
                attackIntervalTicks--;
                if (attackIntervalTicks == 0) {
                    armorer.setLaserTarget(target);
                    attackTicks = getAttackDuration();
                }
                return;
            }
            if (attackTicks > 0) {
                attackTicks--;
            } else {
                if (performAttack()) {
                    attackTime++;
                    if (attackTime >= maxAttackTime) {
                        attackTicks = -1;
                    } else {
                        armorer.setLaserTarget(null);
                        attackIntervalTicks = getAttackInterval();
                    }
                } else {
                    attackTicks = -1;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && attackTicks >= 0;
        }

        @Override
        public void stop() {
            super.stop();
            maxAttackTime = 0;
            attackTicks = 0;
            attackTime = 0;
            attackIntervalTicks = 0;
            target = null;
            armorer.setLaserTarget(null);
            armorer.setAttackType(null);
        }

        protected Pair<EquipmentSlotType, ItemStack> pickItem(Predicate<? super ItemStack> predicate) {
            return pickItem(predicate, 0.5);
        }

        protected Pair<EquipmentSlotType, ItemStack> pickItem(Predicate<? super ItemStack> predicate, double pickArmorProbability) {
            List<ItemStack> armors = CollectionUtils.list(target.getArmorSlots());
            List<Pair<EquipmentSlotType, ItemStack>> armorPairs = armors.stream().filter(predicate).map(stack -> Pair.of(EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.ARMOR, armors.indexOf(stack)), stack)).collect(Collectors.toList());
            List<ItemStack> items = CollectionUtils.list(target.getHandSlots());
            List<Pair<EquipmentSlotType, ItemStack>> itemPairs = items.stream().filter(predicate).map(stack -> Pair.of(EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.HAND, items.indexOf(stack)), stack)).collect(Collectors.toList());
            boolean pickArmor = !armorPairs.isEmpty() && random.nextDouble() < pickArmorProbability;
            return Utils.getOrDefault(pickArmor ? CollectionUtils.getNonnullRandom(armorPairs, random) : CollectionUtils.getRandom(itemPairs, random), Pair.of(EquipmentSlotType.MAINHAND, ItemStack.EMPTY));
        }

        protected abstract boolean performAttack();

        protected abstract int getAttackDuration();

        protected int getAttackInterval() {
            return 20;
        }

        protected double getAttackRadius() {
            return 12;
        }

        protected abstract int getMaxAttackTime();

        protected abstract EtheArmorerAttackType getAttackType();
    }

    public static class ReinforceFriendlyGoal extends EtheArmorerGoal {
        private static final double SKIP_ENCHANTMENT_PROBABILITY = 0.5;

        public ReinforceFriendlyGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            ItemStack stack = pickItem(this::applicable).getSecond();
            if (stack.isEmpty()) {
                return false;
            }
            for (int i = 0; i < getApplyCount(); i++) {
                stack = applyEnchantmentChanges(stack);
            }
            return stack.isEnchanted();
        }

        protected boolean applicable(ItemStack stack) {
            return stack.isEnchantable() || stack.isEnchanted();
        }

        protected ItemStack applyEnchantmentChanges(ItemStack stack) {
            boolean shouldEnchant = true;
            if (stack.isEnchanted()) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    if (entry.getKey().getMaxLevel() > entry.getValue() && random.nextDouble() >= SKIP_ENCHANTMENT_PROBABILITY) {
                        entry.setValue(entry.getValue() + 1);
                        shouldEnchant = false;
                    }
                }
                if (!shouldEnchant) {
                    EnchantmentHelper.setEnchantments(enchantments, stack);
                }
            }
            if (alwaysEnchant() || shouldEnchant) {
                stack = EnchantmentHelper.enchantItem(random, stack, getEnchantLevel(), false);
            }
//          Ensure that the Enchantments are distinct.
            Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(stack);
            stack.removeTagKey("Enchantments");
            EnchantmentHelper.setEnchantments(enchantmentMap, stack);
            return stack;
        }

        private int getApplyCount() {
            switch (armorer.getTier()) {
                case PARAGON:
                    return 3;
                case ELITE:
                    return 2;
                default:
                    return 1;
            }
        }

        private boolean alwaysEnchant() {
            return armorer.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY);
        }

        private int getEnchantLevel() {
            switch (armorer.getTier()) {
                case PARAGON:
                    return 30;
                case ELITE:
                    return 20;
                case EXTRAORDINARY:
                    return 12;
                default:
                    return 6;
            }
        }

        @Override
        protected int getAttackDuration() {
            return 40;
        }

        @Override
        protected int getMaxAttackTime() {
            return 2;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.REINFORCE;
        }
    }

    public static class DamageWeaponGoal extends EtheArmorerGoal {
        private static final double DAMAGE_INVENTORY_ITEM_PROBABILITY = 0.1;
        private static final int DAMAGE_COUNT = 4;

        public DamageWeaponGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            Pair<EquipmentSlotType, ItemStack> pair = getDamageablePair();
            ItemStack stack = pair.getSecond();
            if (stack.isEmpty()) {
                return false;
            }
            int oldDamage = stack.getDamageValue();
            for (int i = 0; i < DAMAGE_COUNT; i++) {
                boolean destroyed = damage(stack, pair.getFirst(), getDamageAmount(stack));
                if (destroyed) {
                    if (pair.getFirst() != null && !(target instanceof PlayerEntity)) {
                        target.setItemSlot(pair.getFirst(), ItemStack.EMPTY);
                    }
                    break;
                }
            }
            int newDamage = stack.getDamageValue();
            return newDamage > oldDamage;
        }

        private Pair<EquipmentSlotType, ItemStack> getDamageablePair() {
            Pair<EquipmentSlotType, ItemStack> pair = pickItem(ItemStack::isDamageableItem);
            ItemStack stack = pair.getSecond();
            List<ItemStack> inventoryItems = InventoryUtils.getInventoryItemsIfIsPlayer(target);
            inventoryItems.removeIf(stackIn -> !stackIn.isDamageableItem());
            boolean fromInventory = false;
            if (stack.isEmpty() || !inventoryItems.isEmpty() && random.nextDouble() < DAMAGE_INVENTORY_ITEM_PROBABILITY) {
                stack = Utils.getOrDefault(CollectionUtils.getRandom(inventoryItems, random), ItemStack.EMPTY);
                fromInventory = true;
            }
            return Pair.of(fromInventory ? null : pair.getFirst(), stack);
        }

        private int getDamageAmount(ItemStack stack) {
            return Utils.randomlyCast(getBaseDamageAmount() * getDamageAmountMultiplier(stack.getMaxDamage()), random);
        }

        private int getBaseDamageAmount() {
            switch (armorer.getTier()) {
                case PARAGON:
                    return 8;
                case ELITE:
                    return 4;
                case EXTRAORDINARY:
                    return 2;
                default:
                    return 1;
            }
        }

        private double getDamageAmountMultiplier(int maxDamage) {
            double min = 1;
            if (maxDamage <= 0) {
                return min;
            }
            return Math.max(min, Math.log(maxDamage)) * 0.5;
        }

        private boolean damage(ItemStack stack, @Nullable EquipmentSlotType type, int amount) {
            if (target instanceof PlayerEntity) {
                stack.hurtAndBreak(amount, target, type == null ? DefaultValues.dummyConsumer() : entity -> entity.broadcastBreakEvent(type));
                return stack.isEmpty();
            }
            return random.nextDouble() < Math.log10(1 + getBaseDamageAmount());
        }

        @Override
        protected int getAttackDuration() {
            return 40;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1 + random.nextInt(2);
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.DAMAGE;
        }
    }

    public static class ReconstructWeaponGoal extends EtheArmorerGoal {
        public ReconstructWeaponGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            if (target instanceof AbstractVoidwalkerEntity) {
                target.setItemInHand(Hand.MAIN_HAND, ((AbstractVoidwalkerEntity) target).createWeapon());
            } else if (ConfigHelper.shouldFailhard()) {
                throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + String.format("The Ethe-Armorer's reconstruct-target(%s) is not a voidwalker. Why?", target.getType().getRegistryName()));
            }
            return !target.getMainHandItem().isEmpty();
        }

        @Override
        protected int getAttackDuration() {
            return 40;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.RECONSTRUCT;
        }
    }

    public static class RandomlyRenameGoal extends EtheArmorerGoal {
        public RandomlyRenameGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            ItemStack stack = pickItem(EtheArmorerEntity::renameable).getSecond();
            stack.setHoverName(new StringTextComponent(String.valueOf(generateRandomNumber())));
            return !stack.isEmpty();
        }

        private int generateRandomNumber() {
            if (random.nextDouble() < 0.01) {
                return 23333;
            }
            int min = 10000;
            int max = 99999;
            return MathHelper.nextInt(random, min, max);
        }

        @Override
        protected int getAttackDuration() {
            return 20;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.RENAME;
        }
    }

    public static class DisenchantAndCurseGoal extends ReinforceFriendlyGoal {
        private static final double SKIP_PROBABILITY = 0.5;
        private static final double APPLY_TO_REINFORCEMENTS_PROBABILITY = 0.3;
        private static final double ADD_CURSE_PROBABILITY = 0.25;

        public DisenchantAndCurseGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected boolean applicable(ItemStack stack) {
            return stack.isEnchanted() || ReinforcementHelper.hasReinforcements(stack);
        }

        @Override
        protected ItemStack applyEnchantmentChanges(ItemStack stack) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            boolean noValidEnchantments = enchantments.isEmpty() || enchantments.keySet().stream().allMatch(Enchantment::isCurse);
            if (noValidEnchantments || random.nextDouble() < APPLY_TO_REINFORCEMENTS_PROBABILITY) {
                Map<Reinforcement, Integer> reinforcements = ReinforcementHelper.getReinforcements(stack);
                for (Iterator<Map.Entry<Reinforcement, Integer>> itr = reinforcements.entrySet().iterator(); itr.hasNext(); ) {
                    Map.Entry<Reinforcement, Integer> entry = itr.next();
                    if (!itr.hasNext() || random.nextDouble() >= SKIP_PROBABILITY) {
                        if (entry.getValue() > 1) {
                            entry.setValue(entry.getValue() - 1);
                        } else {
                            itr.remove();
                        }
                    }
                }
                ReinforcementHelper.putReinforcements(stack, reinforcements, true);
            } else {
                for (Iterator<Map.Entry<Enchantment, Integer>> itr = enchantments.entrySet().iterator(); itr.hasNext(); ) {
                    Map.Entry<Enchantment, Integer> entry = itr.next();
                    if (!entry.getKey().isCurse() && (!itr.hasNext() || random.nextDouble() >= SKIP_PROBABILITY)) {
                        if (entry.getValue() > 1) {
                            entry.setValue(entry.getValue() - 1);
                        } else {
                            itr.remove();
                        }
                    }
                }
                EnchantmentHelper.setEnchantments(enchantments, stack);
            }
            if (random.nextDouble() < ADD_CURSE_PROBABILITY) {
                Enchantment curse = CollectionUtils.getRandom(Streams.stream(ForgeRegistries.ENCHANTMENTS).filter(Enchantment::isCurse).filter(enchantment -> enchantment.canEnchant(stack)).filter(enchantment -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) <= 0).collect(Collectors.toList()), random);
                if (curse == null) {
                    return stack;
                }
                stack.enchant(curse, EnchantmentHelper.getItemEnchantmentLevel(curse, stack) + 1);
            }
            return stack;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.CURSE;
        }
    }

    public static class WoodifyMainHandItemGoal extends EtheArmorerGoal {
        public static final List<PredicateRedirectable<ItemStack, ItemStack>> REDIRECTABLES = new ArrayList<>();
        private static final int DURABILITY_REMAINING = 10;

        static {
            PredicateRedirectable.Creator<ItemStack> planks = PredicateRedirectable.withFactory(() -> new ItemStack(Items.OAK_PLANKS));
            PredicateRedirectable.Creator<ItemStack> stick = PredicateRedirectable.withFactory(() -> new ItemStack(Items.STICK));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_SWORD), stack -> stack.getItem() instanceof SwordItem && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.BOW), stack -> stack.getItem() instanceof BowItem && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_PICKAXE), stack -> stack.getToolTypes().contains(ToolType.PICKAXE) && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_AXE), stack -> stack.getToolTypes().contains(ToolType.AXE) && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_SHOVEL), stack -> stack.getToolTypes().contains(ToolType.SHOVEL) && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_HOE), stack -> stack.getToolTypes().contains(ToolType.HOE) && nonWood(stack)));
            REDIRECTABLES.add(stick.using(ItemStack::isDamageableItem));
            REDIRECTABLES.add(planks.using(DefaultValues::always));
        }

        public WoodifyMainHandItemGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            ItemStack mainHandItem = target.getMainHandItem();
            if (EtheArmorerEntity.isWood(mainHandItem)) {
                return false;
            }
            ItemStack wood = ItemStack.EMPTY;
            for (Redirectable<ItemStack, ItemStack> redirectable : REDIRECTABLES) {
                if (redirectable.test(mainHandItem)) {
                    wood = redirectable.redirect(mainHandItem);
                    copyTagAndResetDamage(mainHandItem, wood);
                    break;
                }
            }
            if (wood.isEmpty() && ConfigHelper.shouldFailhard()) {
                throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + "Wood not found, that should be impossible");
            }
            target.setItemInHand(Hand.MAIN_HAND, wood);
            return !target.getMainHandItem().isEmpty();
        }

        private static void copyTagAndResetDamage(ItemStack mainHandItem, ItemStack wood) {
            wood.setCount(mainHandItem.getCount());
            if (wood.isDamageableItem()) {
                wood.setTag(mainHandItem.getTag());
                int durability = Math.min(mainHandItem.getMaxDamage() - mainHandItem.getDamageValue(), DURABILITY_REMAINING);
                wood.setDamageValue(wood.getMaxDamage() - durability);
            }
        }

        private static boolean nonWood(ItemStack stack) {
            if (stack.getItem() == Items.BOW) {
                return false;
            }
            return !(stack.getItem() instanceof TieredItem) || ((TieredItem) stack.getItem()).getTier() != ItemTier.WOOD;
        }

        @Override
        protected int getAttackDuration() {
            return armorer.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? 40 : 80;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.WOODIFY;
        }
    }

    public static abstract class ComputerScientistGoal extends Goal {
        protected static final double SNEAK_PROBABILITY = 0.2;
        protected final ComputerScientistEntity cs;
        protected final double speedModifier;
        protected final DoubleSupplier attackRadius;
        protected final double escapeRadius;
        protected final ServerWorld level;
        protected final Random random;
        protected LivingEntity target;
        protected boolean continuable = true;

        protected ComputerScientistGoal(ComputerScientistEntity cs) {
            this(cs, 1, () -> 18, 6);
        }

        protected ComputerScientistGoal(ComputerScientistEntity cs, double speedModifier, DoubleSupplier attackRadius, double escapeRadius) {
            this.escapeRadius = escapeRadius;
            EntityUtils.checkGoalInstantiationServerside(cs);
            this.cs = cs;
            this.speedModifier = speedModifier;
            this.attackRadius = attackRadius;
            this.level = (ServerWorld) cs.level;
            this.random = cs.getRandom();
            setFlags(makeFlags());
        }

        protected abstract EnumSet<Flag> makeFlags();

        @Override
        public boolean canUse() {
            if (cs.getAttackIntervalTicks() > 0) {
                return false;
            }
            if (cs.getOperation() != null && cs.getOperation() != operation()) {
                return false;
            }
            LivingEntity target = cs.getTarget();
            if (target != null) {
                this.target = target;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            ComputerOperation operation = operation();
            if (operation != null) {
                cs.setOperation(operation);
                cs.setLaserTarget(target);
            } else {
                resetComputerEngineer();
            }
            if (target != null) {
                ITextComponent text = cs.formatSRGNameOperation(getName(), target, isAggressive());
                if (text != null) {
                    target.sendMessage(text, Util.NIL_UUID);
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
            try {
                LivingEntity entity = next();
                if (entity != null) {
                    target = entity;
//                  Reset operation to prevent null
                    cs.setOperation(operation());
                    cs.setLaserTarget(entity);
                    updateTarget(entity);
                }
            } catch (UnableToContinueException e) {
                continuable = false;
            }
            if (target != null) {
                cs.getLookControl().setLookAt(target, 30, 30);
                followTarget(target, true);
            }
        }

        protected void followTarget(LivingEntity target, boolean shouldEscape) {
            if (cs.distanceToSqr(target) > attackRadius.getAsDouble() * attackRadius.getAsDouble()) {
                moveTo(null, target);
            } else if (shouldEscape && cs.distanceToSqr(target) < escapeRadius * escapeRadius) {
                Vector3d pos = null;
                for (int i = 0; i < 3; i++) {
                    pos = RandomPositionGenerator.getPosAvoid(cs, (int) attackRadius.getAsDouble(), (int) (attackRadius.getAsDouble() / 3), target.position());
                    if (pos != null) {
                        break;
                    }
                }
                if (pos != null) {
                    moveTo(pos, target);
                }
            } else {
                cs.getNavigation().stop();
            }
        }

        protected void moveTo(@Nullable Vector3d pos, @Nullable LivingEntity target) {
            if (pos == null && target == null) {
                throw new NullPointerException();
            }
            if (pos == null) {
                pos = target.position();
            }
            if (random.nextDouble() < SNEAK_PROBABILITY && cs.canBeEtherealToAttack() || target != null && !EntityUtils.canReach(cs, target)) {
                cs.setSneakTarget(pos);
                cs.setEtherealCooldown(AbstractVoidwalkerEntity.LONG_ETHEREAL_COOLDOWN);
            } else {
                if (target != null) {
                    cs.getNavigation().moveTo(target, speedModifier);
                } else {
                    cs.getNavigation().moveTo(pos.x, pos.y, pos.z, speedModifier);
                }
            }
        }

        protected void updateTarget(LivingEntity entity) {
            cs.setTarget(entity);
        }

        @Override
        public void stop() {
            super.stop();
            resetComputerEngineer();
            continuable = true;
            cs.setAttackIntervalTicks(getAttackInterval());
        }

        protected void resetComputerEngineer() {
            cs.setLaserTarget(null);
            cs.setOperation(null);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && continuable;
        }

        protected boolean isAggressive() {
            return true;
        }

        @Nullable
        protected abstract LivingEntity next() throws UnableToContinueException;

        @Nullable
        protected abstract ComputerOperation operation();

        protected abstract String getName();

        protected int getAttackInterval() {
            return 20;
        }

        protected static class UnableToContinueException extends Exception {}
    }

    public static class ShieldSelfGoal extends ComputerScientistGoal {
        private static final String SRG_NAME_SWING = "func_184609_a";
        private int waitingTicks;

        public ShieldSelfGoal(ComputerScientistEntity cs) {
            super(cs);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.LOOK);
        }

        @Override
        public boolean canUse() {
            if (cs.isShieldValid() || cs.getShieldCooldown() > 0) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public void start() {
            super.start();
            waitingTicks = 20;
            cs.setShieldCooldown(20 * 30);
            cs.setSharedShield(new SharedShield(0, cs.getMaxHealth()));
            EntityUtils.addParticlesAroundSelfServerside(cs, level, ParticleTypes.ENCHANT, 4 + random.nextInt(5));
        }

        @Nullable
        @Override
        protected LivingEntity next() throws UnableToContinueException {
            if (waitingTicks > 0) {
                waitingTicks--;
                return null;
            }
            throw new UnableToContinueException();
        }

        @Override
        protected ComputerOperation operation() {
            return null;
        }

        @Override
        protected int getAttackInterval() {
            return 10;
        }

        @Override
        protected boolean isAggressive() {
            return false;
        }

        /**
         * {@link LivingEntity#swing(Hand) swing}
         */
        @Override
        protected String getName() {
            return SRG_NAME_SWING;
        }
    }

    public static class ShieldOthersGoal extends ComputerScientistGoal {
        private static final String SRG_NAME_SET_LOOK_AT = "func_75651_a";
        private static final int MAX_COUNT = 5;
        private final Queue<AbstractVoidwalkerEntity> shieldableEntities = new ArrayDeque<>();
        private int tickCounter;
        private AbstractVoidwalkerEntity curr;
        private boolean preparingToStop;

        public ShieldOthersGoal(ComputerScientistEntity cs) {
            super(cs);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected void updateTarget(LivingEntity entity) {}

        @Override
        public boolean canUse() {
            if (cs.getAttackIntervalTicks() > 0) {
                return false;
            }
            if (!canProvideShield() || cs.getUsers().size() >= MAX_COUNT) {
                return false;
            }
            List<AbstractVoidwalkerEntity> nearby = cs.getNearbyVoidwalkers(AbstractVoidwalkerEntity.class, getFollowRange(), this::stillValid);
            if (!nearby.isEmpty()) {
                nearby.sort(Comparator.comparingDouble(v -> v.distanceToSqr(cs)));
                CollectionUtils.refill(shieldableEntities, nearby);
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            try {
                next();
                ComputerOperation operation = operation();
                if (operation != null) {
                    cs.setOperation(operation);
                    cs.setLaserTarget(curr);
                } else if (ConfigHelper.shouldFailhard()) {
                    throw new NullPointerException("Operation is null");
                }
            } catch (UnableToContinueException e) {
                continuable = false;
            }
            if (target != null) {
                ITextComponent text = cs.formatSRGNameOperation(getName(), target, false);
                if (text != null) {
                    target.sendMessage(text, Util.NIL_UUID);
                }
            }
        }

        @Override
        public void tick() {
            if (tickCounter > 0) {
                tickCounter--;
            }
            if (curr != null) {
                cs.getLookControl().setLookAt(curr, 30, 30);
            }
            try {
                next();
                cs.setOperation(operation());
                cs.setLaserTarget(curr);
            } catch (UnableToContinueException e) {
                continuable = false;
            }
            if (curr != null) {
                followTarget(curr, false);
            }
        }

        private double getFollowRange() {
            return cs.getAttributeValue(Attributes.FOLLOW_RANGE);
        }

        @Nullable
        @Override
        protected AbstractVoidwalkerEntity next() throws UnableToContinueException {
            if (tickCounter > 0) {
                return null;
            }
            if (preparingToStop) {
                throw new UnableToContinueException();
            }
            do {
                curr = shieldableEntities.poll();
                if (curr == null) {
                    throw new UnableToContinueException();
                }
            } while (!stillValid(curr));
            curr.setShieldProvider(cs);
            cs.addShieldUser(curr);
            if (cs.getUsers().size() >= MAX_COUNT) {
                preparingToStop = true;
            }
            tickCounter = 20;
            return curr;
        }

        @Override
        public boolean canContinueToUse() {
            return canProvideShield() && continuable;
        }

        private boolean canProvideShield() {
            return cs.getSharedShield() != null && cs.isShieldValid();
        }

        @Override
        public void stop() {
            resetComputerEngineer();
            continuable = true;
            preparingToStop = false;
            curr = null;
            shieldableEntities.clear();
            tickCounter = 0;
            cs.setAttackIntervalTicks(getAttackInterval());
        }

        private boolean stillValid(AbstractVoidwalkerEntity voidwalker) {
            if (voidwalker instanceof ComputerScientistEntity) {
                return false;
            }
            return EntityUtils.isAlive(voidwalker) && (voidwalker.getShieldProvider() == null || voidwalker.getShieldProvider() == voidwalker) && cs.distanceToSqr(voidwalker) <= getFollowRange() * getFollowRange();
        }

        @Nullable
        @Override
        protected ComputerOperation operation() {
            return ComputerOperation.SHIELD;
        }

        @Override
        protected boolean isAggressive() {
            return false;
        }

        /**
         * {@link net.minecraft.entity.ai.controller.LookController#setLookAt(Entity, float, float) setLookAt}
         */
        @Override
        protected String getName() {
            return SRG_NAME_SET_LOOK_AT;
        }
    }

    public static class ComputerScientistRetreatGoal extends VoidwalkerGoals.RetreatGoal {
        private final ComputerScientistEntity cs;

        public ComputerScientistRetreatGoal(ComputerScientistEntity cs, int retreatFreq) {
            super(cs, retreatFreq);
            this.cs = cs;
        }

        @Override
        public void start() {
            super.start();
            if (cs.getTarget() != null) {
                cs.sendMessage(cs.formatRetreatOperation(), Util.NIL_UUID);
            }
        }
    }

    public static abstract class RegularOperationGoal extends ComputerScientistGoal {
        protected final Set<LivingEntity> visited = new HashSet<>();
        private int ticksRemaining;

        protected RegularOperationGoal(ComputerScientistEntity cs) {
            super(cs);
        }

        protected RegularOperationGoal(ComputerScientistEntity cs, double speedModifier, DoubleSupplier attackRadius, double escapeRadius) {
            super(cs, speedModifier, attackRadius, escapeRadius);
        }

        @Override
        public boolean canUse() {
            if (cs.hasCooldown(operation())) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public void start() {
            super.start();
            ticksRemaining = getOperationTime(target);
        }

        @Nullable
        @Override
        protected LivingEntity next() throws UnableToContinueException {
            if (!EntityUtils.isAlive(target)) {
                throw new UnableToContinueException();
            }
            if (ticksRemaining > 0) {
                ticksRemaining--;
                return null;
            }
            LivingEntity next = doOperate(target);
            if (next == null) {
                throw new UnableToContinueException();
            }
            ticksRemaining = getOperationTime(next);
            return next;
        }

        @Override
        public void stop() {
            super.stop();
            cs.addCooldown(operation(), getOperationCooldown());
            ticksRemaining = 0;
            visited.clear();
        }

        @Nullable
        protected LivingEntity findAnotherTarget(LivingEntity target, double range) {
            return findAnotherTarget(target, range, DefaultValues.alwaysTrue());
        }

        @Nullable
        protected LivingEntity findAnotherTarget(LivingEntity target, double range, Predicate<LivingEntity> predicate) {
            return level.getNearestLoadedEntity(LivingEntity.class,
                    cs.customizeTargetConditions(new EntityPredicate())
                            .range(range)
                            .selector(predicate.and(l -> ESVMob.nonESVMob(l) && !visited.contains(l) && EntityPredicates.ATTACK_ALLOWED.test(l))),
                    target,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    target.getBoundingBox().inflate(range));
        }

        protected abstract int getOperationTime(LivingEntity target);

        protected abstract int getOperationCooldown();

        @Nullable
        protected abstract LivingEntity doOperate(LivingEntity target);

        @NotNull
        @Override
        protected abstract ComputerOperation operation();
    }

    public static class ApplyMutationGoal extends RegularOperationGoal {
        private static final String SRG_NAME_ADD_EFFECT = "func_195064_c";
        private static final List<Redirectable<Effect, Effect>> REDIRECTABLES = new ArrayList<>();
        private static final double FIND_ANOTHER_TARGET_RANGE = 4;

        static {
            REDIRECTABLES.add(new Redirector<>(Effects.POISON, Effects.REGENERATION));
            REDIRECTABLES.add(new Redirector<>(Effects.WEAKNESS, Effects.DAMAGE_BOOST));
            REDIRECTABLES.add(new Redirector<>(Effects.MOVEMENT_SLOWDOWN, Effects.MOVEMENT_SPEED));
            REDIRECTABLES.add(new Redirector<>(Effects.DIG_SLOWDOWN, Effects.DIG_SPEED));
            REDIRECTABLES.add(new Redirector<>(Effects.BLINDNESS, Effects.NIGHT_VISION, Effects.INVISIBILITY));
            REDIRECTABLES.add(new Redirector<>(Effects.LEVITATION, Effects.SLOW_FALLING));
            REDIRECTABLES.add(new Redirector<>(Effects.BAD_OMEN, Effects.HERO_OF_THE_VILLAGE));
        }

        public ApplyMutationGoal(ComputerScientistEntity cs) {
            super(cs);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && target.getActiveEffects().stream().anyMatch(ModEffectUtils::isBeneficial);
        }

        @NotNull
        @Override
        protected ComputerOperation operation() {
            return ComputerOperation.MUTATE;
        }

        @Override
        protected String getName() {
            return SRG_NAME_ADD_EFFECT;
        }

        @Override
        protected int getOperationTime(LivingEntity target) {
            int strength = target.getActiveEffects().stream().filter(ModEffectUtils::isBeneficial).mapToInt(ModEffectUtils::calculateEffectStrength).sum();
            return MathHelper.clamp(16 + strength / 100, 20, 80);
        }

        @Override
        protected int getOperationCooldown() {
            return 300;
        }

        @Nullable
        @Override
        protected LivingEntity doOperate(LivingEntity target) {
            Map<Effect, EffectInstance> effectsMap = new HashMap<>(target.getActiveEffectsMap());
            List<EffectInstance> newEffects = new ArrayList<>();
            Outer:
            for (Map.Entry<Effect, EffectInstance> entry : effectsMap.entrySet()) {
                if (!entry.getKey().isBeneficial()) {
                    continue;
                }
                for (Redirectable<Effect, Effect> redirectable : REDIRECTABLES) {
                    if (redirectable.test(entry.getKey())) {
                        target.removeEffect(entry.getKey());
                        newEffects.add(ModEffectUtils.copyAttributes(redirectable.redirect(entry.getKey()), entry.getValue()));
                        continue Outer;
                    }
                }
                if (entry.getKey().isBeneficial()) {
                    target.removeEffect(entry.getKey());
                }
            }
            newEffects.forEach(target::addEffect);
            visited.add(target);
            return findAnotherTarget(target, FIND_ANOTHER_TARGET_RANGE);
        }
    }

    public static class ShuffleInventoryGoal extends RegularOperationGoal {
        private static final String SRG_NAME_INVENTORY = "field_71071_by";
        private static final double FIND_ANOTHER_TARGET_RANGE = 6;

        public ShuffleInventoryGoal(ComputerScientistEntity cs) {
            super(cs);
        }

        @Override
        public boolean canUse() {
            if (super.canUse()) {
                return target instanceof PlayerEntity;
            }
            return false;
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        /**
         * {@link PlayerEntity#inventory inventory}
         */
        @Override
        protected String getName() {
            return SRG_NAME_INVENTORY;
        }

        @Override
        protected int getOperationTime(LivingEntity target) {
            return 40;
        }

        @Override
        protected int getOperationCooldown() {
            return 600;
        }

        @Nullable
        @Override
        protected LivingEntity doOperate(LivingEntity target) {
            PlayerEntity playerTarget = (PlayerEntity) target;
            PlayerInventory inventory = playerTarget.inventory;
            List<ItemStack> items = inventory.items;
            if (mixHotbar()) {
                Collections.shuffle(items, random);
            } else {
                int sls = PlayerInventory.getSelectionSize();
                for (int i = items.size(); i > sls; i--) {
                    Collections.swap(items, i - 1, sls + random.nextInt(i - sls));
                }
                for (int i = sls; i > 1; i--) {
                    Collections.swap(items, i - 1, random.nextInt(i));
                }
            }
            return findAnotherTarget(playerTarget, FIND_ANOTHER_TARGET_RANGE, entity -> entity instanceof PlayerEntity);
        }

        @NotNull
        @Override
        protected ComputerOperation operation() {
            return ComputerOperation.SHUFFLE;
        }

        private boolean mixHotbar() {
            return cs.getTier().strongerThan(VoidwalkerTier.ORDINARY);
        }
    }

    public static class RegularlyAttackGoal extends RegularOperationGoal {
        private static final String SRG_NAME_HURT = "func_70097_a";
        private static final double FIND_ANOTHER_TARGET_RANGE = 16;

        public RegularlyAttackGoal(ComputerScientistEntity cs) {
            super(cs, 1, () -> cs.getAttributeValue(Attributes.FOLLOW_RANGE) * 0.75, 6);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected String getName() {
            return SRG_NAME_HURT;
        }

        @Override
        protected int getOperationTime(LivingEntity target) {
            return cs.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? 15 : 20;
        }

        @Override
        protected int getOperationCooldown() {
            return 20;
        }

        @Nullable
        @Override
        protected LivingEntity doOperate(LivingEntity target) {
            target.hurt(DamageSource.mobAttack(cs).bypassArmor().bypassMagic(), (float) cs.getAttributeValue(Attributes.ATTACK_DAMAGE));
            return findAnotherTarget(target, FIND_ANOTHER_TARGET_RANGE);
        }

        @NotNull
        @Override
        protected ComputerOperation operation() {
            return ComputerOperation.CYBERATTACK;
        }
    }
}
