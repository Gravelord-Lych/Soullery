package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.entity.functional.SoulBoltEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulBoltRenderer extends EntityRenderer<SoulBoltEntity> {
    public SoulBoltRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(SoulBoltEntity bolt, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.lightning());
        Matrix4f mat = stack.last().pose();

        for (int i = 0; i < 4; ++i) {
            int l = 7;
            float x = 0;
            float y = 0;
            for (int k = l; k >= 0; --k) {
                float x2Offs = 0.1F + (float) i * 0.2F;
                x2Offs = (float) ((double) x2Offs * ((double) k * 0.1D + 1.0D));
                float xOffs = 0.1F + (float) i * 0.2F;
                xOffs *= (float) (k - 1) * 0.1F + 1.0F;
                float r = 0.5f;
                float g = 0.9f;
                float b = 0.95f;
                quad(mat, builder, x, y, k, x, y, r, g, b, x2Offs, xOffs, false, false, true, false);
                quad(mat, builder, x, y, k, x, y, r, g, b, x2Offs, xOffs, true, false, true, true);
                quad(mat, builder, x, y, k, x, y, r, g, b, x2Offs, xOffs, true, true, false, true);
                quad(mat, builder, x, y, k, x, y, r, g, b, x2Offs, xOffs, false, true, false, false);
            }
        }
    }

    private static void quad(Matrix4f mat, IVertexBuilder builder, float x, float z, int y, float x2, float z2, float r, float g, float b, float x2Offs, float xOffs, boolean positiveX, boolean positiveZ, boolean positiveX2, boolean positiveZ2) {
        builder.vertex(mat, x + (positiveX ? xOffs : -xOffs), (float) (y * 16), z + (positiveZ ? xOffs : -xOffs)).color(r, g, b, 0.3F).endVertex();
        builder.vertex(mat, x2 + (positiveX ? x2Offs : -x2Offs), (float) ((y + 1) * 16), z2 + (positiveZ ? x2Offs : -x2Offs)).color(r, g, b, 0.3F).endVertex();
        builder.vertex(mat, x2 + (positiveX2 ? x2Offs : -x2Offs), (float) ((y + 1) * 16), z2 + (positiveZ2 ? x2Offs : -x2Offs)).color(r, g, b, 0.3F).endVertex();
        builder.vertex(mat, x + (positiveX2 ? xOffs : -xOffs), (float) (y * 16), z + (positiveZ2 ? xOffs : -xOffs)).color(r, g, b, 0.3F).endVertex();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(SoulBoltEntity bolt) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}
