package lych.soullery.gui.container.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;

public class SoulReinforcementTableIngredientInventory implements IInventory, IRecipeHelperPopulator {
    private final NonNullList<ItemStack> items;
    private final Container menu;

    public SoulReinforcementTableIngredientInventory(Container container, int size) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.menu = container;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= getContainerSize() ? ItemStack.EMPTY : items.get(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(items, index);
    }

    @Override
    public ItemStack removeItem(int slot, int index) {
        ItemStack stack = ItemStackHelper.removeItem(items, slot, index);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        menu.slotsChanged(this);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper) {
        items.forEach(helper::accountSimpleStack);
    }
}