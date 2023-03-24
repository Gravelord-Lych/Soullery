package lych.soullery.client.render.world.sky;

import lych.soullery.Soullery;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;

@OnlyIn(Dist.CLIENT)
public class ModSkyRenderers {
    public static final ISkyRenderHandler ESV = new ESVSkyRenderer();

    private static ResourceLocation make(String name) {
        return Soullery.prefixTex(String.format("environment/%s_sky.png", name));
    }
}
