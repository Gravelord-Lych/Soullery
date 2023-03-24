package lych.soullery.mixin;

import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.item.ModRarities;
import lych.soullery.util.mixin.IItemMixin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements IItemMixin {
    @Shadow
    @Final
    private Rarity rarity;

    @Override
    public boolean isSoulFoil(ItemStack stack) {
        return false;
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void modifyRarity(ItemStack stack, CallbackInfoReturnable<Rarity> cir) {
        if (ReinforcementHelper.hasReinforcements(stack)) {
            Rarity newRarity;
            Rarity oldRarity = rarity;
            switch (oldRarity) {
                case COMMON:
                case UNCOMMON:
                    newRarity = Rarity.RARE;
                    break;
                case RARE:
                    newRarity = Rarity.EPIC;
                    break;
                case EPIC:
                    newRarity = ModRarities.LEGENDARY;
                    break;
                default:
                    newRarity = oldRarity == ModRarities.LEGENDARY ? ModRarities.MAX : oldRarity;
            }
            cir.setReturnValue(newRarity);
        }
    }
}
