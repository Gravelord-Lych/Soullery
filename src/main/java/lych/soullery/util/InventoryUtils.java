package lych.soullery.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public final class InventoryUtils {
    private InventoryUtils() {}

    public static List<ItemStack> listView(IInventory inventory) {
        return new InventoryList(inventory);
    }

    public static NonNullList<ItemStack> getList(IInventory inventory) {
        NonNullList<ItemStack> list = CollectionUtils.createMutableNonNullListWithSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            list.add(stack);
        }
        return list;
    }

    public static NonNullList<ItemStack> getSortedList(PlayerInventory inventory) {
        NonNullList<ItemStack> list = CollectionUtils.createMutableNonNullListWithSize(inventory.getContainerSize(), ItemStack.EMPTY);
        list.addAll(inventory.offhand);
        list.addAll(Lists.reverse(inventory.armor));
        list.addAll(inventory.items);
        return list;
    }

    public static List<ItemStack> getCopyOf(IInventory inventory) {
        NonNullList<ItemStack> list = CollectionUtils.createMutableNonNullListWithSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            list.add(stack.copy());
        }
        return list;
    }

    public static boolean hasAnyOf(IInventory inventory, Item... items) {
        return inventory.hasAnyOf(new HashSet<>(Arrays.asList(items)));
    }

    public static boolean hasAllOf(IInventory inventory, Item... items) {
        return getList(inventory).stream().noneMatch(stack -> Arrays.stream(items).anyMatch(item -> !(stack.getItem() == item)));
    }

    @SafeVarargs
    public static boolean hasAnyOf(IInventory inventory, ITag<Item>... tags) {
        return getList(inventory).stream().anyMatch(stack -> Arrays.stream(tags).anyMatch(tag -> stack.getItem().is(tag)));
    }

    @SafeVarargs
    public static boolean hasAllOf(IInventory inventory, ITag<Item>... tags) {
        return getList(inventory).stream().noneMatch(stack -> Arrays.stream(tags).anyMatch(tag -> !stack.getItem().is(tag)));
    }

    public static boolean anyMatch(IInventory inventory, Predicate<? super ItemStack> predicate) {
        return getList(inventory).stream().anyMatch(predicate);
    }

    public static boolean allMatch(IInventory inventory, Predicate<? super ItemStack> predicate) {
        return getList(inventory).stream().allMatch(predicate);
    }

    public static NonNullList<ItemStack> get(IInventory inventory, Item item) {
        return getList(inventory).stream().filter(stack -> stack.getItem() == item).collect(CollectionUtils.toNonNullList(ItemStack.EMPTY));
    }

    public static NonNullList<ItemStack> get(IInventory inventory, ITag<Item> tag) {
        return getList(inventory).stream().filter(stack -> stack.getItem().is(tag)).collect(CollectionUtils.toNonNullList(ItemStack.EMPTY));
    }

    public static NonNullList<ItemStack> get(IInventory inventory, Predicate<ItemStack> predicate) {
        return getList(inventory).stream().filter(predicate).collect(CollectionUtils.toNonNullList(ItemStack.EMPTY));
    }

    public static NonNullList<ItemStack> getHotbar(PlayerInventory inventory) {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < inventory.items.size(); i++) {
            if (PlayerInventory.isHotbarSlot(i)) {
                list.add(inventory.items.get(i));
            }
        }
        return list;
    }

    public static boolean isInHand(ItemStack stack, PlayerEntity player, boolean selected) {
        return selected || player.inventory.offhand.contains(stack);
    }

    public static List<ItemStack> getInventoryItemsIfIsPlayer(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return Collections.emptyList();
        }
        PlayerEntity player = (PlayerEntity) entity;
        return new ArrayList<>(player.inventory.items);
    }

    private static class InventoryList extends AbstractList<ItemStack> implements RandomAccess {
        private final IInventory inventory;

        private InventoryList(IInventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public ItemStack get(int index) {
            return inventory.getItem(index);
        }

        @Override
        public ItemStack set(int index, ItemStack element) {
            ItemStack prev = inventory.getItem(index);
            inventory.setItem(index, element);
            return prev;
        }

        @Override
        public int size() {
            return inventory.getContainerSize();
        }

        @Override
        public void clear() {
            inventory.clearContent();
        }

        @SuppressWarnings("ConstantValue")
        @Override
        public int indexOf(@Nullable Object o) {
            IInventory inventory = this.inventory;
            if (o == null) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    if (inventory.getItem(i) == null) {
                        return i;
                    }
                }
            } else {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    if (o.equals(inventory.getItem(i))) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) != -1;
        }
    }
}
