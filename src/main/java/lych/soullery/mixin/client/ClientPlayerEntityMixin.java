package lych.soullery.mixin.client;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lych.soullery.config.ConfigHelper;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.ExtraAbilityConstants;
import lych.soullery.util.mixin.IClientPlayerMixin;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements IPlayerEntityMixin, IClientPlayerMixin {
    @Shadow @Final protected Minecraft minecraft;
    @SuppressWarnings("UnusedAssignment")
    @Unique
    private float enhancedJumpFlag = -1;
    @Unique
    private IntSet itemVanishingSlots;

    private ClientPlayerEntityMixin(World world, BlockPos pos, float yRot, GameProfile profile) {
        super(world, pos, yRot, profile);
        throw new UnsupportedOperationException();
    }

    @ModifyVariable(method = "updateAutoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;hasEffect(Lnet/minecraft/potion/Effect;)Z"), ordinal = 7)
    private float modifyMaxJumpStrength(float f7) {
        if (hasExtraAbility(ExtraAbility.ENHANCED_AUTO_JUMP)) {
            enhancedJumpFlag = 0;
            return f7 * ExtraAbilityConstants.ENHANCED_AUTO_JUMP_MAX_JUMP_HEIGHT_MULTIPLIER;
        }
        return f7;
    }

//  The structure is valid!
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "updateAutoJump",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;autoJumpTime:I", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void recordAutoJumpStrength(float p_189810_1_, float p_189810_2_, CallbackInfo ci, Vector3d vector3d, Vector3d vector3d1, Vector3d vector3d2, float f, float f1, float f12, Vector3d vector3d12, Vector3d vector3d13, float f13, ISelectionContext iselectioncontext, BlockPos blockpos, BlockState blockstate, BlockState blockstate1, float f6, float f7, float f8, Vector3d vector3d4, float f9, float f10, AxisAlignedBB axisalignedbb, Vector3d lvt_19_1_, Vector3d vector3d5, Vector3d vector3d6, Vector3d vector3d7, Vector3d vector3d8, Vector3d vector3d9, Vector3d vector3d10, Iterator<?> iterator, float f11, float f14) {
        if (enhancedJumpFlag == 0) {
            enhancedJumpFlag = f14;
        }
    }

    @Override
    public float getEnhancedJumpStrength() {
        if (enhancedJumpFlag > 0) {
            float tmp = enhancedJumpFlag;
            enhancedJumpFlag = -1;
            return tmp;
        }
        return 0;
    }

    @Override
    public IntSet getItemVanishingSlots() {
        if (itemVanishingSlots == null) {
            itemVanishingSlots = new IntArraySet();
        }
        return itemVanishingSlots;
    }

    @Inject(method = "onSyncedDataUpdated", at = @At("RETURN"))
    private void handleMindOperation(DataParameter<?> data, CallbackInfo ci) {
        if (getOperatingMobData().equals(data)) {
            int mob = entityData.get(getOperatingMobData());
            if (mob == -1 && ConfigHelper.doFirstPersonReset()) {
                Minecraft.getInstance().options.setCameraType(PointOfView.FIRST_PERSON);
            }
            if (mob != -1 && ConfigHelper.doThirdPersonSet()) {
                Minecraft.getInstance().options.setCameraType(PointOfView.THIRD_PERSON_BACK);
            }
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canElytraFly(Lnet/minecraft/entity/LivingEntity;)Z", remap = false))
    private boolean canFlyWithExa(ItemStack instance, LivingEntity entity) {
        if (entity instanceof PlayerEntity && ExtraAbility.FLYER.isOn((PlayerEntity) entity)) {
            return true;
        }
        return instance.canElytraFly(entity);
    }
}
