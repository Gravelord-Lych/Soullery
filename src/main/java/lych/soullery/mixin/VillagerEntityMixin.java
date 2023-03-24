package lych.soullery.mixin;

import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MerchantOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends AbstractVillagerEntity {
    private VillagerEntityMixin(EntityType<? extends AbstractVillagerEntity> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void addExaOffers(PlayerEntity player, CallbackInfo ci) {
        if (ExtraAbility.FAVORED_TRADER.isOn(player)) {
            for (MerchantOffer offer : getOffers()) {
                int discount = (int) (offer.getBaseCostA().getCount() * ExtraAbilityConstants.FAVORED_TRADER_DISCOUNT);
                discount = Math.max(discount, 1);
                offer.addToSpecialPriceDiff(-discount);
            }
        }
    }
}
