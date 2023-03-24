package lych.soullery.mixin;

import lych.soullery.api.event.ArrowSpawnEvent;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.ExtraAbilityConstants;
import lych.soullery.util.mixin.IAbstractArrowEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    @Shadow public abstract ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_);
    private static boolean doubleDurabilityCost;
    private static boolean bowExpert;

    @Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void fireEvent(ItemStack stack, World world, LivingEntity entity, int useItemRemainingTicks, CallbackInfo ci, PlayerEntity playerentity, boolean flag, ItemStack itemstack, int i, float f, boolean flag1, ArrowItem arrowitem, AbstractArrowEntity abstractarrowentity) {
        MinecraftForge.EVENT_BUS.post(new ArrowSpawnEvent(stack, playerentity, abstractarrowentity));
        ((IAbstractArrowEntityMixin) abstractarrowentity).setRecordedBow(stack);
        if (doubleDurabilityCost) {
            stack.hurtAndBreak(1, playerentity, player -> player.broadcastBreakEvent(playerentity.getUsedItemHand()));
            doubleDurabilityCost = false;
        }
    }

    @ModifyVariable(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getProjectile(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private boolean doNotCostArrow(boolean noArrowCost, ItemStack stack, World world, LivingEntity user) {
        if (noArrowCost) {
            return true;
        }
        if (user instanceof PlayerEntity && ExtraAbility.BOW_EXPERT.isOn((PlayerEntity) user)) {
            if (!world.isClientSide() && user.getRandom().nextDouble() < ExtraAbilityConstants.BOW_EXPERT_DOUBLE_DURABILITY_COST_PROBABILITY) {
                doubleDurabilityCost = true;
            }
            bowExpert = true;
            return true;
        }
        return false;
    }

    @ModifyVariable(method = "releaseUsing", ordinal = 1, at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClientSide:Z"))
    private boolean doNotCostArrow2(boolean noArrowCost) {
        if (bowExpert) {
            bowExpert = false;
            return true;
        }
        return noArrowCost;
    }
}
