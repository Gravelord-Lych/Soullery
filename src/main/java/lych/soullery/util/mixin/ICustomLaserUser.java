package lych.soullery.util.mixin;

import lych.soullery.client.ModRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ICustomLaserUser {
    default RenderType getLaserRenderType() {
        return ModRenderTypes.DEFAULT_LASER;
    }
}
