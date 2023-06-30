package lych.soullery.mixin;

import lych.soullery.api.event.ArrowSpawnEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Inject(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void fireEvent(World world, LivingEntity entity, Hand hand, ItemStack crossbow, ItemStack projectile, float shotPitch, boolean noPickup, float strength, float deviation, float angle, CallbackInfo ci, boolean fireworkRocket, ProjectileEntity projectileEntity) {
        if (entity instanceof PlayerEntity && !fireworkRocket && projectileEntity instanceof AbstractArrowEntity){
            MinecraftForge.EVENT_BUS.post(new ArrowSpawnEvent(crossbow, (PlayerEntity) entity, (AbstractArrowEntity) projectileEntity));
        }
    }
}
