package lych.soullery.mixin;

import lych.soullery.item.ModItems;
import lych.soullery.item.potion.IHalfUsedPotion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {
    @Inject(method = "finishUsingItem", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void setToHalfUsedPotion(ItemStack stack, World world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (IHalfUsedPotion.canChemistApply(this, entity)) {
            cir.setReturnValue(IHalfUsedPotion.createHalfUsedPotion(stack, ModItems.HALF_USED_POTION));
        }
    }

    @Redirect(method = "finishUsingItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;add(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean addHalfUsedPotion(PlayerInventory instance, ItemStack glassBottle, ItemStack stack, World world, LivingEntity entity) {
        if (IHalfUsedPotion.canChemistApply(this, entity)) {
            return instance.add(IHalfUsedPotion.createHalfUsedPotion(stack, ModItems.HALF_USED_POTION));
        }
        return instance.add(glassBottle);
    }
}
