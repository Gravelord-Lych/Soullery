package lych.soullery.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.util.function.BiFunction;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SimpleTexturedRenderer<T extends Entity> extends EntityRenderer<T> {
    private final Function<? super T, ? extends ResourceLocation> locationGetter;
    private final BiFunction<? super T, ? super ResourceLocation, ? extends RenderType> typeGetter;

    public SimpleTexturedRenderer(EntityRendererManager manager, Function<? super T, ? extends ResourceLocation> locationGetter, BiFunction<? super T, ? super ResourceLocation, ? extends RenderType> typeGetter) {
        super(manager);
        this.locationGetter = locationGetter;
        this.typeGetter = typeGetter;
    }

    public static <T extends Entity> IRenderFactory<T> single(ResourceLocation location) {
        return create(t -> location, (t, l) -> RenderType.entityCutoutNoCull(l));
    }

    public static <T extends Entity> IRenderFactory<T> fixedRenderType(Function<? super T, ? extends ResourceLocation> locationGetter, Function<? super ResourceLocation, ? extends RenderType> typeGetter) {
        return manager -> new SimpleTexturedRenderer<>(manager, locationGetter, (t, l) -> typeGetter.apply(l));
    }

    public static <T extends Entity> IRenderFactory<T> create(Function<? super T, ? extends ResourceLocation> locationGetter, BiFunction<? super T, ? super ResourceLocation, ? extends RenderType> typeGetter) {
        return manager -> new SimpleTexturedRenderer<>(manager, locationGetter, typeGetter);
    }

    @Override
    protected int getBlockLightLevel(T t, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(T t, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        stack.pushPose();
        stack.scale(1, 1, 1);
        stack.mulPose(entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        MatrixStack.Entry entry = stack.last();
        Matrix4f mat4 = entry.pose();
        Matrix3f mat3 = entry.normal();
        IVertexBuilder builder = buffer.getBuffer(typeGetter.apply(t, getTextureLocation(t)));
        vertex(builder, mat4, mat3, packedLight, 0, 0, 0, 1);
        vertex(builder, mat4, mat3, packedLight, 1, 0, 1, 1);
        vertex(builder, mat4, mat3, packedLight, 1, 1, 1, 0);
        vertex(builder, mat4, mat3, packedLight, 0, 1, 0, 0);
        stack.popPose();
        super.render(t, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    private static void vertex(IVertexBuilder builder, Matrix4f mat4, Matrix3f mat3, int packedLight, float x, float y, int u, int v) {
        builder.vertex(mat4, x - 0.5f, y - 0.25f, 0).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(mat3, 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return locationGetter.apply(t);
    }
}
