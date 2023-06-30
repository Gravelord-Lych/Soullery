package lych.soullery.capability;

import lych.soullery.api.capability.APICapabilities;
import lych.soullery.api.capability.IItemVanishingSkillData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemVanishingSkillDataProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    private final ServerPlayerEntity player;
    private ItemVanishingSkillData cap;

    public ItemVanishingSkillDataProvider(ServerPlayerEntity player) {
        this.player = player;
    }

    @NotNull
    @Override
    public <O> LazyOptional<O> getCapability(@NotNull Capability<O> cap, @Nullable Direction side) {
        return cap == APICapabilities.ITEM_VANISHING_SKILL ? LazyOptional.of(this::getOrCreateCap).cast() : LazyOptional.empty();
    }

    private IItemVanishingSkillData getOrCreateCap() {
        if (cap == null) {
            cap = new ItemVanishingSkillData(player);
        }
        return cap;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return getOrCreateCap().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        getOrCreateCap().deserializeNBT(nbt);
    }
}
