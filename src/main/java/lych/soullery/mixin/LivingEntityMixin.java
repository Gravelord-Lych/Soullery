package lych.soullery.mixin;

import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.entity.ModAttributes;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.extension.soulpower.reinforce.FishReinforcement;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.util.BoundingBoxUtils;
import lych.soullery.util.ExtraAbilityConstants;
import lych.soullery.util.ModConstants;
import lych.soullery.util.Vectors;
import lych.soullery.util.mixin.IClientPlayerMixin;
import lych.soullery.util.mixin.ILivingEntityMixin;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityMixin {
    @Shadow public abstract ItemStack getMainHandItem();

    @Override
    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow public abstract double getAttributeValue(Attribute p_233637_1_);

    @Shadow public abstract BlockState getFeetBlockState();

    @Override
    @Shadow public abstract void lookAt(EntityAnchorArgument.Type p_200602_1_, Vector3d p_200602_2_);

    @Shadow public abstract boolean hasEffect(Effect p_70644_1_);

    @Shadow @Nullable public abstract EffectInstance getEffect(Effect p_70660_1_);

    @Shadow protected abstract boolean onSoulSpeedBlock();

    @Shadow public abstract ItemStack eat(World p_213357_1_, ItemStack p_213357_2_);

    private long sheepReinforcementTickCount;
    @Unique
    private long sheepReinforcementLastHurtByTimestamp;

    /**
     * Knockup strength will only be used when the entity is knockbacked
     */
    private double knockupStrength;

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "createLivingAttributes", at = @At(value = "RETURN"), cancellable = true)
    private static void createJumpStrengthAttribute(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
        cir.setReturnValue(cir.getReturnValue().add(ModAttributes.JUMP_STRENGTH.get()));
    }

    @ModifyConstant(method = "getJumpPower", constant = @Constant(floatValue = 0.42f))
    private float useJumpStrengthAttributeValue(float constant) {
        return (float) getAttributeValue(ModAttributes.JUMP_STRENGTH.get());
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyVariable(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasEffect(Lnet/minecraft/potion/Effect;)Z"), ordinal = 0)
    private float enhancedJump(float f) {
        if ((Object) this instanceof ClientPlayerEntity) {
            float strength = ((IClientPlayerMixin) this).getEnhancedJumpStrength();
            if (strength > 0) {
                return f + ExtraAbilityConstants.ENHANCED_AUTO_JUMP_COEFFICIENT * (strength - 1);
            }
        }
        return f;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void tickSheepReinforcement(CallbackInfo ci) {
        sheepReinforcementTickCount = tickCount;
    }

    @Override
    public long getSheepReinforcementLastHurtByTimestamp() {
        return sheepReinforcementLastHurtByTimestamp;
    }

    @Override
    public void setSheepReinforcementLastHurtByTimestamp(long sheepReinforcementLastHurtByTimestamp) {
        this.sheepReinforcementLastHurtByTimestamp = sheepReinforcementLastHurtByTimestamp;
    }

    @Inject(method = "hurt", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;actuallyHurt(Lnet/minecraft/util/DamageSource;F)V")))
    private void postHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        MinecraftForge.EVENT_BUS.post(new PostLivingHurtEvent((LivingEntity) (Object) this, source, amount, cir.getReturnValueZ()));
    }

    @ModifyVariable(method = "decreaseAirSupply", at = @At(value = "CONSTANT", args = {"intValue=0", "expandZeroConditions=GREATER_THAN_ZERO"}, ordinal = 0, shift = At.Shift.BEFORE), ordinal = 1)
    private int modifyDecreaseAirSupplyProbability(int i) {
        int ex = FishReinforcement.getFishReinforcementLevel(getArmorSlots());
        return i + ex;
    }

    @Inject(method = "knockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setDeltaMovement(DDD)V", shift = At.Shift.AFTER))
    private void onKnockback(float strength, double ratioX, double ratioZ, CallbackInfo ci) {
        if (getKnockupStrength() > 0) {
            push(0, getKnockupStrength(), 0);
            setKnockupStrength(0);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void saveMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        compoundNBT.putLong("SheepReinforcementTickCount", getSheepReinforcementTickCount());
        compoundNBT.putLong("SheepReinforcementLastHurtByTimestamp", getSheepReinforcementLastHurtByTimestamp());
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void loadMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        setSheepReinforcementLastHurtByTimestamp(compoundNBT.getLong("SheepReinforcementLastHurtByTimestamp"));
        sheepReinforcementTickCount = compoundNBT.getLong("SheepReinforcementTickCount");
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantValue"})
    @ModifyVariable(method = "onClimbable", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z", ordinal = 0))
    private Optional<BlockPos> handleClimberExa(Optional<BlockPos> old) {
        if (old.isPresent()) {
            return old;
        }
        if ((Object) this instanceof PlayerEntity && ExtraAbility.CLIMBER.isOn((PlayerEntity) (Object) this)) {
            if (isShiftKeyDown()) {
                return old;
            }
//          Handle blocks like fences
            if (getFeetBlockState().getMaterial().blocksMotion() || getBlockHeightAt(blockPosition().below()) > 1) {
                return Optional.of(blockPosition());
            }
            Vector3d lookAngle = Vectors.copyWithoutY(getLookAngle()).normalize();
            if (Vector3d.ZERO.equals(lookAngle)) {
                return old;
            }
            BlockPos pos = new BlockPos(blockPosition().offset(calculateLadderPos(lookAngle)));
            if (((Object) this) instanceof ClientPlayerEntity) {
                boolean autoJumpEnabled = ((ClientPlayerEntity) (Object) this).isAutoJumpEnabled();
                if (autoJumpEnabled && level.getBlockState(blockPosition().below()).getMaterial().blocksMotion() && canAutoJumpOver(pos)) {
                    return old;
                }
            }
            if (level.getBlockState(pos).getMaterial().blocksMotion()) {
                return Optional.of(blockPosition());
            }
            return old;
        }
        return old;
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    private void handleBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        if (onSoulSpeedBlock() && Reinforcements.SOUL_RABBIT.getTotalLevel(getArmorSlots()) > 0) {
            cir.setReturnValue(1f);
        }
    }

    @ModifyVariable(method = "tryAddSoulSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onSoulSpeedBlock()Z", shift = At.Shift.BY, by = -3))
    private int handleSoulSpeed(int i) {
        return Math.min(i + Reinforcements.SOUL_RABBIT.getTotalLevel(getArmorSlots()), ModConstants.MAX_SOUL_SPEED);
    }

    @SuppressWarnings("ConstantValue")
    @Redirect(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canElytraFly(Lnet/minecraft/entity/LivingEntity;)Z", remap = false))
    private boolean updateFallFlyingWithExa(ItemStack instance, LivingEntity entity) {
        if ((Object) this instanceof PlayerEntity && ExtraAbility.FLYER.isOn((PlayerEntity) (Object) this)) {
            return true;
        }
        return instance.canElytraFly(entity);
    }

    @SuppressWarnings("ConstantValue")
    @Redirect(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;elytraFlightTick(Lnet/minecraft/entity/LivingEntity;I)Z", remap = false))
    private boolean updateFlightItemWithExa(ItemStack instance, LivingEntity entity, int flightTicks) {
        if ((Object) this instanceof PlayerEntity && ExtraAbility.FLYER.isOn((PlayerEntity) (Object) this)) {
            if (instance.canElytraFly(entity)) {
                instance.elytraFlightTick(entity, flightTicks);
            }
            return true;
        }
        return instance.elytraFlightTick(entity, flightTicks);
    }

    private boolean canAutoJumpOver(BlockPos pos) {
        float jumpHeight = getPlayerJumpHeight();
        float requiredHeight = 0;
        while (level.getBlockState(pos).getMaterial().blocksMotion()) {
            requiredHeight++;
            pos = pos.above();
        }
        pos = pos.below();
        requiredHeight--;
        double height = getBlockHeightAt(pos);
        requiredHeight += height;
        return jumpHeight >= requiredHeight;
    }

    private double getBlockHeightAt(BlockPos pos) {
        return BoundingBoxUtils.minmax(level.getBlockState(pos).getCollisionShape(level, pos).toAabbs()).map(AxisAlignedBB::getYsize).orElse(0.0);
    }

    @SuppressWarnings("ConstantValue")
    private float getPlayerJumpHeight() {
        if (!(((Object) this) instanceof PlayerEntity)) {
            throw new UnsupportedOperationException();
        }
        float jumpHeight = 1.2f; // Player's default value
        if (hasEffect(Effects.JUMP)) {
            jumpHeight += (getEffect(Effects.JUMP).getAmplifier() + 1) * 0.75f;
        }
        if (ExtraAbility.ENHANCED_AUTO_JUMP.isOn((PlayerEntity) (Object) this)) {
            jumpHeight *= ExtraAbilityConstants.ENHANCED_AUTO_JUMP_MAX_JUMP_HEIGHT_MULTIPLIER;
        }
        return jumpHeight;
    }

    private static Vector3i calculateLadderPos(Vector3d lookAngle) {
        if (Math.abs(lookAngle.x) >= 0.5) {
            return lookAngle.x > 0 ? Direction.EAST.getNormal() : Direction.WEST.getNormal();
        }
        if (Math.abs(lookAngle.z) >= 0.5) {
            return lookAngle.z > 0 ? Direction.SOUTH.getNormal() : Direction.NORTH.getNormal();
        }
        throw new AssertionError();
    }

    @Override
    public double getKnockupStrength() {
        return knockupStrength;
    }

    @Override
    public void setKnockupStrength(double knockupStrength) {
        this.knockupStrength = knockupStrength;
    }

    @Override
    public long getSheepReinforcementTickCount() {
        return sheepReinforcementTickCount;
    }
}
