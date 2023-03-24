package lych.soullery.gui.container;

import com.google.common.base.Preconditions;
import lych.soullery.block.ModBlocks;
import lych.soullery.block.entity.SEStorageTileEntity;
import lych.soullery.gui.container.slot.SEContainerSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class SEStorageContainer extends Container {
    private final IIntArray seProgress;
    private final IIntArray storageTier;
    private final IWorldPosCallable access;
    static final int GEM_X = 80;
    static final int GEM_Y = 53;

    public SEStorageContainer(int id, BlockPos pos, PlayerInventory inventory, World world, IIntArray seProgress, IWorldPosCallable access) {
        super(ModContainers.SOUL_ENERGY_STORAGE, id);
        this.seProgress = seProgress;
        this.access = access;
        TileEntity blockEntity = world.getBlockEntity(pos);
        Preconditions.checkState(blockEntity instanceof SEStorageTileEntity, String.format("SEStorage was not found at (%s)", pos.toShortString()));
        addSlot(new SEContainerSlot(((SEStorageTileEntity) blockEntity).getInventory(), 0, GEM_X, GEM_Y));
        this.storageTier = new IntArray(1);
        this.storageTier.set(0, ((SEStorageTileEntity) blockEntity).getStorageTier());
        addDataSlots(this.seProgress);
        addDataSlots(this.storageTier);
        renderInventory(inventory, 8, 84);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(access, player, ModBlocks.SOUL_ENERGY_STORAGE) || stillValid(access, player, ModBlocks.SOUL_ENERGY_STORAGE_II);
    }

    private void renderInventory(IInventory inventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);
        // Hotbar
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

    private int addSlotRange(IInventory inventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(inventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IInventory inventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(inventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index == 0) {
            if (!moveItemStackTo(stack, 28, 37, false, player)) {
                if (!moveItemStackTo(stack, 1, 28, false, player)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        } else if (index <= 27) {
            if (!moveItemStackTo(stack, 0, 1, false, player)) {
                if (!moveItemStackTo(stack, 28, 37, false, player)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        } else if (index <= 36) {
            if (!moveItemStackTo(stack, 0, 1, false, player)) {
                if (!moveItemStackTo(stack, 1, 28, false, player)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onQuickCraft(stack, copy);
        }
        if (stack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, copy);
        if (stack.getCount() == copy.getCount()) {
//            stack.equals()
            return ItemStack.EMPTY;
        }
//        return ItemStack.EMPTY;

        return stack;
    }

    @OnlyIn(Dist.CLIENT)
    public int getSEProgress() {
        final int length = 158;

        int progress = seProgress.get(0);
        int total = seProgress.get(1);
        return total != 0 && progress != 0 ? progress * length / total : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public IIntArray getSEProgressArray() {
        return seProgress;
    }

    @Deprecated
    @Override
    protected final boolean moveItemStackTo(ItemStack itemStack, int start, int end, boolean reverse) {
        return moveItemStackTo(itemStack, start, end, reverse, null);
    }

    protected boolean moveItemStackTo(ItemStack itemStack, int start, int end, boolean reverse, @Nullable PlayerEntity player) {
        boolean changed = false;
        int index = start;
        if (reverse) {
            index = end - 1;
        }
        if (itemStack.isStackable()) {
            while (!itemStack.isEmpty()) {
                if (reverse) {
                    if (index < start) {
                        break;
                    }
                } else if (index >= end) {
                    break;
                }

                Slot slot = slots.get(index);
                ItemStack slotItemStack = slot.getItem();
                if (!slotItemStack.isEmpty() && consideredTheSameItem(itemStack, slotItemStack)) {
                    int totalCount = slotItemStack.getCount() + itemStack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), itemStack.getMaxStackSize());
                    if (totalCount <= maxSize) {
                        itemStack.setCount(0);
                        slotItemStack.setCount(totalCount);
                        slot.setChanged();
                        changed = true;
                    } else if (slotItemStack.getCount() < maxSize) {
                        itemStack.shrink(maxSize - slotItemStack.getCount());
                        slotItemStack.setCount(maxSize);
                        slot.setChanged();
                        changed = true;
                    }
                }

                if (reverse) {
                    --index;
                } else {
                    ++index;
                }
            }
        }

        if (!itemStack.isEmpty()) {
            if (reverse) {
                index = end - 1;
            } else {
                index = start;
            }

            while (true) {
                if (reverse) {
                    if (index < start) {
                        break;
                    }
                } else if (index >= end) {
                    break;
                }

                Slot slot = slots.get(index);
                ItemStack slotItemStack = slot.getItem();

                if (slotItemStack.isEmpty() && slot.mayPlace(itemStack)) {
                    if (itemStack.getCount() > slot.getMaxStackSize()) {
                        slot.set(itemStack.split(slot.getMaxStackSize()));
                    } else {
                        slot.set(itemStack.split(itemStack.getCount()));
                    }
//                  Broadcast changes immediately to prevent copying item.
                    broadcastChanges();
                    slot.setChanged();
                    changed = true;
                    break;
                }

                if (reverse) {
                    --index;
                } else {
                    ++index;
                }
            }
        }

        return changed;
    }

    public int getStorageTier() {
        return storageTier.get(0);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
    }
}
