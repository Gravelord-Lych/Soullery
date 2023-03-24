package lych.soullery.mixin.client;

import lych.soullery.client.ModRenderTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedMap;

@Mixin(RenderTypeBuffers.class)
public abstract class RenderTypeBuffersMixin {
    @Shadow @Final private SortedMap<RenderType, BufferBuilder> fixedBuffers;

    @Shadow
    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, RenderType type) {}

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void initModRenderTypes(CallbackInfo ci) {
        castAndPut(fixedBuffers, ModRenderTypes.ARMOR_ENTITY_SOUL_GLINT);
        castAndPut(fixedBuffers, ModRenderTypes.ARMOR_SOUL_GLINT);
        castAndPut(fixedBuffers, ModRenderTypes.ENTITY_SOUL_GLINT);
        castAndPut(fixedBuffers, ModRenderTypes.ENTITY_SOUL_GLINT_DIRECT);
        castAndPut(fixedBuffers, ModRenderTypes.SOUL_GLINT);
        castAndPut(fixedBuffers, ModRenderTypes.SOUL_GLINT_DIRECT);
        castAndPut(fixedBuffers, ModRenderTypes.SOUL_GLINT_TRANSLUCENT);
    }

    private static void castAndPut(SortedMap<RenderType, BufferBuilder> map, RenderType type) {
        put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>) map, type);
    }
}
