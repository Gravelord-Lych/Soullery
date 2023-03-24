package lych.soullery.mixin;

import lych.soullery.entity.iface.IHasOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LightningBoltEntity.class)
public abstract class LightningBoltEntityMixin extends Entity implements IHasOwner<LivingEntity> {
    @Shadow
    @Nullable
    private ServerPlayerEntity cause;

    @Shadow public abstract void setCause(@Nullable ServerPlayerEntity p_204809_1_);

    @Nullable
    private UUID ownerUUID;

    private LightningBoltEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        if (ownerUUID == null) {
            if (cause == null) {
                return null;
            }
            return cause.getUUID();
        }
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        if (ownerUUID != null) {
            PlayerEntity player = level.getPlayerByUUID(ownerUUID);
            if (player instanceof ServerPlayerEntity) {
                setCause((ServerPlayerEntity) player);
            }
        }
    }

    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true)
    private void noFire(int count, CallbackInfo ci) {
        LivingEntity owner = getOwner();
        if (owner != null && !ForgeEventFactory.getMobGriefingEvent(level, owner)) {
            ci.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void saveLightningOwner(CompoundNBT compoundNBT, CallbackInfo ci) {
        saveOwner(compoundNBT);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void loadLightningOwner(CompoundNBT compoundNBT, CallbackInfo ci) {
        loadOwner(compoundNBT);
    }
}
