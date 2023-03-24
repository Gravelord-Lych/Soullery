package lych.soullery.entity.monster;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soullery.entity.monster.boss.SkeletonKingEntity;
import lych.soullery.util.EntityUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class SkeletonFollowerEntity extends SkeletonEntity implements IHasOwner<SkeletonKingEntity> {
    @Nullable
    private UUID ownerUUID;
    private static final ImmutableMap<EquipmentSlotType, Function<Random, ItemStack>> WEAPON_MAP = ImmutableMap.of(EquipmentSlotType.MAINHAND, random2(Items.BOW, Items.STONE_SWORD, random -> 0));
    private static final ImmutableMap<EquipmentSlotType, Function<Random, ItemStack>> WEAPON_MAP_T4 = ImmutableMap.of(EquipmentSlotType.MAINHAND, random2(Items.BOW, Items.IRON_SWORD, random -> random.nextDouble() < 0.7 ? 0 : 5 + random.nextInt(4)));
    private static final ImmutableMap<EquipmentSlotType, Function<Random, ItemStack>> WEAPON_MAP_T7 = ImmutableMap.<EquipmentSlotType, Function<Random, ItemStack>>builder()
            .put(EquipmentSlotType.MAINHAND, random2(Items.BOW, Items.IRON_SWORD, random -> random.nextBoolean() ? 0 : 8 + random.nextInt(5)))
            .put(EquipmentSlotType.HEAD, randomEnchantedArmor(Items.IRON_HELMET))
            .put(EquipmentSlotType.CHEST, randomEnchantedArmor(Items.IRON_CHESTPLATE))
            .put(EquipmentSlotType.LEGS, randomEnchantedArmor(Items.IRON_LEGGINGS))
            .put(EquipmentSlotType.FEET, randomEnchantedArmor(Items.IRON_BOOTS))
            .build();

    private static final Int2ObjectMap<ImmutableMap<EquipmentSlotType, Function<Random, ItemStack>>> WEAPON_MAP_MAP = EntityUtils.<ImmutableMap<EquipmentSlotType, Function<Random, ItemStack>>>choiceBuilder().range(1, 3).value(WEAPON_MAP).range(4, 6).value(WEAPON_MAP_T4).range(7, 9).value(WEAPON_MAP_T7).build();

    public SkeletonFollowerEntity(EntityType<? extends SkeletonFollowerEntity> skeleton, World world) {
        super(skeleton, world);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new RestrictSunGoal(this));
        goalSelector.addGoal(3, new FleeSunGoal(this, 1));
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, WolfEntity.class, 6, 1, 1.2));
        goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(6, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new CopyOwnerTargetGoal<>(this));
        targetSelector.addGoal(2, new HurtByTargetGoal(this, AbstractSkeletonEntity.class).setAlertOthers());
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    private static Function<Random, ItemStack> randomEnchantedArmor(Item armor) {
        return random -> randomEnchanted(randomIn -> randomIn.nextBoolean() ? 0 : 3 + randomIn.nextInt(4), random, new ItemStack(armor));
    }

    private static Function<Random, ItemStack> random2(IItemProvider f, IItemProvider s, ToIntFunction<Random> enchantmentLevelFunction) {
        return random -> randomEnchanted(enchantmentLevelFunction, random, random.nextBoolean() ? new ItemStack(f) : new ItemStack(s));
    }

    private static ItemStack randomEnchanted(ToIntFunction<Random> enchantmentLevelFunction, Random random, ItemStack stack) {
        int enchantmentLevel = enchantmentLevelFunction.applyAsInt(random);
        if (enchantmentLevel > 0) {
            EnchantmentHelper.enchantItem(random, stack, enchantmentLevel, false);
        }
        return stack;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        ILivingEntityData result = super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
        ImmutableMap<EquipmentSlotType, Function<Random, ItemStack>> weaponMap = getCorrectWeaponMap();
        for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
            setItemSlot(slotType, weaponMap.getOrDefault(slotType, random -> ItemStack.EMPTY).apply(random));
        }
        return result;
    }

    private ImmutableMap<EquipmentSlotType, Function<Random, ItemStack>> getCorrectWeaponMap() {
        if (getOwner() == null) {
            return ImmutableMap.of();
        }
        return getOwner().reachedTier(10) ? WEAPON_MAP_T7 : WEAPON_MAP_MAP.get(getOwner().getTier());
    }

    @Override
    public boolean shouldSetDirectDamageToOwner() {
        return true;
    }

    @Override
    public boolean shouldSetIndirectDamageToOwner() {
        return true;
    }

    @Override
    protected AbstractArrowEntity getArrow(ItemStack stack, float power) {
        AbstractArrowEntity arrow = super.getArrow(stack, power);
        if (getOwner() != null) {
            arrow.setOwner(getOwner());
        }
        return arrow;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof SkeletonKingEntity) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
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
}
