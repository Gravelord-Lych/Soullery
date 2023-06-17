package lych.soullery.item;

import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.InventoryUtils;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFixerItem extends AbstractTickableItem implements ISkillPerformable {
    private static final int SKILL_SE_COST = 800;
    private static final int SKILL_MAX_REPAIR = 600;
    private static final int SKILL_COOLDOWN = 20 * 20;
    private static final int SE_COST_PER_DAMAGE_FOR_HIGH_DURABILITY_ITEM = 2;
    private static final int SE_COST_PER_DAMAGE_FOR_MID_DURABILITY_ITEM = 3;
    private static final int SE_COST_PER_DAMAGE_FOR_LOW_DURABILITY_ITEM = 5;
    private static final int SE_COST_PER_DAMAGE_FOR_LOWER_DURABILITY_ITEM = 10;

    public ItemFixerItem(Properties properties) {
        super(properties);
    }

    @Override
    protected void serverPlayerInventoryTick(ItemStack stack, World world, PlayerEntity player, int index, boolean selected) {
        if (player.tickCount % 100 == 0) {
            for (ItemStack stackIn : InventoryUtils.listView(player.inventory)) {
                if (canRepair(stackIn)) {
                    if (SoulEnergies.cost(player, getSECostFor(stackIn))) {
                        stackIn.setDamageValue(stackIn.getDamageValue() - 1);
                    }
                }
            }
        }
    }

    private static boolean canRepair(ItemStack stackIn) {
        return stackIn.isDamageableItem() && stackIn.getDamageValue() > 0;
    }

    private static int getSECostFor(ItemStack stack) {
        int se = getBaseSECostFor(stack);
        int mul = 1, div = 1;
        if (!stack.getEnchantmentTags().isEmpty()) {
            mul += 2;
            div += 1;
        }
        if (ReinforcementHelper.hasReinforcements(stack)) {
            mul += 2;
            div += 1;
        }
        return se * mul / div;
    }

    private static int getBaseSECostFor(ItemStack stack) {
        if (stack.getMaxDamage() >= 1000) {
            return SE_COST_PER_DAMAGE_FOR_HIGH_DURABILITY_ITEM;
        }
        if (stack.getMaxDamage() >= 250) {
            return SE_COST_PER_DAMAGE_FOR_MID_DURABILITY_ITEM;
        }
        if (stack.getMaxDamage() >= 50) {
            return SE_COST_PER_DAMAGE_FOR_LOW_DURABILITY_ITEM;
        }
        if (stack.getMaxDamage() >= 30) {
            return SE_COST_PER_DAMAGE_FOR_LOWER_DURABILITY_ITEM;
        }
        return 500 / stack.getMaxDamage();
    }

    @Override
    public boolean perform(ItemStack stack, ServerPlayerEntity player) {
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        for (ItemStack stackIn : InventoryUtils.getSortedList(player.inventory)) {
            int mul = 1;
            if (!stack.getEnchantmentTags().isEmpty()) {
                mul += 1;
            }
            if (ReinforcementHelper.hasReinforcements(stack)) {
                mul += 1;
            }
            if (canRepair(stackIn) && SoulEnergies.cost(player, SKILL_SE_COST * mul)) {
                stackIn.setDamageValue(Math.max(stackIn.getDamageValue() - Math.min(SKILL_MAX_REPAIR, stackIn.getMaxDamage() / 3), 0));
                player.getCooldowns().addCooldown(this, SKILL_COOLDOWN);
                return true;
            }
        }
        return false;
    }

    @Override
    public void performed(ItemStack stack, ServerPlayerEntity player) {
        ISkillPerformable.super.performed(stack, player);
        EntityUtils.spawnAnimServerside(player, player.getLevel());
    }
}
