package lych.soullery.mixin;

import lych.soullery.util.mixin.IItemStackMixin;
import lych.soullery.util.mixin.ITridentEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends AbstractArrowEntity implements ITridentEntityMixin {
    @Shadow @Final private static DataParameter<Boolean> ID_FOIL;
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final DataParameter<Boolean> ID_SOUL_FOIL = EntityDataManager.defineId(TridentEntity.class, DataSerializers.BOOLEAN);

    private TridentEntityMixin(EntityType<? extends AbstractArrowEntity> type, World world) {
        super(type, world);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isSoulFoil() {
        return entityData.get(ID_SOUL_FOIL);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "RETURN"))
    public void initSoulFoil(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
        if (((IItemStackMixin) (Object) stack).hasSoulFoil()) {
            entityData.set(ID_SOUL_FOIL, true);
            entityData.set(ID_FOIL, false);
        }
    }

    @Inject(method = "defineSynchedData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/datasync/EntityDataManager;define(Lnet/minecraft/network/datasync/DataParameter;Ljava/lang/Object;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void defineSoulFoilData(CallbackInfo ci) {
        entityData.define(ID_SOUL_FOIL, false);
    }

    @Inject(method = "isFoil", at = @At(value = "HEAD"), cancellable = true)
    @OnlyIn(Dist.CLIENT)
    public void checkSoulFoilFirst(CallbackInfoReturnable<Boolean> cir) {
        if (isSoulFoil()) {
            cir.setReturnValue(false);
        }
    }
}
