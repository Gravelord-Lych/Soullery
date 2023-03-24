package lych.soullery.item.crafting;

import lych.soullery.item.ModItems;
import lych.soullery.item.SoulPieceItem;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SoulContainerRecipe extends SpecialRecipe {
    private static final Ingredient SOUL_PIECE = Ingredient.of(ModItems.SOUL_PIECE);

    public SoulContainerRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        if (inventory.getHeight() < 3 || inventory.getWidth() < 3) {
            return false;
        }
        ItemStack firstItem = inventory.getItem(0);
        if (!SOUL_PIECE.test(firstItem)) {
            return false;
        }
        EntityType<?> requiredType = SoulPieceItem.getType(firstItem);
        if (requiredType == null) {
            return false;
        }
        for (int i = 1; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!SOUL_PIECE.test(stack)) {
                return false;
            }
            EntityType<?> type = SoulPieceItem.getType(stack);
            if (type != requiredType) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInventory inventory) {
        ItemStack firstItem = inventory.getItem(0);
        if (!SOUL_PIECE.test(firstItem)) {
            return ItemStack.EMPTY;
        }
        EntityType<?> requiredType = SoulPieceItem.getType(firstItem);
        if (requiredType == null) {
            return ItemStack.EMPTY;
        }
        for (int i = 1; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!SOUL_PIECE.test(stack)) {
                return ItemStack.EMPTY;
            }
            EntityType<?> type = SoulPieceItem.getType(stack);
            if (type != requiredType) {
                return ItemStack.EMPTY;
            }
        }
        ItemStack stack = new ItemStack(ModItems.SOUL_CONTAINER);
        SoulPieceItem.setType(stack, requiredType);
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOUL_CONTAINER.get();
    }
}
