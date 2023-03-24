package lych.soullery.mixin;

import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.extension.soulpower.reinforce.StriderReinforcement;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getItem();

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void striderReinforcementFireImmune(CallbackInfoReturnable<Boolean> cir) {
        int level = Reinforcements.STRIDER.getLevel(getItem());
        if (level >= StriderReinforcement.FIRE_RESISTANT_LEVEL) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;canBeHurtBy(Lnet/minecraft/util/DamageSource;)Z"))
    private boolean applyStriderReinforcement(Item instance, DamageSource source) {
        int level = Reinforcements.STRIDER.getLevel(getItem());
        if (level >= StriderReinforcement.ALL_RESISTANT_LEVEL) {
            return false;
        } else if (level >= StriderReinforcement.FIRE_RESISTANT_LEVEL) {
            return !source.isFire();
        }
        return instance.canBeHurtBy(source);
    }
}
