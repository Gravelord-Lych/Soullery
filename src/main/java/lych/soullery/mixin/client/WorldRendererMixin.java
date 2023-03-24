package lych.soullery.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.client.LaserRenderingManager;
import lych.soullery.client.ModRenderTypes;
import lych.soullery.util.mixin.IEntityMixin;
import lych.soullery.util.mixin.IWorldRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements IWorldRendererMixin {
    @Shadow
    @Final
    private RenderTypeBuffers renderBuffers;

    @Shadow
    @Nullable
    private VertexBuffer skyBuffer;
    @Shadow
    private ClientWorld level;
    @Shadow
    private int ticks;

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract boolean shouldShowEntityOutlines();

    @Shadow @Nullable protected abstract Particle addParticleInternal(IParticleData particle, boolean alwaysVisible, boolean canDecreaseStatus, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

    @Unique
    private final LaserRenderingManager laserManager = new LaserRenderingManager(() -> level, () -> ticks);
    @Unique
    private boolean forceShowGlowing;

    @Inject(method = "renderLevel",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/RenderType;entityGlintDirect()Lnet/minecraft/client/renderer/RenderType;")),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;endBatch(Lnet/minecraft/client/renderer/RenderType;)V",
                    ordinal = 1
            ),
            require = 0)
    private void renderSoulGlints(MatrixStack stack, float partialTicks, long nanoTime, boolean shouldRenderBlockOutline, ActiveRenderInfo info, GameRenderer renderer, LightTexture texture, Matrix4f matrix4f, CallbackInfo ci) {
        renderBuffers.bufferSource().endBatch(ModRenderTypes.ARMOR_SOUL_GLINT);
        renderBuffers.bufferSource().endBatch(ModRenderTypes.ARMOR_ENTITY_SOUL_GLINT);
        renderBuffers.bufferSource().endBatch(ModRenderTypes.SOUL_GLINT);
        renderBuffers.bufferSource().endBatch(ModRenderTypes.SOUL_GLINT_DIRECT);
        renderBuffers.bufferSource().endBatch(ModRenderTypes.SOUL_GLINT_TRANSLUCENT);
        renderBuffers.bufferSource().endBatch(ModRenderTypes.ENTITY_SOUL_GLINT);
        renderBuffers.bufferSource().endBatch(ModRenderTypes.ENTITY_SOUL_GLINT_DIRECT);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature") // It's valid
    @ModifyVariable(method = "renderLevel",
                    at = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/OutlineLayerBuffer;endOutlineBatch()V",
                            shift = At.Shift.AFTER
                    ),
                    ordinal = 3)
    private boolean forceShowGlowing(boolean glowing) {
        if (forceShowGlowing) {
            forceShowGlowing = false;
            return true;
        }
        return glowing;
    }

    @ModifyVariable(method = "renderEntity", at = @At(value = "HEAD"), argsOnly = true)
    private IRenderTypeBuffer renderCustomOutline(IRenderTypeBuffer buffer, Entity entity) {
        if (shouldShowEntityOutlines() && ((IEntityMixin) entity).getHighlightColor().isPresent()) {
            OutlineLayerBuffer outlineBuffer = renderBuffers.outlineBufferSource();
            Color color = ((IEntityMixin) entity).getHighlightColor().get();
            int r, g, b, alpha = 255;
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            outlineBuffer.setColor(r, g, b, alpha);
            forceShowGlowing = true;
            return outlineBuffer;
        }
        return buffer;
    }

    @Nullable
    @Override
    public VertexBuffer getSkyBuffer() {
        return skyBuffer;
    }

    @Override
    public LaserRenderingManager getLaserRenderingManager() {
        return laserManager;
    }

    @Nullable
    @Override
    public Particle callAddParticleInternal(IParticleData particle, boolean alwaysVisible, boolean canDecreaseStatus, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return addParticleInternal(particle, alwaysVisible, canDecreaseStatus, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
