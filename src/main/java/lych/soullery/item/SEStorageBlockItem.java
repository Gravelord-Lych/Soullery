package lych.soullery.item;

import lych.soullery.api.ItemSEContainer;
import lych.soullery.block.entity.SEStorageTileEntity;
import lych.soullery.capability.ItemSoulEnergyProvider;
import lych.soullery.util.SoulEnergies;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SEStorageBlockItem extends BlockItem implements ItemSEContainer {
    private final int capacity;

    public SEStorageBlockItem(Block block, int capacity, Properties properties) {
        super(block, properties.stacksTo(1));
        this.capacity = capacity;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SEStorageTileEntity) {
            SEStorageTileEntity storage = (SEStorageTileEntity) blockEntity;
            storage.setSoulEnergyStored(getSEStoredIn(stack));
        }
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - (double) getSEStoredIn(stack) / getCapacity(stack);
    }

    private static int getSEStoredIn(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(ItemSoulEnergyProvider.SOUL_ENERGY_TAG) : 0;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return SoulEnergies.getRGBDurabilityForDisplay(stack, this::getDurabilityForDisplay);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ItemSoulEnergyProvider(this, () -> stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tips, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tips, flag);
        SoulEnergies.addSEToolTip(stack, tips, SEStorageBlockItem::getSEStoredIn, this::getCapacity);
    }

    @Override
    public int getCapacity(ItemStack stack) {
        return capacity;
    }

    @Override
    public int getMaxReceive(ItemStack stack) {
        return 0;
    }

    @Override
    public int getMaxExtract(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isTransferable(ItemStack stack) {
        return false;
    }
}
