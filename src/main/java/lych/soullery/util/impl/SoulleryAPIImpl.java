package lych.soullery.util.impl;

import lych.soullery.api.ItemSEContainer;
import lych.soullery.api.SoulleryAPI;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.api.exa.MobDebuff;
import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.api.shield.ISharedShield;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.capability.ItemSoulEnergyProvider;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.extension.shield.SharedShield;
import lych.soullery.extension.soulpower.buff.PlayerBuffMap;
import lych.soullery.extension.soulpower.debuff.MobDebuffMap;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.SoulLandGenHelper;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public enum SoulleryAPIImpl implements SoulleryAPI {
    INSTANCE;

    @Override
    public IExtraAbility createExtraAbility(ResourceLocation registryName, int cost, boolean special) {
        return ExtraAbility.create(registryName, cost, special);
    }

    @Override
    public Optional<IExtraAbility> getExtraAbilityByRegistryName(ResourceLocation registryName) {
        return ExtraAbility.getOptional(registryName);
    }

    @Override
    public Set<IExtraAbility> getExtraAbilitiesOnPlayer(PlayerEntity player) {
        return ((IPlayerEntityMixin) player).getExtraAbilities();
    }

    @Override
    public void registerExtraAbility(IExtraAbility exa) {
        ExtraAbility.register(exa);
    }

    @Override
    public Map<ResourceLocation, IExtraAbility> getRegisteredExtraAbilities() {
        return ExtraAbility.getRegisteredExtraAbilities();
    }

    @Override
    public void registerSoulCaveCarverReplaceableBlock(Block block) {
        SoulLandGenHelper.registerReplaceableBlock(block);
    }

    @Override
    public boolean isDummy() {
        return false;
    }

    @Override
    public ICapabilityProvider getSoulEnergyProviderForItem(ItemSEContainer container, Supplier<ItemStack> stack) {
        return new ItemSoulEnergyProvider(container, stack);
    }

    @Nullable
    @Override
    public PlayerBuff bind(IExtraAbility exa, PlayerBuff buff) {
        return PlayerBuffMap.bind(exa, buff);
    }

    @Nullable
    @Override
    public MobDebuff bind(IExtraAbility exa, MobDebuff debuff) {
        return MobDebuffMap.bind(exa, debuff);
    }

    @NotNull
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense) {
        return new SharedShield(absoluteDefense, passiveDefense);
    }

    @NotNull
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense, boolean consumable) {
        return new SharedShield(absoluteDefense, passiveDefense, consumable);
    }

    @NotNull
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount) {
        return new SharedShield(absoluteDefense, passiveDefense, maxRegenInterval, regenAmount);
    }

    @NotNull
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount, boolean consumable) {
        return new SharedShield(absoluteDefense, passiveDefense, maxRegenInterval, regenAmount, consumable);
    }

    @NotNull
    @Override
    public ISharedShield loadShield(CompoundNBT compoundNBT) {
        return new SharedShield(compoundNBT);
    }

    @Override
    public void disableShield(World world, IShieldUser user, @Nullable Random random) {
        EntityUtils.disableShield(world, user, random);
    }
}
