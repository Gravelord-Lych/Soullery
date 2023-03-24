package lych.soullery.client.render.world.dimension;

import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulLandRenderInfo extends DimensionRenderInfo {
    public SoulLandRenderInfo() {
        super(Float.NaN, true, FogType.NONE, false, false);
    }

    @Override
    public Vector3d getBrightnessDependentFogColor(Vector3d colorVec, float time) {
        return colorVec.multiply(0.2, 0.38, 0.4);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }
}
