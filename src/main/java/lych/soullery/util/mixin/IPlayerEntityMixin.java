package lych.soullery.util.mixin;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.gui.container.inventory.ExtraAbilityInventory;
import lych.soullery.util.AdditionalCooldownTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static lych.soullery.entity.iface.ITieredMob.MAX_TIER;
import static lych.soullery.entity.iface.ITieredMob.MIN_TIER;

public interface IPlayerEntityMixin {
    Set<IExtraAbility> getExtraAbilities();

    void setExtraAbilities(Set<IExtraAbility> set);

    @Nullable
    MobEntity getOperatingMob();

    void setOperatingMob(@Nullable MobEntity mob);

    DataParameter<Integer> getOperatingMobData();

    default boolean hasExtraAbility(IExtraAbility exa) {
        return getExtraAbilities().contains(exa);
    }

    default boolean addExtraAbility(IExtraAbility exa) {
        Set<IExtraAbility> set = new LinkedHashSet<>(getExtraAbilities());
        boolean added = set.add(exa);
        if (added) {
            setExtraAbilities(set);
            return true;
        }
        return false;
    }

    default boolean removeExtraAbility(IExtraAbility exa) {
        Set<IExtraAbility> set = new LinkedHashSet<>(getExtraAbilities());
        boolean removed = set.remove(exa);
        if (removed) {
            setExtraAbilities(set);
            return true;
        }
        return false;
    }

    Map<EntityType<?>, Integer> getBossTierMap();

    default int getTier(EntityType<?> type) {
        return MathHelper.clamp(getBossTierMap().getOrDefault(type, MIN_TIER), MIN_TIER, MAX_TIER);
    }

    default void setTier(EntityType<?> type, int tier) {
        getBossTierMap().put(type, MathHelper.clamp(tier, MIN_TIER, MAX_TIER));
    }

    default void upgrade(EntityType<?> type) {
        setTier(type, Math.min(getTier(type) + 1, MAX_TIER));
    }

    void addSavableItem(ItemStack stack);

    List<ItemStack> getSavableItems();

    void restoreSavableItemsFrom(PlayerEntity old);

    AdditionalCooldownTracker getAdditionalCooldowns();

    boolean isStatic();

    void setStatic(boolean isStatic);

    ExtraAbilityInventory getExtraAbilityCarrierInventory();

    int getEnderLauncherRemainingTicks();

    void setUsingEnderLauncher(ItemStack enderLauncher);

    ItemStack getEnderLauncher();
}
