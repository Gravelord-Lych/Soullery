package lych.soullery.client.render.world.sky;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lych.soullery.util.mixin.IWorldRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class SimpleSkyRenderer implements ISkyRenderHandler {
    @Nullable
    protected final ResourceLocation skyLocation;
    protected Color color;
    private VertexFormat vertexBufferFormat = DefaultVertexFormats.POSITION;

    public SimpleSkyRenderer(@Nullable ResourceLocation skyLocation, @Nullable Color initialColor) {
        this.skyLocation = skyLocation;
        this.color = initialColor;
    }

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
        renderSky(ticks, partialTicks, matrixStack, world, mc, mc.getTextureManager());
    }

    @SuppressWarnings("deprecation")
    private void renderSky(int ticks, float partialTicks, MatrixStack stack, ClientWorld world, Minecraft mc, TextureManager manager) {
        RenderSystem.enableFog();
        Color color = getColor(ticks, partialTicks, world, mc);
        RenderSystem.color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        FogRenderer.levelFogColor();
        ((IWorldRendererMixin) mc.levelRenderer).getSkyBuffer().bind();
        vertexBufferFormat.setupBufferState(0L);
        ((IWorldRendererMixin) mc.levelRenderer).getSkyBuffer().draw(stack.last().pose(), 7);
        VertexBuffer.unbind();
        vertexBufferFormat.clearBufferState();

        RenderSystem.disableFog();
        if (skyLocation == null) {
            return;
        }
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        manager.bind(skyLocation);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        for (int i = 0; i < 6; ++i) {
            stack.pushPose();
            switch (i) {
                case 1:
                    stack.mulPose(Vector3f.XP.rotationDegrees(90));
                    break;
                case 2:
                    stack.mulPose(Vector3f.XP.rotationDegrees(-90));
                    break;
                case 3:
                    stack.mulPose(Vector3f.XP.rotationDegrees(180));
                    break;
                case 4:
                    stack.mulPose(Vector3f.ZP.rotationDegrees(90));
                    break;
                case 5:
                    stack.mulPose(Vector3f.ZP.rotationDegrees(-90));
                    break;
            }
            Matrix4f matrix4f = stack.last().pose();
            begin(builder);
            doRenderTex(color, builder, matrix4f);
            tessellator.end();
            stack.popPose();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    protected void doRenderTex(Color color, BufferBuilder builder, Matrix4f matrix4f) {
        builder.vertex(matrix4f, -100, -100, -100).uv(0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        builder.vertex(matrix4f, -100, -100, 100).uv(0, 16).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        builder.vertex(matrix4f, 100, -100, 100).uv(16, 16).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        builder.vertex(matrix4f, 100, -100, -100).uv(16, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
    }

    @SuppressWarnings("deprecation")
    protected void begin(BufferBuilder builder) {
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    }

    protected Color getColor(int ticks, float partialTicks, ClientWorld world, Minecraft mc) {
        return color;
    }
}
