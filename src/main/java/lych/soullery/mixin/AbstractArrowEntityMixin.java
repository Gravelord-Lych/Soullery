package lych.soullery.mixin;

import lych.soullery.entity.projectile.EtherealArrowEntity;
import lych.soullery.extension.soulpower.reinforce.SkeletonReinforcement;
import lych.soullery.util.mixin.IAbstractArrowEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(AbstractArrowEntity.class)
public abstract class AbstractArrowEntityMixin extends ProjectileEntity implements IAbstractArrowEntityMixin {
    @Shadow protected boolean inGround;
    private int enhancedLevel;
    @Nullable
    private ItemStack recordedBow;

    private AbstractArrowEntityMixin(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/AbstractArrowEntity;isInWaterOrRain()Z"))
    private void noInsideBlockForEtheArrow(CallbackInfo ci) {
        if ((Object) this instanceof EtherealArrowEntity && inGround) {
            inGround = false;
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        compoundNBT.putInt("EnhancedLevel", getEnhancedLevel());
        if (getRecordedBow() != null) {
            compoundNBT.put("RecordedBow", getRecordedBow().save(new CompoundNBT()));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        setEnhancedLevel(compoundNBT.getInt("EnhancedLevel"));
        if (compoundNBT.contains("RecordedBow", Constants.NBT.TAG_COMPOUND)) {
            setRecordedBow(ItemStack.of(compoundNBT.getCompound("RecordedBow")));
        }
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/vector/Vector3d;scale(D)Lnet/minecraft/util/math/vector/Vector3d;"))
    private double enlargeScale(double oldScale) {
        return oldScale * (1 + getEnhancedLevel() * SkeletonReinforcement.ENHANCED_ARROW_SPEED_MULTIPLIER);
    }

    @Override
    public int getEnhancedLevel() {
        return enhancedLevel;
    }

    @Override
    public void setEnhancedLevel(int enhancedLevel) {
        this.enhancedLevel = enhancedLevel;
    }

    @Nullable
    @Override
    public ItemStack getRecordedBow() {
        return recordedBow;
    }

    @Override
    public void setRecordedBow(ItemStack bow) {
        this.recordedBow = bow;
    }
}
