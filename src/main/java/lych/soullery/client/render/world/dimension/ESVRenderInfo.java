package lych.soullery.client.render.world.dimension;

import lych.soullery.client.render.world.sky.ESVSkyRenderer;
import lych.soullery.client.render.world.sky.ModSkyRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ESVRenderInfo extends DimensionRenderInfo {
    public ESVRenderInfo() {
        super(Float.NaN, false, FogType.NONE, true, false);
        setSkyRenderHandler(ModSkyRenderers.ESV);
    }

    @Override
    public Vector3d getBrightnessDependentFogColor(Vector3d colorVec, float time) {
        if (getSkyRenderHandler() == null) {
            return Vector3d.ZERO;
        }
        return getSkyRenderHandler().getColorVec().scale(0.8);
    }

    @Nullable
    @Override
    public ESVSkyRenderer getSkyRenderHandler() {
        return (ESVSkyRenderer) super.getSkyRenderHandler();
    }

    public static boolean flash() {
        if (Minecraft.getInstance().level != null) {
            if (Minecraft.getInstance().level.effects() instanceof ESVRenderInfo) {
                return ((ESVRenderInfo) Minecraft.getInstance().level.effects()).doFlash();
            }
        }
        return false;
    }

    public boolean doFlash() {
        if (getSkyRenderHandler() != null) {
            getSkyRenderHandler().flash();
            return true;
        }
        return false;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }
}
