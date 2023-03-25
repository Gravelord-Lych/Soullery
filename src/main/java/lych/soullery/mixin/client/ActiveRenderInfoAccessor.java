package lych.soullery.mixin.client;

import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ActiveRenderInfo.class)
public interface ActiveRenderInfoAccessor {
    @Invoker
    void callSetPosition(double x, double y, double z);

    @Invoker
    void callMove(double forward, double y, double left);

    @Invoker
    double callGetMaxZoom(double amount);

    @Accessor
    void setDetached(boolean detached);
}
