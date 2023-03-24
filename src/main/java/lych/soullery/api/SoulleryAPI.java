package lych.soullery.api;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.api.exa.MobDebuff;
import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.api.shield.ISharedShield;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.world.gen.carver.SoulCaveCarver;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public interface SoulleryAPI {
    String MOD_ID = "soullery";

    /**
     * @return An instance of this. {@link SoulleryAPIDummyImpl} if SoulCraft Mod is not found
     */
    static SoulleryAPI getInstance() {
        return APIConstants.INSTANCES.get();
    }

    /**
     * Get the version of {@link SoulleryAPI}.
     * @return The version of the API
     */
    default int apiVersion() {
        return 1;
    }

    /**
     * Create an Extra Ability (for player). It was named "Extra" because there's a class called {@link net.minecraft.entity.player.PlayerAbilities PlayerAbilities}.
     * @param registryName The registry name of the Extra Ability
     * @param cost The Soul Container cost to apply the Extra Ability
     * @param special True if it's special
     * @return The Extra Ability created by the registry name. or a dummy if the API is a dummy
     */
    IExtraAbility createExtraAbility(ResourceLocation registryName, int cost, boolean special);

    /**
     * Gets an Extra Ability from the registry name.
     * @param registryName The registry name of the Extra Ability
     */
    Optional<IExtraAbility> getExtraAbilityByRegistryName(ResourceLocation registryName);

    /**
     * Gets all the Extra Abilities on the player.
     * @param player The player
     * @return All the Extra Abilities on the player
     */
    Set<IExtraAbility> getExtraAbilitiesOnPlayer(PlayerEntity player);

    /**
     * This method should only be called to check Soulcraft Extra Abilities.
     * @param path The path of the Extra Ability's registryName
     * @return True if the player has this <i>Extra Ability</i>
     */
    default boolean hasSCExtraAbility(PlayerEntity player, String path) {
        return hasExtraAbility(player, new ResourceLocation(MOD_ID, path));
    }

    /**
     * @return True if the player has this <i>Extra Ability</i>
     */
    default boolean hasExtraAbility(PlayerEntity player, ResourceLocation registryName) {
        return getExtraAbilityByRegistryName(registryName).map(exa -> exa.isOn(player)).orElse(false);
    }

    /**
     * Register an Extra Ability. The method can be called during common set up
     * @param exa The Extra Ability that will be registered
     */
    void registerExtraAbility(IExtraAbility exa);

    /**
     * Gets all registered Extra Abilities.
     * @return The Extra Abilities that were registered before the method was invoked. This map is <strong>unmodifiable</strong>
     */
    Map<ResourceLocation, IExtraAbility> getRegisteredExtraAbilities();

    /**
     * Register a block that is replaceable and can be replaced by air when {@link SoulCaveCarver} carves.
     * @param block The replaceable block
     */
    void registerSoulCaveCarverReplaceableBlock(Block block);

    /**
     * @return True if the api is a dummy
     */
    boolean isDummy();

    @Nullable
    default ICapabilityProvider getSoulEnergyProviderForItem(ItemSEContainer container, ItemStack stack) {
        return getSoulEnergyProviderForItem(container, () -> stack);
    }

    @Nullable
    ICapabilityProvider getSoulEnergyProviderForItem(ItemSEContainer container, Supplier<ItemStack> stack);

    /**
     * Bind an Extra Ability and a {@link PlayerBuff buff}
     * @return The previous buff associated with <code>exa</code>,  or <code>null</code> if there was no mapping for <code>exa</code>.
     */
    @Nullable
    PlayerBuff bind(IExtraAbility exa, PlayerBuff buff);

    /**
     * Bind an Extra Ability and a {@link MobDebuff debuff}
     * @return The previous debuff associated with <code>exa</code>,  or <code>null</code> if there was no mapping for <code>exa</code>.
     */
    @Nullable
    MobDebuff bind(IExtraAbility exa, MobDebuff debuff);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @return The newly created shared shield, <code>null</code> if Soullery is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @param consumable Whether the shield {@link ISharedShield#canBeConsumed() can be consumed} or not
     * @return The newly created shared shield, <code>null</code> if Soullery is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense, boolean consumable);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @param maxRegenInterval The {@link ISharedShield#getMaxRegenInterval() max regenerate interval}
     *                         of the shield
     * @param regenAmount The {@link ISharedShield#getRegenAmount() amount of health regenerated} during a
     *                    shield regeneration
     * @return The newly created shared shield, <code>null</code> if Soullery is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @param maxRegenInterval The {@link ISharedShield#getMaxRegenInterval() max regenerate interval}
     *                         of the shield
     * @param regenAmount The {@link ISharedShield#getRegenAmount() amount of health regenerated} during a
     *                    shield regeneration
     * @param consumable Whether the shield {@link ISharedShield#canBeConsumed() can be consumed} or not
     * @return The newly created shared shield, <code>null</code> if Soullery is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount, boolean consumable);

    /**
     * Loads a shared shield from the specific {@link CompoundNBT NBT}.
     * @param compoundNBT THe NBT that stores the shield's data
     * @return The loaded shared shield, <code>null</code> if Soullery is absent or broken
     */
    @Nullable
    ISharedShield loadShield(CompoundNBT compoundNBT);

    /**
     * Disables the shield user's shield. Also removes the shield if the shield is
     * {@link ISharedShield#canBeConsumed() consumable}
     * @param world The world
     * @param user The shield user
     * @param random The random used to add particles. <code>null</code> if no particles should
     *               be added and no sounds should be played
     */
    void disableShield(World world, IShieldUser user, @Nullable Random random);
}
