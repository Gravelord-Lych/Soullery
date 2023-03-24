package lych.soullery.mixin;

import lych.soullery.item.ModItems;
import lych.soullery.item.potion.IHalfUsedPotion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrowablePotionItem.class)
public class ThrowablePotionItemMixin {
    @SuppressWarnings("ConstantValue")
    @ModifyVariable(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V", shift = At.Shift.AFTER))
    private ItemStack addHalfUsedPotion(ItemStack stack, World world, PlayerEntity player, Hand hand) {
        boolean isSplashOrLingering = (ThrowablePotionItem) (Object) this instanceof SplashPotionItem || (ThrowablePotionItem) (Object) this instanceof LingeringPotionItem;
        if (isSplashOrLingering && IHalfUsedPotion.canChemistApply(this, player)) {
            return IHalfUsedPotion.createHalfUsedPotion(player.getItemInHand(hand), (ThrowablePotionItem) (Object) this instanceof SplashPotionItem ? ModItems.HALF_USED_SPLASH_POTION : ModItems.HALF_USED_LINGERING_POTION);
        }
        return stack;
    }
}
