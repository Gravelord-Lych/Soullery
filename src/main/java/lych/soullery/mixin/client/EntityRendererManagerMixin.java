package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRendererManager.class)
public abstract class EntityRendererManagerMixin {
    @Inject(method = "renderFlame", at = @At("HEAD"), cancellable = true)
    private void cancelEarly(MatrixStack stack, IRenderTypeBuffer buffer, Entity entity, CallbackInfo ci) {
        if (!((IEntityMixin) entity).getFireOnSelf().isRealFire()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderFlame", ordinal = 0, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;pushPose()V"))
    private TextureAtlasSprite renderSoulFlame0(TextureAtlasSprite sprite, MatrixStack stack, IRenderTypeBuffer buffer, Entity entity) {
        return ((IEntityMixin) entity).getFireOnSelf().getFireOverlays().getFirst().sprite();
    }

    @ModifyVariable(method = "renderFlame", ordinal = 1, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;pushPose()V"))
    private TextureAtlasSprite renderSoulFlame1(TextureAtlasSprite sprite, MatrixStack stack, IRenderTypeBuffer buffer, Entity entity) {
        return ((IEntityMixin) entity).getFireOnSelf().getFireOverlays().getSecond().sprite();
    }
}
