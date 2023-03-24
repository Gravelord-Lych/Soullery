package lych.soullery.client.render.world.sky;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IWorldRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ESVSkyRenderer implements ISkyRenderHandler {
    private static final RangedInteger FLASH_INTERVAL_RANGE = RangedInteger.of(20 * 25, 20 * 50);
    private static final RangedInteger FLASH_DURATION_RANGE = RangedInteger.of(20 * 6, 20 * 9);
    private static final float[] COLOR_HSB = new float[] {0.5555556f, 0.8f, 0.6f};
    private final VertexFormat vertexBufferFormat = DefaultVertexFormats.POSITION;
    private int nextFlashTimeRemaining;
    private int flashCount;
    private int flashWarmup;
    private int prevTick;
    private Color cachedColorO;
    private Color cachedColor;
    private boolean initialized;
    @Nullable
    private Color color;

    @SuppressWarnings("deprecation")
    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld level, Minecraft mc) {
        WorldRenderer renderer = mc.levelRenderer;
        RenderSystem.disableTexture();
        color = updateColor(ticks, partialTicks, level);
        Vector3d vec3d = getColorVec();
        float x = (float) vec3d.x;
        float y = (float) vec3d.y;
        float z = (float) vec3d.z;

        setupFogColor(x, y, z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(x, y, z);

        ((IWorldRendererMixin) renderer).getSkyBuffer().bind();
        vertexBufferFormat.setupBufferState(0L);
        ((IWorldRendererMixin) renderer).getSkyBuffer().draw(matrixStack.last().pose(), 7);
        VertexBuffer.unbind();
        vertexBufferFormat.clearBufferState();
        RenderSystem.disableFog();

        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        matrixStack.popPose();
        RenderSystem.color3f(0.0F, 0.0F, 0.0F);

        double d0 = mc.player.getEyePosition(partialTicks).y - 30;
        if (d0 < 0.0D) {
            matrixStack.pushPose();
            matrixStack.translate(0.0F, 12.0F, 0.0F);
            ((IWorldRendererMixin) renderer).getSkyBuffer().bind();
            vertexBufferFormat.setupBufferState(0L);
            ((IWorldRendererMixin) renderer).getSkyBuffer().draw(matrixStack.last().pose(), 7);
            VertexBuffer.unbind();
            vertexBufferFormat.clearBufferState();
            matrixStack.popPose();
            float f19 = -((float) (d0 + 65.0D));
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.vertex(-1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            tessellator.end();
        }
        if (level.effects().hasGround()) {
            RenderSystem.color3f(x * 0.2F + 0.04F, y * 0.2F + 0.04F, z * 0.6F + 0.1F);
        } else {
            RenderSystem.color3f(x, y, z);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    private void setupFogColor(float fogRed, float fogGreen, float fogBlue) {
        FogRenderer.levelFogColor();
    }

    public Vector3d getColorVec() {
        if (color == null) {
            return Vector3d.ZERO;
        }
        return new Vector3d(color.getRed() / 255d, color.getGreen() / 255d, color.getBlue() / 255d);
    }

    protected Color updateColor(int ticks, float partialTicks, ClientWorld world) {
        if (ticks == prevTick && cachedColor != null) {
            return getCachedColor(partialTicks);
        }
        prevTick = ticks;
        cachedColorO = cachedColor;
        cachedColor = getColorPerTick(world);
        if (cachedColorO == null) {
            cachedColorO = cachedColor;
        }
        return getCachedColor(partialTicks);
    }

    protected Color getCachedColor(float partialTicks) {
        return Utils.lerpColor(partialTicks, cachedColorO, cachedColor);
    }

    protected void begin(BufferBuilder builder) {
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    }

    private Color getColorPerTick(ClientWorld world) {
        Random random = world.random;
        if (!initialized) {
            nextFlashTimeRemaining = getRandomFlashTickInterval(random);
            initialized = true;
        }
        if (nextFlashTimeRemaining > 0) {
            nextFlashTimeRemaining--;
            return Color.BLACK;
        } else {
            if (flashCount == 0) {
                flashCount = getRandomFlashTickDuration(random);
            }
            if (flashWarmup == 0) {
                flashWarmup = flashCount - 20;
            }
            int flashCooldown = 20;
            int flashEdgeTime = Math.max(Math.max(flashCount - flashWarmup, flashCooldown - flashCount), 0);
            flashCount--;
            if (flashCount == 0) {
                flashWarmup = 0;
                nextFlashTimeRemaining = getRandomFlashTickInterval(random);
            }
            return getFlashColor(flashEdgeTime);
        }
    }

    public void flash() {
        flashCount = Math.min(flashCount, 1);
    }

    private static Color getFlashColor(int flashEdgeTime) {
        float flashValue = Utils.fade((20 - flashEdgeTime) / 20f);
        float hue = COLOR_HSB[0];
        float minSaturation = 0;
        float maxSaturation = COLOR_HSB[1];
        float saturation = MathHelper.lerp(flashValue, minSaturation, maxSaturation);
        float minBrightness = 0;
        float maxBrightness = COLOR_HSB[2];
        float brightness = MathHelper.lerp(flashValue, minBrightness, maxBrightness);
        return new Color(Color.HSBtoRGB(hue, saturation, brightness));
    }

    private static int getRandomFlashTickInterval(Random random) {
        return FLASH_INTERVAL_RANGE.randomValue(random);
    }

    private static int getRandomFlashTickDuration(Random random) {
        return FLASH_DURATION_RANGE.randomValue(random);
    }
}
