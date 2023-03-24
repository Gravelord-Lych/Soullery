package lych.soullery.client;

import lych.soullery.Soullery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

import static net.minecraft.client.renderer.RenderType.create;

@OnlyIn(Dist.CLIENT)
public final class ModRenderTypes extends RenderState {
    public static final ResourceLocation SOUL_ENCHANT_GLINT = Soullery.prefixTex("misc/soul_enchanted_item_glint.png");
    public static final RenderType ARMOR_SOUL_GLINT = create("armor_soul_glint", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.builder().setTextureState(new TextureState(SOUL_ENCHANT_GLINT, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    public static final RenderType ARMOR_ENTITY_SOUL_GLINT = create("armor_entity_soul_glint", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.builder().setTextureState(new TextureState(SOUL_ENCHANT_GLINT, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    public static final RenderType DEFAULT_LASER = laser(3);
    public static final RenderType SOUL_GLINT_TRANSLUCENT = create("soul_glint_translucent", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.builder().setTextureState(new TextureState(SOUL_ENCHANT_GLINT, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
    public static final RenderType SOUL_GLINT = create("soul_glint", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.builder().setTextureState(new TextureState(SOUL_ENCHANT_GLINT, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    public static final RenderType SOUL_GLINT_DIRECT = create("soul_glint_direct", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.builder().setTextureState(new TextureState(SOUL_ENCHANT_GLINT, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    public static final RenderType ENTITY_SOUL_GLINT = create("entity_soul_glint", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.builder().setTextureState(new TextureState(SOUL_ENCHANT_GLINT, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
    public static final RenderType ENTITY_SOUL_GLINT_DIRECT = create("entity_soul_glint_direct", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, RenderType.State.builder().setTextureState(new TextureState(SOUL_ENCHANT_GLINT, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));

    private ModRenderTypes(String name, Runnable setupState, Runnable clearState) {
        super(name, setupState, clearState);
        throw new UnsupportedOperationException();
    }

    public static RenderType laser(double width) {
        return create("laser",
                DefaultVertexFormats.POSITION_COLOR,
                GL11.GL_LINES,
                256,
                RenderType.State.builder()
                        .setLineState(line(width))
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setOutputState(ITEM_ENTITY_TARGET)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .createCompositeState(false));
    }

    private static LineState line(double width) {
        return new LineState(OptionalDouble.of(Math.min(width, Minecraft.getInstance().getWindow().getWidth() / 1920.0 * width)));
    }
}
