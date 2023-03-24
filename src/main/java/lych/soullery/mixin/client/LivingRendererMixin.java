package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.entity.iface.INoDeathRotationEntity;
import lych.soullery.entity.iface.INoRedOverlayEntity;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingRenderer.class)
public abstract class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    private LivingRendererMixin(EntityRendererManager manager) {
        super(manager);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "getOverlayCoords", at = @At(value = "HEAD"), cancellable = true)
    private static void getOverlayCoordsForNoRedOverlayEntities(LivingEntity entity, float whiteOverlayProgress, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof INoRedOverlayEntity) {
            cir.setReturnValue(OverlayTexture.pack(OverlayTexture.u(whiteOverlayProgress), OverlayTexture.v(false)));
        }
    }

    @Inject(
            method = "setupRotations",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;sqrt(F)F"
            ),
            cancellable = true)
    private void setUpRotationsForNoDeathRotationEntities(T entity, MatrixStack stack, float bob, float rot, float partialTicks, CallbackInfo ci) {
        if (entity instanceof INoDeathRotationEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "getRenderType", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void modifyRenderTypeIfIsHighlighted(T entity, boolean bodyVisible, boolean canBeSeen, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        if (((IEntityMixin) entity).getHighlightColor().isPresent()) {
            cir.setReturnValue(RenderType.outline(getTextureLocation(entity)));
        }
    }
}
