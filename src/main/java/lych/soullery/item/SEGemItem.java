package lych.soullery.item;

import lych.soullery.Soullery;
import lych.soullery.api.ItemSEContainer;
import lych.soullery.capability.ItemSoulEnergyProvider;
import lych.soullery.util.SoulEnergies;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import java.util.List;

import static lych.soullery.api.ItemSEContainer.getMode;
import static lych.soullery.api.ItemSEContainer.setMode;

public class SEGemItem extends Item implements ItemSEContainer, IModeChangeable, IUpgradeableItem {
    private static final int MAX_SE_LEVEL = 10;
    private static final int LEVEL_STEP = 5;
    private final int capacity;
    private final boolean soulFoiled;

    public SEGemItem(Properties properties, int capacity, boolean soulFoiled) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
        this.soulFoiled = soulFoiled;
    }

    @Override
    public int getCapacity(ItemStack stack) {
        return capacity;
    }

    @Override
    public int getMaxReceive(ItemStack stack) {
        return capacity;
    }

    @Override
    public int getMaxExtract(ItemStack stack) {
        return capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ItemSoulEnergyProvider(this, () -> stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tips, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tips, flag);
        ITextComponent mode = TransferMode.MODE_KEY.copy().append(getMode(stack).makeTranslationKey());
        tips.add(mode);
        SoulEnergies.addSEToolTip(stack, tips);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return SoulEnergies.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return SoulEnergies.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {
        super.fillItemCategory(group, list);
        if (allowdedIn(group)) {
            for (int i = LEVEL_STEP; i <= MAX_SE_LEVEL; i += LEVEL_STEP) {
                ItemStack stack = new ItemStack(this);
                setMode(stack, TransferMode.NORMAL);
                final int currentLevel = i;
                SoulEnergies.of(stack).ifPresent(ses -> {
                    int se = ses.getMaxSoulEnergyStored() / MAX_SE_LEVEL * currentLevel;
                    ses.forceReceiveSoulEnergy(se);
                    list.add(stack);
                });
            }
        }
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return soulFoiled;
    }

    @Override
    public void changeMode(ItemStack stack, ServerPlayerEntity player) {
        if (getMode(stack) == TransferMode.OUT) {
            setMode(stack, TransferMode.NORMAL);
        } else {
            setMode(stack, TransferMode.byId(getMode(stack).getId() + 1).orElseThrow(() -> new IllegalStateException("Something wrong happened when trying to change a Soul Energy Gem's mode")));
        }
        player.sendMessage(new TranslationTextComponent(Soullery.prefixMsg("item", "item_soul_energy_container.transfer.set_mode")).append(getMode(stack).makeTranslationKey()), Util.NIL_UUID);
    }

    @Override
    public boolean canUpgrade(ItemStack stack) {
        return stack.getItem() == ModItems.SOUL_ENERGY_GEM;
    }

    @Override
    public ItemStack upgraded(ItemStack old) {
        checkUpgradeable(old);
        return new ItemStack(ModItems.SOUL_ENERGY_GEM_II);
    }
}
