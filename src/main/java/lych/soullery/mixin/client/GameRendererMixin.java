package lych.soullery.mixin.client;

import lych.soullery.client.shader.ModShaders;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private IResourceManager resourceManager;
    @Shadow @Final private static Logger LOGGER;
//  TODO: try not to use this
    @Shadow @Nullable private ShaderGroup postEffect;

    @Shadow private boolean effectActive;

    @Shadow public abstract void loadEffect(ResourceLocation p_175069_1_);

    @Shadow public abstract void render(float p_195458_1_, long p_195458_2_, boolean p_195458_4_);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;doEntityOutline()V", shift = At.Shift.AFTER))
    private void renderReversion(float partialTicks, long nanoSecond, boolean shouldTick, CallbackInfo ci) {
        if (minecraft.player != null) {
            if (postEffect == null) {
                if (((IEntityMixin) minecraft.player).isReversed()) {
                    postEffect = initReversionEffect();
                    if (postEffect != null) {
                        effectActive = true;
                        postEffect.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
                    }
                }
            } else if (!((IEntityMixin) minecraft.player).isReversed() && ModShaders.REVERSION.toString().equals(postEffect.getName())) {
                postEffect = null;
                effectActive = false;
            }
        }
    }

    @Nullable
    private ShaderGroup initReversionEffect() {
        try {
            Entity cameraEntity = minecraft.cameraEntity;
            if (!(cameraEntity instanceof PlayerEntity)) {
                return null;
            }
            return new ShaderGroup(minecraft.getTextureManager(), resourceManager, minecraft.getMainRenderTarget(), ModShaders.REVERSION);
        } catch (IOException e) {
            LOGGER.warn("Failed to initialize reversion shader", e);
            return null;
        }
    }
}
