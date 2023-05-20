package lych.soullery.mixin;

import com.mojang.authlib.GameProfile;
import lych.soullery.Soullery;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.config.ConfigHelper;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.gui.container.inventory.ExtraAbilityInventory;
import lych.soullery.item.EnderLauncherItem;
import lych.soullery.util.AdditionalCooldownTracker;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.InventoryUtils;
import lych.soullery.util.ModDataSerializers;
import lych.soullery.util.mixin.IFoodStatsMixin;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static lych.soullery.util.ExtraAbilityConstants.ULTRAREACH_HORIZONTAL_BONUS;
import static lych.soullery.util.ExtraAbilityConstants.ULTRAREACH_VERTICAL_BONUS;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IPlayerEntityMixin {
    @Shadow @Final public PlayerInventory inventory;
    @Shadow protected FoodStats foodData;

    @Override
    @Shadow public abstract ItemStack eat(World p_213357_1_, ItemStack p_213357_2_);

    @Shadow public abstract void playNotifySound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_);

    private static final DataParameter<Integer> DATA_OPERATING_ID = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);
    private static final DataParameter<Set<IExtraAbility>> DATA_EXTRA_ABILITIES = EntityDataManager.defineId(PlayerEntity.class, ModDataSerializers.EXA);
    private static final DataParameter<ItemStack> DATA_USING_ENDER_LAUNCHER = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> DATA_ENDER_LAUNCHER_TIME = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);
    private boolean isStatic = true;
    private final ExtraAbilityInventory extraAbilityCarrierInventory = new ExtraAbilityInventory(6);
    @Unique
    private final Map<EntityType<?>, Integer> bossTierMap = new HashMap<>();
    @Unique
    private final List<ItemStack> savableItems = new ArrayList<>();
    @Unique
    private final AdditionalCooldownTracker additionalCooldowns = new AdditionalCooldownTracker();
    @Nullable
    @Unique
    private UUID operatingMob;

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineMoreData(CallbackInfo ci) {
        entityData.define(DATA_OPERATING_ID, -1);
        entityData.define(DATA_EXTRA_ABILITIES, new LinkedHashSet<>());
        entityData.define(DATA_USING_ENDER_LAUNCHER, ItemStack.EMPTY);
        entityData.define(DATA_ENDER_LAUNCHER_TIME, 0);
    }

    @Nullable
    @Override
    public MobEntity getOperatingMob() {
        Entity entity;
        if (level.isClientSide()) {
            entity = level.getEntity(entityData.get(DATA_OPERATING_ID));
        } else {
            entity = ((ServerWorld) level).getEntity(operatingMob);
        }
        return EntityUtils.isAlive(entity) && entity instanceof MobEntity ? (MobEntity) entity : null;
    }

    @Override
    public void setOperatingMob(@Nullable MobEntity mob) {
        if (mob == null) {
            operatingMob = null;
            entityData.set(DATA_OPERATING_ID, -1);
            return;
        }
        operatingMob = mob.getUUID();
        entityData.set(DATA_OPERATING_ID, mob.getId());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tick()V", shift = At.Shift.AFTER))
    private void syncTick(CallbackInfo ci) {
        if (!level.isClientSide() && operatingMob != null) {
            Entity entity = ((ServerWorld) level).getEntity(operatingMob);
            if (EntityUtils.isAlive(entity) && entity instanceof MobEntity) {
                setOperatingMob((MobEntity) entity);
            } else {
                MindOperator.resetAndAddCooldown((PlayerEntity) (Object) this, 40);
            }
        }
        if (getEnderLauncherRemainingTicks() <= 0) {
            entityData.set(DATA_USING_ENDER_LAUNCHER, ItemStack.EMPTY);
        } else {
            entityData.set(DATA_ENDER_LAUNCHER_TIME, getEnderLauncherRemainingTicks() - 1);
        }
    }

    @Override
    public Set<IExtraAbility> getExtraAbilities() {
        return entityData.get(DATA_EXTRA_ABILITIES);
    }

    @Override
    public void setExtraAbilities(Set<IExtraAbility> set) {
        entityData.set(DATA_EXTRA_ABILITIES, set);
    }

    @Override
    public Map<EntityType<?>, Integer> getBossTierMap() {
        return bossTierMap;
    }

    @Override
    public void addSavableItem(ItemStack stack) {
        savableItems.add(stack.copy());
    }

    @Override
    public List<ItemStack> getSavableItems() {
        return savableItems;
    }

    @Override
    public void restoreSavableItemsFrom(PlayerEntity old) {
        ((IPlayerEntityMixin) old).getSavableItems().forEach(inventory::add);
        ((IPlayerEntityMixin) old).getSavableItems().clear();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void handleFoodData(World world, BlockPos pos, float yRot, GameProfile profile, CallbackInfo ci) {
        ((IFoodStatsMixin) foodData).setPlayer((PlayerEntity) (Object) this);
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;"))
    private AxisAlignedBB modifyTouchableRange(AxisAlignedBB bb) {
        if (hasExtraAbility(ExtraAbility.ULTRAREACH)) {
            return bb.inflate(ULTRAREACH_HORIZONTAL_BONUS, ULTRAREACH_VERTICAL_BONUS, ULTRAREACH_HORIZONTAL_BONUS);
        }
        return bb;
    }

    @ModifyConstant(method = "drop(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/item/ItemEntity;", constant = @Constant(intValue = 40))
    private int lengthenPickupDelay(int oldPickupDelay, ItemStack stack, boolean randomlyDrop) {
        if (!randomlyDrop && hasExtraAbility(ExtraAbility.ULTRAREACH)) {
            return oldPickupDelay + ConfigHelper.getUltrareachLengthenPickupDelayAmount();
        }
        return oldPickupDelay;
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V"))
    private void saveMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        ListNBT extraAbilityListNBT = new ListNBT();
        for (IExtraAbility exa : getExtraAbilities()) {
            CompoundNBT singleExaNBT = new CompoundNBT();
            singleExaNBT.putString("RegistryName", exa.getRegistryName().toString());
            extraAbilityListNBT.add(singleExaNBT);
        }
        compoundNBT.put("ExtraAbilities", extraAbilityListNBT);

        ListNBT bossTierNBT = new ListNBT();
        for (EntityType<?> type : bossTierMap.keySet()) {
            CompoundNBT singleBossTierNBT = new CompoundNBT();
            Objects.requireNonNull(type.getRegistryName(), "RegistryName should be non-null");
            singleBossTierNBT.putString("BossType", type.getRegistryName().toString());
            singleBossTierNBT.putInt("BossTier", bossTierMap.get(type));
            bossTierNBT.add(singleBossTierNBT);
        }
        compoundNBT.put("BossTiers", bossTierNBT);

        if (!savableItems.isEmpty()) {
            ListNBT savableItemNBT = new ListNBT();
            for (ItemStack stack : savableItems) {
                savableItemNBT.add(stack.save(new CompoundNBT()));
            }
            compoundNBT.put("StriderReinforcementSavableItems", savableItemNBT);
        }

        compoundNBT.put("SCAdditionalCooldowns", getAdditionalCooldowns().save());

        ListNBT exaInventoryNBT = new ListNBT();
        for (ItemStack stack : InventoryUtils.listView(getExtraAbilityCarrierInventory())) {
            exaInventoryNBT.add(stack.save(new CompoundNBT()));
        }
        compoundNBT.put("ExtraAbilityCarrierInventory", exaInventoryNBT);

        if (operatingMob != null) {
            compoundNBT.putUUID("OperatingMob", operatingMob);
        }
    }

    @SuppressWarnings("all")
    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V"))
    private void loadMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        if (compoundNBT.contains("ExtraAbilities", Constants.NBT.TAG_LIST)) {
            Set<IExtraAbility> set = new LinkedHashSet<>();
            ListNBT extraAbilityListNBT = compoundNBT.getList("ExtraAbilities", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < extraAbilityListNBT.size(); i++) {
                CompoundNBT singleExa = extraAbilityListNBT.getCompound(i);
                ResourceLocation registryName = ResourceLocation.tryParse(singleExa.getString("RegistryName"));
                Objects.requireNonNull(registryName, "Registry name should be non-null");
                if (!ExtraAbility.getOptional(registryName).isPresent()) {
                    Soullery.LOGGER.warn(ExtraAbility.MARKER, String.format("Found unknown Extra Ability: %s, ignored", registryName));
                }
                ExtraAbility.getOptional(registryName).ifPresent(set::add);
            }
            setExtraAbilities(set);
        }

        if (compoundNBT.contains("BossTiers", Constants.NBT.TAG_LIST)) {
            bossTierMap.clear();
            ListNBT bossTierNBT = compoundNBT.getList("BossTiers", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < bossTierNBT.size(); i++) {
                CompoundNBT singleBossTierNBT = bossTierNBT.getCompound(i);
                try {
                    EntityType<?> type = getEntityTypeBy(singleBossTierNBT, "BossType");
                    if (type != null) {
                        int tier = singleBossTierNBT.getInt("BossTier");
                        bossTierMap.put(type, tier);
                    }
                } catch (ResourceLocationException e) {
                    Soullery.LOGGER.error("Failed to parse ResourceLocation when trying to load boss tiers", e);
                }
            }
        }

        if (compoundNBT.contains("StriderReinforcementSavableItems", Constants.NBT.TAG_LIST)) {
            ListNBT savableItemNBT = compoundNBT.getList("StriderReinforcementSavableItems", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < savableItemNBT.size(); i++) {
                savableItems.add(ItemStack.of(savableItemNBT.getCompound(i)));
            }
        }

        if (compoundNBT.contains("SCAdditionalCooldowns", Constants.NBT.TAG_COMPOUND)) {
            additionalCooldowns.reloadFrom(compoundNBT.getCompound("SCAdditionalCooldowns"));
        }

        if (compoundNBT.contains("ExtraAbilityCarrierInventory", Constants.NBT.TAG_LIST)) {
            extraAbilityCarrierInventory.clearContent();
            ListNBT exaInventoryNBT = compoundNBT.getList("ExtraAbilityCarrierInventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < exaInventoryNBT.size(); i++) {
                CompoundNBT itemNBT = exaInventoryNBT.getCompound(i);
                extraAbilityCarrierInventory.setItem(i, ItemStack.of(itemNBT));
            }
        }

        if (compoundNBT.contains("OperatingMob")) {
            operatingMob = compoundNBT.getUUID("OperatingMob");
        }
    }

    @Override
    public ExtraAbilityInventory getExtraAbilityCarrierInventory() {
        return extraAbilityCarrierInventory;
    }

    @Override
    public AdditionalCooldownTracker getAdditionalCooldowns() {
        return additionalCooldowns;
    }

    @SuppressWarnings("SameParameterValue")
    @Nullable
    private EntityType<?> getEntityTypeBy(CompoundNBT singleBossTierNBT, String name) {
        ResourceLocation registryName = new ResourceLocation(singleBossTierNBT.getString(name));
        @SuppressWarnings("deprecation")
        EntityType<?> type = Registry.ENTITY_TYPE.getOptional(registryName).orElse(null);
        if (type == null) {
            Soullery.LOGGER.warn(String.format("Found unknown Entity Type: %s, ignored", registryName));
            return null;
        }
        return type;
    }

    @Override
    @Unique
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    @Unique
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public DataParameter<Integer> getOperatingMobData() {
        return DATA_OPERATING_ID;
    }

    @Override
    public int getEnderLauncherRemainingTicks() {
        if (entityData.get(DATA_USING_ENDER_LAUNCHER).isEmpty()) {
            return 0;
        }
        return entityData.get(DATA_ENDER_LAUNCHER_TIME);
    }

    @Override
    public ItemStack getEnderLauncher() {
        return entityData.get(DATA_USING_ENDER_LAUNCHER);
    }

    @Override
    public void setUsingEnderLauncher(ItemStack usingEnderLauncher) {
        entityData.set(DATA_USING_ENDER_LAUNCHER, usingEnderLauncher);
        entityData.set(DATA_ENDER_LAUNCHER_TIME, EnderLauncherItem.ROTATE_TICKS);
    }
}
