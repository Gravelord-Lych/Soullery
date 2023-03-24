package lych.soullery.mixin;

import lych.soullery.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ProjectileItemEntity {
    public PotionEntityMixin(EntityType<? extends ProjectileItemEntity> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "isLingering", at = @At("HEAD"), cancellable = true)
    private void handleHalfUsedLingeringPotion(CallbackInfoReturnable<Boolean> cir) {
        if (getItem().getItem() == ModItems.HALF_USED_LINGERING_POTION) {
            cir.setReturnValue(true);
        }
    }
}
