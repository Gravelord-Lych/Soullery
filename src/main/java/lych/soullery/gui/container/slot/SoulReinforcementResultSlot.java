package lych.soullery.gui.container.slot;

import com.google.common.collect.ImmutableList;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.gui.container.inventory.SoulReinforcementTableIngredientInventory;
import lych.soullery.item.ExtraAbilityCarrierItem;
import lych.soullery.item.SoulContainerItem;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SoulReinforcementResultSlot extends Slot {
    private final SoulReinforcementTableIngredientInventory ingredientSlots;

    public SoulReinforcementResultSlot(IInventory inventory, SoulReinforcementTableIngredientInventory ingredientSlots, int index, int x, int y) {
        super(inventory, index, x, y);
        this.ingredientSlots = ingredientSlots;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(PlayerEntity player, ItemStack stack) {
        ItemStack seContainer = ingredientSlots.getItem(2);
        consume(stack, seContainer);
        ingredientSlots.setItem(0, ItemStack.EMPTY);
        return super.onTake(player, stack);
    }

    private void consume(ItemStack result, ItemStack seContainer) {
        EntityType<?> type = SoulContainerItem.getType(ingredientSlots.getItem(1));
        if (type != null) {
            if (ingredientSlots.getItem(0).getItem() instanceof ExtraAbilityCarrierItem && ExtraAbilityCarrierItem.getExa(ingredientSlots.getItem(0)) == null) {
                IExtraAbility exa = ExtraAbility.byEntity(type);
                if (exa != null) {
                    int soulContainerCost = exa.getSoulContainerCost();
                    if (ingredientSlots.getItem(1).getCount() < soulContainerCost) {
                        throw new AssertionError();
                    }
                    handleRemainedSoulContainers(soulContainerCost);
                    handleEnergyCost(seContainer, exa.getSECost());
                    return;
                }
            } else {
                Reinforcement reinforcement = Reinforcements.get(type);
                if (reinforcement != null) {
                    int oldLevel = reinforcement.getLevel(ingredientSlots.getItem(0));
                    int newLevel = reinforcement.getLevel(result);
                    int soulContainerCost = reinforcement.getCost(oldLevel, newLevel);
                    if (ingredientSlots.getItem(1).getCount() < soulContainerCost) {
                        throw new AssertionError();
                    }
                    handleRemainedSoulContainers(soulContainerCost);
                    handleEnergyCost(seContainer, Reinforcements.getEnergyCost(reinforcement, oldLevel, newLevel));
                    return;
                }
            }
        }
        throw new AssertionError();
    }

    private static void handleEnergyCost(ItemStack stack, int cost) {
        if (SoulEnergies.of(stack).orElseThrow(AssertionError::new).getSoulEnergyStored() < cost) {
            throw new AssertionError();
        }
        SoulEnergies.cost(ImmutableList.of(stack), cost);
    }

    private void handleRemainedSoulContainers(int cost) {
        ItemStack remainder;
        if (ingredientSlots.getItem(1).getCount() == cost) {
            remainder = ItemStack.EMPTY;
        } else {
            remainder = ingredientSlots.getItem(1).copy();
            remainder.shrink(cost);
        }
        ingredientSlots.setItem(1, remainder);
    }
}
