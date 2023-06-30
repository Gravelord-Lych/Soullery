package lych.soullery.item;

import lych.soullery.advancements.ModCriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

public class SoulContainerItem extends SoulPieceItem {
    public SoulContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (getType(stack) != null) {
            return Rarity.EPIC;
        }
        return super.getRarity(stack);
    }

    @Override
    public void onCraftedBy(ItemStack stack, World world, PlayerEntity player) {
        super.onCraftedBy(stack, world, player);
        if (player instanceof ServerPlayerEntity) {
            ModCriteriaTriggers.CRAFTED_SOUL_CONTAINER.trigger((ServerPlayerEntity) player);
        }
    }
}
